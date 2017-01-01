(require 'cljs.build.api)

(cljs.build.api/watch "src"
  {:main 'cljtris.core
   :output-to "out/main.js"})
