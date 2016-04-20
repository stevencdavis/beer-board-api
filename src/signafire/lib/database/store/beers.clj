(ns signafire.lib.database.store.beers
  (:require [clojure.java.jdbc :as j]))

(defn get-beers
  [conn]
  (j/query conn
           ["SELECT location, floor, type, foamy, flat, slow, warm, empty, modified
             FROM beers
             ORDER BY floor ASC"]))
