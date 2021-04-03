(ns webly.user.analytics.google-analytics
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]))



   ;[:script "window.dataLayer = window.dataLayer || [];
   ;          function gtag(){dataLayer.push(arguments);} "]


[:script "window.ga=window.ga||function(){(ga.q=ga.q||[]).push(arguments)};ga.l=+new Date;"]
#_[:script {:async true
            :src "https://www.google-analytics.com/analytics.js"}]

[:script {:async true
          :src "https://www.google-analytics.com/analytics_debug.js"}]

; analytics

(def analytics
  "(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','https://www.google-analytics.com/analytics.js','ga');")

(defn script-analytics [id]
  [:script
   (str analytics
        "ga('create', " id ", 'auto');
         ga('send', 'pageview');")])

#?(:cljs
   (defn ga-init [id debug?]
     (js/ga "create" id "auto")
     (js/ga "send" "pageview")
     (when debug?
       (set! (.. js/window -ga_debug) #js {:trace true}))))

#?(:cljs
   (defn ga [data]
     (js/ga "send"  (clj->js data))))

#?(:cljs
   (defn ga-event [category action label]
     (ga {:hitType "event"
          :eventCategory category
          :eventAction action
          :eventLabel label})))