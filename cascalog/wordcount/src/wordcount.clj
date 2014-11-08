(ns wordcount
  (:use [cascalog.api]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.string :as s]
            [cascalog.logic.def :as def]
            [cascalog.logic.ops :as c])
  (:gen-class))

(def/defmapcatfn tokenize [line]
  "reads in a line of string and tokenizes it by regex"
  (s/split line #"[\[\]\\\(\),.)\s]+"))

(defn -main [in out & args]
  (?<- (hfs-delimited out)
    [?word ?count]
    ((hfs-delimited in :skip-header? true) ?line)
    (tokenize ?line :> ?word)
    (c/count ?count)))
