{:npm-deps {; react
            "create-react-class" "^15.7.0"  ; is this needed?
            "react-dom-factories" "^1.0.2" ; is this needed?
            ; since reagent 1.1, reagent no longer brings react and react-dom
            "react" "^17.0.1"
            "react-dom" "^17.0.1"

            ; javascript unit tests 
            "karma" "^6.3.2"
            "karma-chrome-launcher" "^3.1.0"
            "karma-cljs-test" "^0.1.0"
            ;"karma-junit-reporter"  "2.0.1"
            ;"cross-env": "^6.0.3",

            ;; 2021 11 22 awb99: deps.cljs from frontend need to be copied here.
            ;; shadow-cljs is not picking them up.

            ; fonts
            "@fortawesome/fontawesome-free" "^5.14.0"
            "get-google-fonts" "^1.2.2"

            ; tailwind
            "tailwindcss" "2.1.2"
            "autoprefixer" "^10.0.2" ; peer dependency of tailwind. actually needed?
            "postcss" "^8.1.13"  ; peer dependency of tailwind

            "@icon/fontisto-emoji" "^3.0.4-alpha.0" ; this leads to warnings in dependent projects, perhaps due to alpha text

;
            }}
