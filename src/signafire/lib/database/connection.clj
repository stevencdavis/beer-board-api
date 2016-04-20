(ns signafire.lib.database.connection
  (:require [signafire.lib.config :as config])
  (:import com.mchange.v2.c3p0.ComboPooledDataSource))

;; Create a database connection pool rather than re-connecting for every operation
(defn pool
  "Creates a database connection pool"
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               ;(.setUser (:user spec))
               ;(.setPassword (:password spec))
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60))
               ;; test the connection every 5 minutes to remove expired/broken connections
               (.setIdleConnectionTestPeriod 300))]
    {:datasource cpds}))

(defn create-connection
  "Create a new connection pool and return it"
  [config]
  (pool config))