(ns battlesnake.snake
  (:require [reagent.core :as reagent :refer [atom]]))


(defn canvas []
  [:div "here be canvas"])

(defn ^:export init [parent]
  (reagent/render-component [canvas] parent))
