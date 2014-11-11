(ns wordcount
  (:use [cascalog.api]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.string :as s]
            [cascalog.logic.def :as def]
            [cascalog.logic.ops :as c])
  (:import (org.apache.lucene.analysis TokenStream)
           (org.apache.lucene.analysis.en EnglishAnalyzer)
           (org.apache.lucene.analysis.tokenattributes CharTermAttribute)
           (java.io StringReader))
  (:gen-class))


(def/defmapcatfn tokenize [line]
                 "reads in a line of string and tokenizes it using a Lucene Analyzer"
                 (let [tokenStream (.tokenStream (EnglishAnalyzer.) "contents" (StringReader. line))
                       attributeType CharTermAttribute
                       term (.addAttribute tokenStream attributeType)]
                   (do (.reset tokenStream)
                       (loop [result []]
                         (if (.incrementToken tokenStream)
                           (recur (conj result (str term)))
                           (do (.close tokenStream) result))))))

(defn -main [in out & args]
  (?<- (hfs-delimited out)
       [?word ?count]
       ((hfs-delimited in :skip-header? true) ?line)
       (tokenize ?line :> ?word)
       (c/count ?count)))
