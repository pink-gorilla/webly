{:npm-deps {; fonts
            "@fortawesome/fontawesome-free" "^5.14.0"
            "get-google-fonts" "^1.2.2"
            "@icon/fontisto-emoji" "^3.0.4-alpha.0" ; this leads to warnings in dependent projects, perhapsdueto alpha text
            ; tailwind
            "tailwindcss" "2.1.2"
            "autoprefixer" "^10.0.2" ; peer dependency of tailwind. actually needed?
            "postcss" "^8.1.13"  ; peer dependency of tailwind

            ; javascript unit tests 
            "karma" "^6.3.2"
            "karma-chrome-launcher" "^3.1.0"
            "karma-cljs-test" "^0.1.0"
            ;"karma-junit-reporter"  "2.0.1"
            ;"cross-env": "^6.0.3",

            ; react
            "create-react-class" "^15.7.0"

            ;"react" "^17.0.1"  ; reagent brings it in deps.cljs
            ;"react-dom" "^17.0.1"  ; reagent brings it deps.cljs
            "react-dom-factories" "^1.0.2"

  ;
            }
 :npm-dev-deps {; this works with lein-shadow, but not with normal shadow-cljs
                ;"shadow-cljs"           "2.12.4"

                ; javascript unit tests 
                ;"karma" "^6.3.2"
                ;"karma-chrome-launcher" "^3.1.0"
                ;"karma-cljs-test" "^0.1.0"
                }}
