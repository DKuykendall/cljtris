(ns cljtris.core (:require [reagent.core :as r]))

(def size 20)

(def height 24)

(def width 10)

(def board (r/atom []))

(defn create-piece [color squares] {:active true
                                    :squares(for [[x y] squares]
                                              {:x x
                                               :y y
                                               :color color })})

(def line (create-piece "red" [[0 0] [1 0] [2 0] [3 0]]))

(def square (create-piece "blue" [[0 0] [1 0] [0 1] [1 1]]))

(def s (create-piece "green" [[0 1] [1 1] [1 0] [2 0]]))

(def z (create-piece "gray" [[0 0] [1 0] [1 1] [2 1]]))

(def L (create-piece "orange" [[0 0] [0 1] [0 2] [1 2]]))

(def reverse-L (create-piece "purple" [[1 0] [1 1] [1 2] [0 2]]))

(def pieces [line square s z L reverse-L])

(defn new-piece [] (swap! board conj (rand-nth pieces)))

(defn render [board] 
  (for [piece board]
    (for [{x :x y :y color :color} (:squares piece)] 
      [:div {:style {:width (str (- size 2) "px")
                     :height (str (- size 2) "px")
                     :background-color color
                     :position "absolute"
                     :left (inc (* size (inc x)))
                     :top  (inc (* size (inc y)))}
             :key (gensym)}])))

(defn move [dir] (println dir))

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

(defn on-other-piece? [squares board] 
  (let [result (for [square squares
                     piece board]
                 (seq (filter #(and (= (:x square) (:x %))
                               (= (:y square) (:y %))
                               (not (:active piece))) (:squares piece))))]
    (seq (remove nil? result))))

(defn fall [board] (for [piece board] 
                     (if (:active piece)
                       (let [new-squares 
                             (for [square (:squares piece)]
                               (assoc square :y (inc (:y square))))]
                         (if (or (some #(> (:y %) (dec height)) new-squares)
                                 (on-other-piece? new-squares board))
                           (do (new-piece) {:active false :squares (:squares piece)})
                           {:active true :squares new-squares}))
                       piece)))

(defn swap [] (swap! board fall))

(defonce fall-event (js/setInterval swap 1000))

(defonce key-event (.addEventListener js/document "keydown" keydown))

(new-piece)

(defn first-component [] (into [:div] (render @board)))

(r/render-component [first-component] (.getElementById js/document "app"))
