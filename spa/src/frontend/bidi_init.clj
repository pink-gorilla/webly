(ns frontend.bidi-init
  (:require
   [extension :refer [get-extensions]]))

(defn get-cljs-routes [_module-name _config exts _default-config]
  (->> (get-extensions exts {:cljs-routes {}})
       (map :cljs-routes)
       (apply merge)))
