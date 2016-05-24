(ns signafire.resources
  (:require [signafire.lib.database.store.beers :as beers]
            [liberator.core :refer [defresource]]
            [clojure.data.json :as json]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [taoensso.timbre :refer [spy debug debugf error]])
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
                            (debug "Fetching beers!")
                            (let [beers (beers/get-beers (:connection database))]
                              {:meta    {}
                               :results beers})
                            (catch Throwable t
                              (-> t (error) (throw))))))

(defresource update-beer
             ;; Get a list of activities for a particular user
             [database location floor type status foamy flat warm slow]
             base-resource
             :allowed-methods [:put]
             :malformed? (fn [_]
                           (when (or (nil? database)
                                     (nil? location)
                                     (nil? floor))
                             true))
             :put! (fn [_]
                     (try
                       (let [modified (tc/to-sql-time (t/now))
                             beers (beers/update-beer (:connection database) location floor type status foamy flat warm slow modified)]
                         (debugf "Updating beer %s" {:location location
                                                     :floor floor
                                                     :type type
                                                     :status status
                                                     :foamy foamy
                                                     :flat flat
                                                     :warm warm
                                                     :slow slow
                                                     :modified modified})
                         (beers/log-action (:connection database) location floor type status foamy flat warm slow modified)
                         {::data {:meta    {}
                                  :results {:modified (tc/to-long modified)}}})
                       (catch Throwable t
                         (-> t (error) (throw))))))
