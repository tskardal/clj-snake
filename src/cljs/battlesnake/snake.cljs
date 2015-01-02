(ns cljs.battlesnake.snake
  (:require [reagent.core :as reagent :refer [atom]]))

(def game-name (atom ""))

(defn create-game []  
  [:div
   [:h3 "Create a game"]
   [:input {:type "text" :value @game-name :placeholder "Name the game!"
            :on-change #(reset! game-name (-> % .-target .-value))}]
   [:input {:type "button" :value "Start" :on-click #(reset! game-name "")}]])

(defn game-list []
  [:ul
   [:li "Game #1"]
   [:li "Game #2"]])

(defn lobby []
  [:div
   [create-game]
   [game-list]])

(reagent/render-component [lobby] (.-body js/document))
