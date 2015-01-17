(ns battlesnake.lobby
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [chan <! >! put! close! timeout]]
            [reagent.core :as reagent :refer [atom]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def game-name (atom ""))
(def all-games (atom [{:name "Awesome game"} {:name "Less awesome game"}]))

(defn create-game []
  [:div
   [:h3 "Create a game"]
   [:input {:type "text" :value @game-name :placeholder "Name the game!"
            :on-change #(reset! game-name (-> % .-target .-value))}]
   [:input {:type "button" :value "Start" :on-click #(reset! game-name "")}]])

(defn game-list []
  [:ul
   (for [game @all-games]
     [:li (game :name)])])

(defn lobby []
  [:div
   [create-game]
   [game-list]])

(defn listen [ws-channel]  
  (go-loop []
    (when-let [{:keys [message]} (<! ws-channel)]
      (js/console.log (str "msg: " message))
      (recur))))

(defn ^:export init [parent]
  (go (let [{:keys [ws-channel]} (<! (ws-ch "ws://localhost:3000/ws"))]
        (listen ws-channel)
        (>! ws-channel "Hoist")))
  (reagent/render-component [lobby] parent))
