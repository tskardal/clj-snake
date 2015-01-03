(ns battlesnake.snake
  (:require [reagent.core :as reagent :refer [atom]]))

(defn create-snake []
  {:body (take 15 (map vector (range) (repeat 5)))
   :dir :right
   :type :snake})

(defn render-snake [ctx snake]
  (set! (.-fillStyle ctx) "rgb(200, 0, 200)")
  (doseq [[x y] (:body snake)]
    (.fillRect ctx (* 10 x) (* 10 y) 10 10 10)))

(defn render [ctx]
  (set! (.-fillStyle ctx) "rgb(200, 0, 0)")
  (.fillRect ctx 10 10 55 50)
  (set! (.-fillStyle ctx) "rgba(0, 0, 200, 0.5)")
  (.fillRect ctx 30 30 55 50))

(defn ^:export init [canvas]
  (let [ctx (.getContext canvas "2d")
        the-snake (create-snake)]
    (render-snake ctx the-snake)))
