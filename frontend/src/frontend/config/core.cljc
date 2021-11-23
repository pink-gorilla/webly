(ns frontend.config.core)




(defonce webly-mode-atom
  (atom :dynamic))


(def prefix-res
  (atom  "/r/"))

(defn res-href [href]
  (str @prefix-res href))

(defn link-css [href]
  [:link {:rel "stylesheet"
          :href (res-href href)}])









