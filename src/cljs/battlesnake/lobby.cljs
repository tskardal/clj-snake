(ns battlesnake.lobby
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [chan <! >! put! close! timeout]]
            [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def game-name (atom ""))
(def all-games (atom []))

(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn- on-create-game [name]
  (js/console.log (str "name: " name))
  (reset! game-name "")
  (POST "/games"
        {:params {:game-name name
                  :created-by "Bob"}
         :format :edn
         :handler handler
         :error-handler error-handler}))

(defn- update-game-list [fetched-games]
  (reset! all-games fetched-games))

(defn fetch-games []
  (GET "/games" {:handler update-game-list :response-format :edn}))

(defn create-game []
  [:div
   [:h3 "Create a game"]
   [:input {:type "text" :value @game-name :placeholder "Name the game!"
            :on-change #(reset! game-name (-> % .-target .-value))}]
   [:input {:type "button" :value "Start" :on-click #(on-create-game @game-name)}]])

(defn game-list []
  [:ul
   (for [game @all-games]
     [:li
      [:a {:href (str "/game/" (game :id))} (game :name)]])])

(defn lobby []
  [:div   
   [create-game]
   [game-list]])

(defn listen [ws-channel]  
  (go-loop []
    (when-let [{:keys [message]} (<! ws-channel)]
      (js/console.log (str "msg: " message))
      (recur))))

(defn update-games []
  (go-loop []    
    (fetch-games)
    (<! (timeout 5000))
    (recur)))

(defn ^:export init [parent]
  (go (let [{:keys [ws-channel]} (<! (ws-ch "ws://localhost:3000/ws"))]
        (listen ws-channel)
        (>! ws-channel "Hoist")))
  (update-games)
  (reagent/render-component [lobby] parent))
