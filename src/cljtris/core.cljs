(ns cljtris.core (:require [reagent.core :as r]))

(defn first-component [] [:div "Hello world!"])

(r/render-component [first-component] (.-body js/document))
