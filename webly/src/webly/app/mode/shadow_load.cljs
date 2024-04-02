(ns webly.app.mode.shadow-load
  (:require 
   [taoensso.timbre :refer-macros [info warn]]
   [shadow.loader :as l]
   ))

(defn change-url [url relative-res-path]
  ; url "/r/tick.js" => "/index-files/"
  (let [url-end (subs url 3)]
    (str relative-res-path url-end)))

(defn patch-url [relative-res-path uris uri-name]
  ; uris: goog.global.shadow$modules.uris
  (let [uri (aget uris uri-name)
        l (.-length uri)]
    (doall (for [i (range l)]
             (let [el (aget uri i)
                   adjusted (change-url el relative-res-path)] ; 
               (info "shadow uri:" uri-name " original:" el "adjusted: " adjusted)
               (aset uri i adjusted))))))

(defn set-shadow-load-dir [resource-path relative-res-path]
  (let [old (.. js/window -goog -global -SHADOW_ENV -scriptBase)
        script-base (str resource-path "cljs-runtime/")
        ;  https://awb99.github.io/walhalla/r/cljs-runtime/
        ]
    (info "shadow-cljs scriptbase old: " old  " new: " script-base)
  ; goog.global.SHADOW_ENV.scriptBase
  ; "http://localhost:8080/r/cljs-runtime/"
    (set! (.. js/window -goog -global -SHADOW_ENV -scriptBase)
          script-base)
    (let [;Object.keys (goog.global.shadow$modules["uris"])
          shadow-modules (.. js/window -goog -global -shadow$modules)
          uris (aget shadow-modules "uris")
          uri-names (.keys js/Object uris)]
    ; ['tick', 'ui-binaryclock', 'ui-container',
    ; 'ui-video', 'ui-spark', 'ui-codemirror', 'ui-repl']
      (doall (map (partial patch-url relative-res-path uris) uri-names)))))
