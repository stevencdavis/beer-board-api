(ns signafire.resources
  (:require [signafire.lib.database.store.beers :as beers]
            [liberator.core :refer [defresource]]
            [clojure.data.json :as json]
            [taoensso.timbre :refer [spy error]])
  (:import (java.sql Timestamp)))


;; Make java.sql.Timestamp objects JSON-writable
(extend-type Timestamp
  json/JSONWriter
  (-write [date out]
    (json/-write (str date) out)))


(def base-resource
  {:available-media-types ["application/json"]
   :handle-ok             (fn [ctx] (::data ctx))
   :new?                  (fn [ctx]
                            (::new? ctx))
   ;; Handle when the entity is created or updated (201)
   :handle-created        (fn [ctx]
                            (::data ctx))
   ;; Did not send the message (204)
   :handle-no-content     (fn [ctx]
                            (::data ctx))
   ;; otherwise, returns 200 when entity is removed
   :respond-with-entity?  true})


(defresource get-beers
             ;; Get a list of activities for a particular user
             [database]
             base-resource
             :allowed-methods [:get]
             :malformed? (fn [_]
                           (when (or (nil? "some param"))
                             true))
             :handle-ok (fn [_]
                          (try
                            (spy "Fetching beers!")
                            (let [beers (beers/get-beers (:connection database))]
                              {:meta    {}
                               :results beers})
                            (catch Throwable t
                              (-> t (error) (throw))))))

(defresource save-beer
             ;; Get a list of activities for a particular user
             [database]
             base-resource
             :allowed-methods [:put]
             :malformed? (fn [_]
                           (when (or (nil? "some param"))
                             true))
             :handle-ok (fn [_]
                          (try
                            (spy "Fetching beers!")
                            (let [beers (beers/get-beers (:connection database))]
                              {:meta    {}
                               :results beers})
                            (catch Throwable t
                              (-> t (error) (throw))))))
