(ns webly.config
  (:require
   [taoensso.timbre :as timbre]
   #?(:cljs [webly.web.events-bidi]) ; side-effects
   ))

(def webly-config
  (atom {; most web-apps will have to change that 
         :title "webly"
         :icon "/r/webly/favicon.ico"
         :start  "webly.web.app.after-load (); "

         :timbre-loglevel [[#{"pinkgorilla.nrepl.client.connection"} :debug]
                           [#{"*"} :info]]

         ; mos web-apps can leave this at default values
         :prefix "/r/" ; resource route prefix
         ; css from resources 
         :css-links ["tailwindcss/dist/tailwind.css"
                     "@fortawesome/fontawesome-free/css/all.min.css"
                     "fonts-google/fonts.css"]
         ; css from external sites
         :css-extern [;"http://fonts.googleapis.com/css?family=Arvo:400,700,400italic,700italic|Lora:400,700,400italic,700italic"
                      ;"https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic"
                      ;"https://fonts.googleapis.com/css?family=Roboto+Condensed:400,300"
                      ;"https://cdnjs.cloudflare.com/ajax/libs/material-design-iconic-font/2.2.0/css/material-design-iconic-font.min.css"
                      ]

         :shadow {:dev-http {:port 8000}
                  :http {:port 8001 :host "localhost"}
                  :nrepl {:port 8002}}}))

(defn res-href [href]
  (str (:prefix @webly-config) href))

(defn link-css [href]
  [:link {:rel "stylesheet"
          :href (res-href href)}])

(defn timbre-config! []
  (timbre/set-config!
   (merge timbre/default-config
          {:min-level (:timbre-loglevel @webly-config)})))


