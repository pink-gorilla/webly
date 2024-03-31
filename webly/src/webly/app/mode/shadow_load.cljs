(ns webly.app.mode.shadow-load
  (:require 
   [webly.app.mode.url :refer [dynamic-base]]))


(defn patch-url [uris uri-name]
  (let [base (dynamic-base)
        uri (aget uris uri-name)
        ;_ (.log js/console "uri: " uri)
        l (.-length uri)]
    ;(.log js/console "length: " l)
    (doall (for [i (range l)]
             (do
        ;(.log js/console "setting index: " i)
               (let [el (aget uri i)] ; "/r/tick.js"
          ;(.log js/console "el: " el)
                 (aset uri i (str base el))))))))

(defn set-shadow-load-dir [url]
  ; goog.global.SHADOW_ENV.scriptBase
  ; "http://localhost:8080/r/cljs-runtime/"
  (set! (.. js/window -goog -global -SHADOW_ENV -scriptBase)
        url)
  (let [;Object.keys (goog.global.shadow$modules["uris"])
        shadow-modules (.. js/window -goog -global -shadow$modules)
        uris (aget shadow-modules "uris")
        uri-names (.keys js/Object uris)]
    ; ['tick', 'ui-binaryclock', 'ui-container',
    ; 'ui-video', 'ui-spark', 'ui-codemirror', 'ui-repl']
    (doall (map (partial patch-url uris) uri-names))))

(defn dynamic-cljs-runtime-dir []
  (let [base (dynamic-base)]
    (str base "/r/cljs-runtime/")))

(defn set-github-load-mode []
   ;  https://awb99.github.io/walhalla/r/cljs-runtime/
  (let [dir (dynamic-cljs-runtime-dir)]
    (println "github shadow base: " dir)
    (set-shadow-load-dir dir)))