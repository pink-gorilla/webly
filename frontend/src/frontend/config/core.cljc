(ns frontend.config.core)


(defonce webly-mode-atom
  (atom :dynamic))


#_(def prefix-res
  (atom  "/r/"))

#_(defn res-href [href]
  (str @prefix-res href))

#_(defn link-css [href]
  [:link {:rel "stylesheet"
          :href (res-href href)}])









