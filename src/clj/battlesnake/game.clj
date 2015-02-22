(ns battlesnake.game
  (:require [clojure.core.async :refer [<! <!! >! put! close! go-loop go timeout]]
            [snake :as s]))

(defn- notify-start [conns game-id]
  (go
    (doseq [[_ ws] conns]
      (>! ws {:type :starting :game game-id}))
    (<!! (timeout 3000))))

(defn- prepare [{players :players}]
  (for [player players]
    (dissoc player :ws)))

(defn- connections [{players :players}]
  (apply merge
         (for [player players]
           {(:id player) (:ws player)})))

(defn start [game-info]
  (let [game (dissoc game-info :players)
        players (prepare game-info)
        conns (connections game-info)]    
    (notify-start conns (:id game-info))
    (go-loop [state (s/tick players)]
      ;; TODO game over?
      (println "tick")
      (doseq [[_ ws] conns]          
        (>! ws {:type :tick :game state}))      
      (<! (timeout 1000))     
      (recur (s/tick state)))))
