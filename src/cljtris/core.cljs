(ns cljtris.core (:require [reagent.core :as r]))

(def size 50)

(def line {:color "red" :squares [[0 0] [1 0] [2 0] [3 0]]})

(def square {:color "blue" :squares [[0 0] [1 0] [0 1] [1 1]]})

(def s {:color "green" :squares [[0 1] [1 1] [1 0] [2 0]]})

(def z {:color "gray" :squares [[0 0] [1 0] [1 1] [2 1]]})

(def L {:color "orange" :squares [[0 0] [0 1] [0 2] [1 2]]})

(def reverse-L {:color "purple" :squares [[1 0] [1 1] [1 2] [0 2]]})

(def pieces [line square s z L reverse-L])

(defn render [piece] 
  (for [[x y] (:squares piece)] 
    [:div {:style {:width (str (- size 2) "px")
                   :height (str (- size 2) "px")
                   :background-color (:color piece)
                   :position "absolute"
                   :left (inc (* size (inc x)))
                   :top  (inc (* size (inc y)))}}]))

(defn first-component [] (into [:div] (render (rand-nth pieces))))

(r/render-component [first-component] (.-body js/document))
