(ns battlesnake.server
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [resources]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :as reload]))

(defroutes app
  (GET "/home" [] (io/resource "public/index.html"))
  (resources "/"))

(defn start-server []
  (let [handler (reload/wrap-reload (site #'app))]
    (run-server handler {:port 3000})
    (println "Server running!")))

(defn -main [& args]
  (comment (start-server))
  (println "Hello there"))