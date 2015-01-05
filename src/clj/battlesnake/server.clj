(ns battlesnake.server
  (:gen-class)
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [resources]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :as reload]
            [chord.http-kit :refer [wrap-websocket-handler]]
            [clojure.core.async :refer [<! >! put! close! go-loop]]))

(defn ws-handler [{:keys [ws-channel] :as req}]
  (println "Connection from " (:remote-addr req))
  (go-loop []
    (when-let [{:keys [message error] :as msg} (<! ws-channel)]
      (prn "Message received:" msg)
      (>! ws-channel (if error
                       (format "Error: '%s'." (pr-str msg))
                       {:received (format "You passed: '%s' at %s." (pr-str message) (java.util.Date.))}))
      (recur))))

(defroutes app
  (GET "/" [] (io/resource "public/index.html"))
  (GET "/ws" [] (-> ws-handler wrap-websocket-handler))
  (resources "/"))

(defn start-server []
  (let [handler (reload/wrap-reload (site #'app))]
    (run-server handler {:port 3000})
    (println "Server running!")))

(defn -main [& args]
  (start-server))
