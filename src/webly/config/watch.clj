(ns webly.config.watch
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   [lambdaisland.deep-diff2 :as ddiff]
   [webly.writer :refer [write-status]]))

(defn watch-config! [a]
  (add-watch a :watcher
             (fn [key atom old-state new-state]
               ;"key" key -- :watcher
                     ;"atom" atom -- a
               (write-status "config" new-state)
               (debug "config changed: ")
                   ;  "old-state" old-state
                   ;  "new-state" new-state
               ; too verbose for many modules
               #_(let [d (ddiff/diff old-state new-state)]
                 (ddiff/pretty-print d)))))