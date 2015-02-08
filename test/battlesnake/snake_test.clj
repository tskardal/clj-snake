(ns battlesnake.snake-test
  (:require [snake :refer :all]
            [midje.sweet :refer :all]))

(def sample-game
  {:width 10
   :height 10
   :players [{:id 1}
             {:id 2}
             {:id 3}
             {:id 4}]})

(facts "about a snake game"
  (fact "it has a size"
    (:width (create-new)) => 10
    (:height (create-new)) => 10)
  (fact "it can add four players"
    (-> (create-new)
        (add-player)
        (add-player)
        (add-player)
        (add-player)
        :players
        count) => 4)
  (fact "it's impossible to add more than four players"
    (-> sample-game
        (add-player)) => (throws AssertionError))
  (fact "first player is placed in upper left corner going right"
    (let [g (-> (create-new) add-player)
          p (-> g :players first)
          dir (:dir p)
          head (-> p :body first)]
      dir => [1 0]
      head => [3 1]))
  (fact "last player is placed in lower left corner going up"
    (let [g (-> (create-new) add-player add-player add-player add-player)
          p (-> g :players last)
          dir (:dir p)
          head (-> p :body first)]
      dir => [0 -1]
      head => [1 7])))

(facts "about a player"
  (def player
    {:id 1 :dir [1 0] :body [[1 0] [0 0]]})
  (fact "it moves one step in the correct direction"
    (-> player move-player :body) => [[2 0] [1 0]]))

