(ns webly.user.analytics.google-tag
  (:require
   #?(:clj  [taoensso.timbre :refer [debug info warn error]])
   #?(:cljs  [taoensso.timbre :refer-macros [debug info warn error]])))




;The syntax is :async true for tags that don't have a value, but you'll need to ensure that the :mode is set to :html. So:
; (html {:mode :html} [:script {:async true}])
; Note that if you use the html5 macro, the mode will automatically be set when rendering.

(defn script-cljs [id]
  [:script {:async nil ; async not rendered. see: https://github.com/weavejester/hiccup/issues/182
            :type "text/javascript"
            :src (str "https://www.googletagmanager.com/gtag/js?id=" id)}])

(defn script-js [id]
  [:div
   (str "<script async src='https://www.googletagmanager.com/gtag/js?id=" id "' type= 'text/javascript'> </script>")])

(defn script-tag-src
  [google-analytics-config]
  (let [{:keys [enabled id]} google-analytics-config]
    (if (and enabled id)
      (do (info "google analytics starting with google id: " id)
          ;(script-cljs id)
          (script-js id))
      (do
        (warn "no google analytics config!")
        [:div {:class "no-google-analytics-config"}]))))

(defn script-tag-config [google-analytics-config]
  (let [{:keys [enabled id]} google-analytics-config]
    (if (and enabled id)
      [:script
       (str "window.dataLayer = window.dataLayer || [];
         function gtag(){dataLayer.push(arguments);}
         gtag('js', new Date());
         gtag('config', '" id "');")]
      [:div {:class "no-google-analytics-config"}])))

#?(:cljs
   ; gtag("event", "sign_up", {"method": "email" });
   (defn send-event [action data]
     (let [datajs (clj->js data)]
       (info "ga event" action data)
       (js/gtag "event" action datajs))))

