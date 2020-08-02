(ns webly.web.handler)

(defmulti reagent-page
  (fn [x] (get x :handler)))
