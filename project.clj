(defproject vip-feed-format-converter "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.xml "0.2.0-alpha2"]
                 [org.clojure/data.csv "0.1.4"]]
  :main ^:skip-aot vip-feed-format-converter.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :uberjar-name "vip-feed-format-converter.jar")
