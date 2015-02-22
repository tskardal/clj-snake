(ns snake)

(def snake-templates [{:dir [1 0] :body[[3 1] [2 1] [1 1]]}
                      {:dir [0 1] :body[[3 1] [2 1] [1 1]]}
                      {:dir [-1 0] :body[[3 1] [2 1] [1 1]]}
                      {:dir [0 -1] :body[[1 7] [1 8] [1 9]]}])

(defn create-new []
  {:width 10 :height 10})

(defn add-player [game & props]
  #+clj (println "adding player with props" (first props))
  {:pre [(< (count (:players game)) 4)]}
  (let [players (or (:players game)
                    [])
        c (count players)]
    (assoc game :players (conj players (merge (or (first props) {:id (inc c)}) (nth snake-templates c))))))

(defn- add-points [& points]
  (vec (apply map + points)))

(defn move-player [{:keys [dir body] :as player}]
  (let [head (first body)
        moved (conj (butlast body) (add-points head dir))]
    (assoc player :body moved)))

(defn tick [players]
  (vec (for [p players]
         (move-player p))))
