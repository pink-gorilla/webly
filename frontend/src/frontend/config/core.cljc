(ns frontend.config.core)

(def prefix-res
  (atom  "/r/"))

(defn res-href [href]
  (str @prefix-res href))

(defn link-css [href]
  [:link {:rel "stylesheet"
          :href (res-href href)}])




