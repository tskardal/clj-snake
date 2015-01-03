(ns battlesnake.snake
  (:require [reagent.core :as reagent :refer [atom]]))

(defn ^:export init [canvas]
  (-> canvas
    .getContext "2d"))
