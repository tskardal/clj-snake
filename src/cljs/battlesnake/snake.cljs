(ns cljs.battlesnake.snake
  (:require [reagent.core :as reagent :refer [atom]]))

(defn simple-component []
  [:div
   [:h3 "Hello Reagent!"]
   [:div
    [:p "I am a component!"]
    [:p.someclass
     "I have " [:strong "bold"]
     [:span {:style {:color "red"}} " and red "] "text."]]])

(reagent/render-component (fn [] [simple-component])
                            (.-body js/document))
