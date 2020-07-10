(ns webly.web.handler)

;(defmulti reagent-page identity)

(defmulti reagent-page
  (fn [x] (get x :handler)))
