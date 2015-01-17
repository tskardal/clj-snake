(defproject battlesnake "0.1.0-SNAPSHOT"
  :description "A multiplayer snake game for the web"
  :url "http://github.com/tskardal"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[compojure "1.3.1"]
                 [http-kit "2.1.16"]
                 [jarohen/chord "0.4.2" :exclusions [org.clojure/clojure]]
                 [javax.servlet/servlet-api "2.5"]                
                 [org.clojure/clojure "1.6.0"]                 
                 [org.clojure/clojurescript "0.0-2496"]               
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [reagent "0.5.0-alpha"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-devel "1.3.2"]]
  :plugins [[lein-cljsbuild "1.0.4"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds
              [{;; CLJS source code path
                :source-paths ["src/cljs"]
                :jar true

                ;; Google Closure (CLS) options configuration
                :compiler {;; CLS generated JS script filename
                           :output-to "target/classes/public/js/battlesnake.js"
                           :output-dir "target/classes/public/js"
                           ;; :source-map "target/classes/public/js/battlesnake.js.map"

                           ;; minimal JS optimization directive
                           :optimizations :whitespace

                           ;; generated JS code prettyfication
                           :pretty-print true

                           ;; we'll be using react through Reagent
                           :preamble ["reagent/react.js"]}}]}
;  :profiles {:dev {:plugins [[com.cemerick/austin "0.1.5"]]}}
  :exclusions [org.clojure/clojure]
  :source-paths ["src/clj"]
  :main battlesnake.server
;  :aot [battlesnake.server]
  :repl-options {:timeout 120000})


