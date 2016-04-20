(ns signafire.core
  (:require [signafire.lib.config :as config]
            [signafire.components.webserver :as webserver]
            [signafire.components.database :as database]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as log :refer [spy info infof debug debugf error errorf]])
  (:gen-class))


(log/handle-uncaught-jvm-exceptions!
  (fn [throwable ^Thread thread]
    (errorf throwable "Uncaught exception on thread: %s"
            (.getName thread))
    (System/exit 1)))


(defn create-inert-app
  [config]
  (component/system-using (component/system-map :web-server (webserver/map->WebServer (merge (:web-server config)
                                                                                             {:environment (get-in (meta config) [:location :nomad/environment])}))
                                                :database (database/map->Database (:database config)))
                          {:web-server {:database :database}}))

(defn -main
  []
  (let [config (config/get-config)
        _ (debug "Running with configuration:" config)
        app-instance (create-inert-app config)]
    (component/start app-instance)))