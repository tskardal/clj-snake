(ns snake)

(defn update []
  (let [msg "updatz!"]
    #+clj (println msg)
    #+cljs (js/console.log msg)))
