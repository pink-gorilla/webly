(ns webly.user.emoji)

(defn emoji [name]
  [:i {:class (str "fiem " name)}])
