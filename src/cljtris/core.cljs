(ns cljtris.core (:require [reagent.core :as r]))

(def size 20)

(def height 24)

(def width 10)

(def board (r/atom {:active nil :blocks []}))

(defn create-piece [color squares] (for [[x y] squares]
                                              {:x x
                                               :y y
                                               :color color }))

(def line (create-piece "red" [[0 0] [1 0] [2 0] [3 0]]))

(def square (create-piece "blue" [[0 0] [1 0] [0 1] [1 1]]))

(def s (create-piece "green" [[0 1] [1 1] [1 0] [2 0]]))

(def z (create-piece "gray" [[0 0] [1 0] [1 1] [2 1]]))

(def L (create-piece "orange" [[0 0] [0 1] [0 2] [1 2]]))

(def reverse-L (create-piece "purple" [[1 0] [1 1] [1 2] [0 2]]))

(def pieces [line square s z L reverse-L])

(defn add-piece [board] (assoc board :active (rand-nth pieces)))

(defn create-block [{x :x y :y color :color}]
        [:div {:style {:width (str (- size 2) "px")
                       :height (str (- size 2) "px")
                       :background-color color
                       :position "absolute"
                       :left (inc (* size (inc x)))
                       :top  (inc (* size (inc y)))}
               :key (gensym)}])

(defn render [board]
  (let [active-squares (for [square (:active board)] (create-block square))
        pile-squares   (for [square (:blocks board)] (create-block square))]
    (concat active-squares pile-squares)))

(defn on-other-piece? [squares blocks]
  (let [result (for [square squares]
                 (seq (filter #(and (= (:x square) (:x %))
                                    (= (:y square) (:y %))) 
                              blocks)))]
    (seq (remove nil? result))))

(defn move [dir] 
  (swap! board assoc :active  (let [new-piece (for [square (:active @board)] 
                             (assoc square :x (+ dir (:x square))))]
             (if (or (some #(or (< (:x %) 0) (> (:x %) (dec width))) new-piece)
                     (on-other-piece? new-piece (:blocks @board)))
               (:active @board)
               new-piece))))

(defn rotate [])

(defn fast-drop [])

(defn keydown [e]
  (case (.-keyCode e)
    ;left
    37 (move -1)
    ;right
    39 (move 1)
    ;uo
    38 (rotate)
    ;down
    40 (fast-drop)
    nil))


(defn fall [board] (let [piece (:active board)
                         new-piece
                         (for [square piece]
                           (assoc square :y (inc (:y square))))]
                     (if (or (some #(> (:y %) (dec height)) new-piece)
                             (on-other-piece? new-piece (:blocks board)))
                       (add-piece (assoc board :blocks (concat (:active board)
                                                               (:blocks board))))
                       (assoc board :active new-piece))))
                     ;(if (or (some #(> (:y %) (dec height)) new-squares)
                     ;        (on-other-piece? new-squares board))
                     ;  (do (new-piece) {:active false :squares (:squares piece)})
                     ;  {:active true :squares new-squares})))

(defn swap [] (swap! board fall))

(defonce fall-event (js/setInterval swap 200))

(defonce key-event (.addEventListener js/document "keydown" keydown))

(swap! board add-piece @board)

(defn first-component [] (into [:div] (render @board)))

(r/render-component [first-component] (.getElementById js/document "app"))
