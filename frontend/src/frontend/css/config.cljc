(ns frontend.css.config
  (:require
   [clojure.string :refer [starts-with?]]))

(defn link-css [link]
  (if (or (starts-with? link "http")
          (starts-with? link "/"))
    link
    (str "/r/" link)))

(defn css-component [available component-kw component-theme]
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
          (map link-css links))))

(defn css-app [available current]
  (into []
        (reduce
         (fn [acc [kw v]]
           (concat acc (css-component available kw v)))
         []
         current)))

