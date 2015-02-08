(ns battlesnake.game
  (:require [clojure.core.async :refer [<! >! put! close! go-loop go timeout]]
            [snake :as s]))

(defn- notify-start [players game-id]
  (go
    (doseq [p players]
      (>! (:ws p) {:type :starting :game game-id}))
    (<! (timeout 3000))))

(defn start [game]
  (let [state (atom game)
        players (:players game)]    
    (notify-start players (:id game))
    (go-loop []
      (let [ug (s/tick @state)]
        ; TODO game over?
        (doseq [p players]
          (>! (:ws p) (str "updated game: " ug)))
        (reset! state ug)
        (<! (timeout 1000))
        (recur)))))
