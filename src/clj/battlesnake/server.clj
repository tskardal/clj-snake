(ns battlesnake.server
  (:gen-class)
  (:require [chord.http-kit :refer [wrap-websocket-handler]]
            [clojure.core.async :refer [<! >! put! close! go-loop]]
            [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [resources]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.reload :as reload]))

(def idseq (java.util.concurrent.atomic.AtomicInteger.))
(defn next-id [] (.getAndIncrement idseq))

(def games (atom {}))

(defn ws-handler [{:keys [ws-channel] :as req}]
  (println "Connection from " (:remote-addr req))
  (go-loop []
    (when-let [{:keys [message error] :as msg} (<! ws-channel)]
      (prn "Message received:" msg)
      (>! ws-channel (if error
                       (format "Error: '%s'." (pr-str msg))
                       {:received (format "You passed: '%s' at %s." (pr-str message) (java.util.Date.))}))
      (recur))))

(defn active-games []
  (filter #(= :active (:status %))(vals @games)))

(defn game-info [id]
  (get @games id))

(defn create-game [name]
  (let [id (next-id)]
    (println "creating " name)
    (swap! games assoc id {:id id :name name :status :active})))

(defroutes app
  (GET "/" [] (io/resource "public/index.html"))
  (GET "/ws" [] (-> ws-handler wrap-websocket-handler))
  (GET "/games" [] (active-games))
  (GET "/games/:id" [id] (io/resource "public/game.html"))
  (POST "/games" {{name :game-name} :body-params} (create-game name))
  (resources "/"))

(defn start-server []
  (-> (site #'app)
      (reload/wrap-reload)
      (wrap-restful-format)      
      (run-server {:port 3000}))
  (println "Server running!"))

(defn -main [& args]
  (start-server))
