(ns webly.user.emoji)

;; font awesome example:
;;  [:i.fas.fa-trash]

; https://fontello.com/ - icon font generator

(defn emoji [name]
  [:i {:class (str "fiem " name)}])

; fontisto-emoji

; another option is:
; https://emoji-css.afeld.me/
; nice browser, but they have no npm

