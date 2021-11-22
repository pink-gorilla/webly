(ns frontend.css.helper
  (:require
   [goog.string :as gstring]
   [goog.string.format]))

(defn add-themes
  "this helper fuction makes it easier to add multiple theme css files whose
   name depends on the theme name. Used in babashka in ui-code and others"
  [m theme-base themes]
  (let [theme-link (fn [theme]
                     (gstring/format theme-base theme))
        add-theme (fn [acc theme]
                    ;(println "adding:" theme)
                    (assoc acc theme [(theme-link theme)]))]
    (reduce add-theme m themes)))