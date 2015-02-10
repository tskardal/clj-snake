(ns battlesnake.snake
  (:require [reagent.core :as reagent :refer [atom]]))

(def dirs {37 [-1 0] ; left
           38 [0 -1] ; up
           39 [1 0]  ; right
           40 [0 1]}); down

(def width 500)
(def height 500)
(def block-size 10)
(def grid-width (/ width block-size))
(def grid-height (/ height block-size))

(def edibles (atom []))

(defn create-edible []
  {:color "rgb(0, 200, 0)"
   :location [(rand-int 50) (rand-int 50)]})

(defn render-snake [ctx snake]
  (set! (.-fillStyle ctx) "rgb(200, 0, 200)")
  (doseq [[x y] (:body snake)]
    (.fillRect ctx (* 10 x) (* 10 y) 10 10 10)))

(defn render-edibles [ctx edibles]
  (doseq [e edibles]
    (set! (.-fillStyle ctx) (:color e))
    (let [[x y] (:location e)]
      (.fillRect ctx (* 10 x) (* 10 y) 10 10 10))))

(defn render [ctx]
  (set! (.-fillStyle ctx) "rgb(200, 0, 0)")
  (.fillRect ctx 10 10 55 50)
  (set! (.-fillStyle ctx) "rgba(0, 0, 200, 0.5)")
  (.fillRect ctx 30 30 55 50))

(defn- add-points [& points]
  (vec (apply map + points)))

;; TODO move to cljx
(defn- crash? [loc {body :body}]
  (or
   (some #(= % loc) body)
   (let [[x y] loc]
     (or
      (< x 0)
      (>= x grid-width)
      (< y 0)
      (>= y grid-height)))))

(comment (defn- move-snake []  
           (let [h (first (:body @snake))        
                 dir (:dir @snake)
                 nh (add-points h dir)]
             (if (crash? nh @snake)
               (do
                 (js/alert "Game over!")
                 (reset! snake (create-snake))
                 (reset! edibles []))
               (if-let [e (seq (filter #(= nh (:location %)) @edibles))]      
                 (do
                   (swap! edibles #(filter (fn [x] (not= nh (:location x))) @edibles))
                   (swap! snake assoc :body (cons nh (:body @snake))))
                 (swap! snake assoc :body (cons nh (butlast (:body @snake)))))))))

(defn handle-input [e]  
  (when-let [dir (dirs (js/parseInt (aget e "keyCode")))]    
    (swap! snake assoc :dir dir)))

(defn render-game [ctx game]
  (doseq [p (:players game)]
    (js/console.log (str "Rendering player " p))
    (render-snake ctx p)))

(defn start [canvas game]
  (let [body (aget js/document "body")]
    (.addEventListener body "keydown" handle-input))
  (comment s
    (js/setInterval move-snake 60)
    (js/setInterval (fn []
                      (when (< 2 (count @edibles))
                        (swap! edibles butlast))
                      (swap! edibles conj (create-edible)) ; TODO ensure no duplicates
                      (js/console.log (str "edibles: " @edibles))) 5000))
  (let [ctx (.getContext canvas "2d")]
    (letfn [(render []
              (.clearRect ctx 0 0 500 500)
              (render-game ctx @game)
;              (render-edibles ctx @edibles)
              (.requestAnimationFrame js/window render))]
      (.requestAnimationFrame js/window render))))
