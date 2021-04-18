{:npm-deps {; fonts
            "@fortawesome/fontawesome-free" "^5.14.0"
            "get-google-fonts" "^1.2.2"

            ; tailwind
            "tailwindcss" "2.1.1"
            "autoprefixer" "^10.0.2" ; peer dependency of tailwind. actually needed?

            ; javascript unit tests 
            ;"karma" "^6.3.2"
            ;"karma-chrome-launcher" "^3.1.0"
            ;"karma-cljs-test" "^0.1.0"

            ; react
            "create-react-class" "^15.7.0"
            "react" "^16.13.0"  ; reagent needs this version
            "react-dom" "^16.13.0"  ; reagent needs this version
            "react-dom-factories" "^1.0.2"

            ; markdown -> html renderer
            "marked" "^2.0.3"
            ; "highlight.js": "9.18.1",
            ; "react-highlight.js": "1.0.7",

  ;
            }
 :npm-dev-deps {;"shadow-cljs"           "2.12.4"

                ; javascript unit tests 
                "karma" "^6.3.2"
                "karma-chrome-launcher" "^3.1.0"
                "karma-cljs-test" "^0.1.0"
                ;"karma-junit-reporter"  "2.0.1"
                }}
