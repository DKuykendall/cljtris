(ns cljtris.core (:require [reagent.core :as r]))

(def size 20)

(def height 24)

(def width 10)

(def board (r/atom {:active nil :blocks [] :score 0}))

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

(def T (create-piece "cyan" [[0 0] [1 0] [2 0] [1 1]]))

(def pieces [line square s z L reverse-L T])

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

(defn rotate [] 
  (swap! board assoc :active 
         (loop [center (second (:active @board))
                new-piece (for [square (:active @board)]
                            (let [rel-x (- (:x center) (:x square))
                                  rel-y (- (:y center) (:y square))
                                  new-x (+ (:x center) rel-y)
                                  new-y (+ (:y center) (- rel-x))]
                              (assoc square :x new-x :y new-y)))]
           (cond (some #(< (:x %) 0) new-piece) 
                 (recur center (map #(update % :x inc) new-piece))
                 (some #(> (:x %) (dec width)) new-piece)
                 (recur center (map #(update % :x dec) new-piece))
                 (on-other-piece? new-piece (:blocks @board))
                 (recur center (map #(update % :y dec) new-piece))
                 :else new-piece))))

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

(defn find-complete-rows [] 
  (set (map first 
            (filter #(= 10 (second %)) 
                    (frequencies (map :y (:blocks @board)))))))

(defn remove-rows [complete-rows] 
  (loop [rows (sort complete-rows) 
         blocks (:blocks @board)]
    (if (empty? rows)
      blocks
      (let [head (first rows)
            tail (rest rows)
            less (filter #(< (:y %) head) blocks) 
            grt  (filter #(> (:y %) head) blocks)]
        (recur tail 
               (concat grt (map #(assoc % :y (inc (:y %))) less)))))))

(defn fall [board] (let [piece (:active board)
                         new-piece
                         (for [square piece]
                           (assoc square :y (inc (:y square))))]
                     (if (or (some #(> (:y %) (dec height)) new-piece)
                             (on-other-piece? new-piece (:blocks board)))
                       (add-piece (assoc board :blocks (concat (:active board)
                                                               (:blocks board))))
                       (let [complete-rows (find-complete-rows)] 
                         (assoc board :active new-piece
                                      :blocks (remove-rows complete-rows)
                                      :score (+ (:score board) (count complete-rows)))))))

(defn swap [] (swap! board fall))

(defonce fall-event (js/setInterval swap 200))

(defonce key-event (.addEventListener js/window "keydown" keydown))

(swap! board add-piece @board)

(defn first-component [] [:div (into [:div {:style {:outline "1px solid black" 
                                              :width (* (inc width) size)
                                              :height (* (inc height) size)}}] 
                               (render @board))
                          [:p (str "Score - " (:score @board))]])

(r/render-component [first-component] (.getElementById js/document "app"))
