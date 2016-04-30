(ns signafire.components.webserver
  (:require [signafire.resources :as resources]
            [signafire.middleware :as middleware]
            [com.stuartsierra.component :as component]
            [compojure.core :as compojure :refer [GET POST PUT PATCH DELETE OPTIONS]]
            [compojure.route :as route]                     ; form, query params decode; cookie; session, etc
            [compojure.handler :refer [site]]
            [liberator.core :refer [defresource]]
            [org.httpkit.server :as httpkit-server]
            [ring.middleware.cors :as cors]
            [ring.middleware.json :as ring-json]
            [ring.middleware.reload :as reload]
            [taoensso.timbre :as log :refer [spy info infof debug debugf error errorf]]))


;; Default 'options' resource to allow options requests on any route
(defresource options
             :allowed-methods [:options])

(defn app-routes
  "Returns the web handler function as a closure over the
  application component."
  [database]
  ;; Instead of static 'defroutes':
  (compojure/routes
    ;; Allow 'OPTIONS' request on all routes
    (OPTIONS "/*" [] options)

    (GET "/beers" {{:keys [] :or {}} :params} (resources/get-beers database))
    (PUT "/beers" [location floor type status foamy flat warm slow] (resources/update-beer database location floor type status foamy flat warm slow))

    (route/not-found (fn [ctx]                              ;; all other, return 404
                       (debugf "Bad request [%s] from %s" (:uri ctx) (:remote-addr ctx))
                       "<p>Page not found.</p>"))))


(defrecord WebServer [environment port max-body allowed-origins ; constructor fields
                      database                              ; injected dependencies
                      http-server                           ; runtime state
                      ]
  component/Lifecycle
  (start [this]
    (assoc this :http-server
                (try
                  (let [dev-mode (or (= :default environment)
                                     (= "test" environment))
                        handler (-> (app-routes database)
                                    (middleware/ignore-trailing-slash)
                                    (ring-json/wrap-json-params)
                                    (site)
                                    (cors/wrap-cors :access-control-allow-origin (mapv re-pattern allowed-origins)
                                                    :access-control-allow-credentials true
                                                    :access-control-allow-methods #{:get :put :post :patch :delete :options}
                                                    :access-control-allow-headers #{"origin"
                                                                                    "x-requested-with"
                                                                                    "content-type"
                                                                                    "accept"
                                                                                    "accept-encoding"
                                                                                    "dnt"}))
                        handler (if dev-mode
                                  (do
                                    (infof "Using hot code reload! (%s mode)" environment)
                                    (reload/wrap-reload handler))
                                  handler)]
                    (infof "Starting server on port %s" port)
                    (httpkit-server/run-server handler {:port port :max-body max-body}))
                  (catch Throwable t
                    (-> t (error) (throw))))))
  (stop [this]
    (when-not (nil? http-server)
      ;; graceful shutdown: wait 100ms for existing requests to be finished
      ;; :timeout is optional, when no timeout, stop immediately
      (http-server :timeout 100))
    this))