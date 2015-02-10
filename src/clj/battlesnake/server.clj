(ns battlesnake.server
  (:gen-class)
  (:require [battlesnake.game :as g]
            [chord.http-kit :refer [wrap-websocket-handler]]
            [clojure.core.async :refer [<! >! put! close! go-loop go]]
            [clojure.java.io :as io]
            [compojure.core :refer [defroutes routes GET POST]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [resources]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.reload :as reload]
            [snake :as s]))

(def idseq (java.util.concurrent.atomic.AtomicInteger.))
(defn next-id [] (.getAndIncrement idseq))

(def games (atom {}))

(defn join-game [id ws]
  (let [player-id (java.util.UUID/randomUUID)
        game (get @games id)]
    (swap! games assoc id (s/add-player game {:id player-id :ws ws}))    
    player-id))

(defmulti recv-cmd :cmd)

(defmethod recv-cmd :join [{id :id} ws-channel]
  (println "got join to game with id=" id)
  (go    
    (>! ws-channel {:type :joined :player-id (join-game id ws-channel) :game (get @games id)})    
    (g/start (get @games id))))

(defn test [a b]
  (println "a:" a)
  (when b
    (println "b: " b)))

(defn ws-handler [{:keys [ws-channel] :as req}]
  (println "Connection from " (:remote-addr req))
  (go-loop []
    (when-let [{:keys [message error] :as msg} (<! ws-channel)]      
      (prn "Message received:" message)      
      (when (:cmd message)
        (recv-cmd message ws-channel))
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
    (swap! games assoc id {:id id :name name :status :active :players []})
    ))

(defroutes app
  (GET "/" [] (io/resource "public/index.html"))  
  (GET "/games" [] (active-games))
  (GET "/games/:id" [id] (io/resource "public/game.html"))
  (POST "/games" {{name :game-name} :body-params} (create-game name))
  (resources "/"))

(defroutes ws
  (GET "/ws" [] (-> ws-handler wrap-websocket-handler)))

(def handler (routes (site (wrap-restful-format app)) ws))

(defn start-server []
  (-> handler
      (reload/wrap-reload)      
      (run-server {:port 3000}))
  (println "Server running!"))

(defn -main [& args]
  (start-server))
