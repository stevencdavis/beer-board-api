(ns signafire.components.database
  (:require [com.stuartsierra.component :as component]
            [signafire.lib.database.connection :as connection]))


;; API



;; Component

(defrecord Database [subprotocol subname
                     connection]

  ;; Provides a connection to a mysql database

  component/Lifecycle
  (start [this]
    ;; initialize mysql connection pool
    (assoc this :connection (connection/create-connection {:subprotocol subprotocol
                                                           :subname subname})))

  (stop [this]
    (assoc this :connection nil)))