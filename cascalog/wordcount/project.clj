(defproject cascalog-workdcount-demo "0.0.1"
  :description "A Cascalog demo as a standalone Leiningen project"
  :min-lein-version "2.1.1"
  :repositories {"conjars" "http://conjars.org/repo/"}
  :uberjar-name "cascalog.jar"
  :aot :all
  :main wordcount
  :dependencies [[cascading/cascading-hadoop2-mr1 "2.6.1"]
                 [cascalog/cascalog "2.1.1" :exclusions [[cascading/cascading-hadoop]
                                                         [cascading/cascading-local]]]
                 ]
  :profiles {:provided {:dependencies [
                                       [org.apache.hadoop/hadoop-mapreduce-client-jobclient "2.5.1"]
                                       [org.apache.hadoop/hadoop-common "2.5.1"]
                                       ]}}
  )
