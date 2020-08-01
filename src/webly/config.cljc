(ns webly.config
  (:require
   [clojure.string] ; clj is empty otherwise
   #?(:cljs [webly.web.events-bidi]) ; side-effects
   ))

(def webly-config
  (atom {:prefix "/r/" ; resource route prefix
         :title "webly"
         :icon "/r/favicon.ico"
         
         :timbre-loglevel :debug ; :info  ;  :trace :debug
        
         :start  "webly.web.app.after-load (); "
         ; css from resources 
         :css-links ["tailwindcss/dist/tailwind.css"
                    "@fortawesome/fontawesome-free/css/all.min.css"]  
         ; css external sites
         :css-extern ["http://fonts.googleapis.com/css?family=Arvo:400,700,400italic,700italic|Lora:400,700,400italic,700italic"
               "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic"
               "https://fonts.googleapis.com/css?family=Roboto+Condensed:400,300"
               "https://cdnjs.cloudflare.com/ajax/libs/material-design-iconic-font/2.2.0/css/material-design-iconic-font.min.css"] 
         }))

(defn res-href [href]
  (str (:prefix @webly-config) href))

(defn link-css [href]
  [:link {:rel "stylesheet"
          :href (res-href href)}])




