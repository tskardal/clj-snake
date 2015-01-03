(ns battlesnake.server
  (:gen-class)
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [resources]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :as reload]))

(defroutes app
  (GET "/" [] (io/resource "public/index.html"))
  (resources "/"))

(defn start-server []
  (let [handler (reload/wrap-reload (site #'app))]
    (run-server handler {:port 3000})
    (println "Server running!")))

(defn -main [& args]
  (start-server))
