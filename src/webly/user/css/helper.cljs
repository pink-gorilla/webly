(ns webly.user.css.helper
  (:require
   [goog.string :as gstring]
   [goog.string.format]))

(defn add-themes [m theme-base themes]
  (let [theme-link (fn [theme]
                     (gstring/format theme-base theme))
        add-theme (fn [acc theme]
                    ;(println "adding:" theme)
                    (assoc acc theme [(theme-link theme)]))]
    (reduce add-theme m themes)))