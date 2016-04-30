(ns signafire.lib.database.store.beers
  (:require [clojure.java.jdbc :as j]))

(defn get-beers
  [conn]
  (j/query conn
           ["SELECT location, floor, type, status, foamy, flat, slow, warm, modified
             FROM beers
             ORDER BY floor ASC"]))

(defn update-beer
  [conn location floor type status foamy flat warm slow modified]
  (j/update! conn :beers
             {:type     type
              :status   status
              :foamy    foamy
              :flat     flat
              :warm     warm
              :slow     slow
              :modified modified}
             ["location = ? AND floor = ?"
              location floor]))