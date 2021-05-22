(ns webly.user.app.css)

;"http://fonts.googleapis.com/css?family=Arvo:400,700,400italic,700italic|Lora:400,700,400italic,700italic"
;"https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic"
;"https://fonts.googleapis.com/css?family=Roboto+Condensed:400,300"
;"https://cdnjs.cloudflare.com/ajax/libs/material-design-iconic-font/2.2.0/css/material-design-iconic-font.min.css"


(def components
  {:tailwind {:light ["tailwindcss/dist/tailwind.min.css"]
              :dark ["tailwindcss/dist/tailwind-dark.min.css"]}
   :fonts    {true ["@fortawesome/fontawesome-free/css/all.min.css"
                    "fonts-google/fonts.css"]}
   :emoji    {true ["@icon/fontisto-emoji/fontisto-emoji.css"]}
   :webly    {true ["webly/dialog.css"]}})

(def config
  {:tailwind :light ; true
   :fonts    true
   :emoji    false
   :webly    true})

