(ns frontend.css.config
  (:require
   [clojure.string :refer [starts-with?]]))

(defn link-css [prefix link]
  (if (or (starts-with? link "http")
          (starts-with? link "/"))
    link
    (str prefix link)))

(defn css-component [prefix available component-kw component-theme]
  (let [component-theme (or component-theme false) ;  (get config component-kw) false)
        get-theme (fn [theme]
                    (or (get-in available [component-kw theme]) []))
        links (case component-theme
                false []
                true (get-theme true)
                (concat
                 (get-theme true)
                 (get-theme component-theme)))]
    (into []
          (map (partial link-css prefix) links))))

(defn css-app [prefix available current]
  (into []
        (reduce
         (fn [acc [kw v]]
           (concat acc (css-component prefix available kw v)))
         []
         current)))

