(ns signafire.lib.database.store.beers
  (:require [clojure.java.jdbc :as j]))

(defn get-beers
  [conn]
  (j/query conn
           ["SELECT location, floor, type, foamy, flat, slow, warm, empty, modified
             FROM beers
             ORDER BY floor ASC"]))

(defn update-beer
  [conn location floor type empty foamy flat warm slow]
  (j/update! conn :beers
             {:type type
              :empty empty
              :foamy foamy
              :flat flat
              :warm warm
              :slow slow}
             ["location = ? AND floor = ?"
              location floor]))