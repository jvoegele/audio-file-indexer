(defproject audio-file-indexer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [me.raynes/fs "1.4.6"]
                 [org/jaudiotagger "2.0.3"]
                 [org.clojure/data.json "0.2.6"]]
  :main ^:skip-aot audio-file-indexer.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
