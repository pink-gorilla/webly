(defproject org.pinkgorilla/webly "0.2.42"
  :description "web (server / reagent) helper library."
  :url "https://github.com/pink-gorilla/webly"
  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/release_username
                                     :password :env/release_password
                                     :sign-releases false}]]

  :min-lein-version "2.9.4" ; nrepl 0.7.0

  ;:prep-tasks ["css" ; copies tailwind css, so it ends up as resources 
  ;             "google-fonts"
  ;             "md"]

  :release-tasks [["vcs" "assert-committed"]
                  ["bump-version" "release"]
                  ["vcs" "commit" "Release %s"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["deploy"]
                  ["bump-version"]
                  ["vcs" "commit" "Begin %s"]
                  ["vcs" "push"]]

  :managed-dependencies [; managed deps does nott work here, as webly is used as a library.
                         ; projects using webly would have to fix all the transient libs of webly
                         ; on their own AGAIN.                         
                         ; lein deps :tree conflicts need to be sorted out in :dependencies
                         ]

  :dependencies [; dependency conflict resolution
                 [org.ow2.asm/asm-commons "9.0"] ; jetty and shadow
                 [org.ow2.asm/asm-analysis "9.0"] ; jetty and shadow

                 [commons-codec "1.15"]
                 [com.cognitect/transit-clj "1.0.324"] ; luminus-transit + httpfx + 
                 [com.cognitect/transit-cljs "0.8.269"]
                ;[com.cognitect/transit-java "1.0.343"]
                 [com.fasterxml.jackson.core/jackson-core "2.12.3"] ; cheshire + jsonista
                 [org.ow2.asm/asm "9.1"] ; core.asymc tools.reader
                 [org.clojure/tools.reader "1.3.5"] ; sente+encore + shadow-cljs + sci
                 [javax.xml.bind/jaxb-api "2.3.1"] ; transit/java + shadow-cljs 
                ;[com.google.code.findbugs/jsr305 "3.0.2"]
                 [org.slf4j/slf4j-api "2.0.0-alpha1"] ; jetty
                 [borkdude/edamame "0.0.11"] ; clj-kodo and reframe

                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/core.async "1.3.618"]
                 [com.taoensso/timbre "5.1.2"] ; clj/cljs logging
                 [com.fzakaria/slf4j-timbre "0.3.21"] ; slf4j ->timbre adapter (used by jetty)

                 ; time
                 [clojure.java-time "0.3.2"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]

                 ; encoding
                 [org.clojure/data.json "2.1.0"] ; https://github.com/thheller/shadow-cljs/issues/872
                 [luminus-transit "0.1.2"]
                 [cheshire "5.10.0"]  ; JSON parsings and pretty printing

                 ; ring + middlewares
                 [ring/ring-core "1.9.3"]
                 [ring/ring-anti-forgery "1.3.0"]
                 [ring-cors "0.1.13"]
                 [ring/ring-defaults "0.3.2"
                  :exclusions [javax.servlet/servlet-api]]
                 ;; [ring.middleware.logger "0.5.0"]
                 [metosin/muuntaja "0.6.8"] ; 30x faster than ring-middleware-format
                 [ring/ring-json "0.5.1"]
                 [ring-cljsjs "0.2.0"]
                 [bk/ring-gzip "0.3.0"] ; from oz
                 [luminus/ring-ttl-session "0.3.3"]
                 [ring-oauth2 "0.1.5"]
                 [prone "2020-01-17"] ; exception middleware
                 [ring/ring-devel "1.9.3"] ; reload middleware

                 ; routing
                 [bidi "2.1.6"]
                 [clj-commons/pushy "0.3.10"]
                 [com.cemerick/url "0.1.1"]  ; url query-strings

                 ; server side templating
                 [hiccup "1.0.5"]

                 ; frontend
                 [reagent "1.0.0" :exclusions [org.clojure/tools.reader
                                               ; cljsjs/react
                                               ; cljsjs/react-dom
                                               ]]
                 [re-frame "1.2.0"]
                 [cljs-ajax "0.8.3"] ; needed for re-frame/http-fx

                 [day8.re-frame/http-fx "0.2.3"  ; reframe based http requests
                  :exclusions [[re-frame]]] ; a more modern reframe comes from webly

                 [keybind "2.2.0"]

                 [day8.re-frame/re-frame-10x "1.0.2"]
                 [day8.re-frame/tracing "0.6.2"] ; https://github.com/day8/re-frame-debux
                 ;[day8.re-frame/tracing-stubs "0.6.2"]

                 ;shadow
                 ; shadow-cljs MAY NOT be a dependency in lein deps :tree -> if so, bundler
                 ; will fail because shadow contains core.async which is not compatible with 
                 ; self hosted clojurescript
                 [thheller/shadow-cljs "2.12.5"
                  :exclusions [org.clojure/tools.reader ; outdated
                               ]]
                 ;[thheller/shadow-cljsjs "0.0.21"]  ; already referred to from shadow-cljs
                 [org.clojure/clojurescript "1.10.844"]

                 [resauce "0.2.0"] ; resources
                 [cprop "0.1.17"] ; config management
                 [akiroz.re-frame/storage "0.1.4"] ; localstorage 

                 ; supported servers
                 ; ; last version with java 9 : 0.14.0
                 [info.sunng/ring-jetty9-adapter "0.15.1"] ; uses jetty 10.0.2
                 ;[luminus/ring-undertow-adapter "1.2.0"]
                 ;[http-kit "2.5.3"]

                 ; websockets
                 [com.taoensso/encore "3.19.0"] ; sente exclude
                 [com.taoensso/sente "1.16.2"
                  :exclusions [com.taoensso/encore ; outdated
                               org.clojure/core.async]]

                 [fipp "0.6.23"] ; edn pretty printing
                 ]

  :target-path  "target/jar"
  :source-paths ["src"]
  :test-paths ["test"]
  :resource-paths ["resources"  ; webly resources (svg/img)
                   "target/node_modules"] ; css png resources from npm modules (tailwind)

  :profiles {:demo {; unit tests use demo profile for resource tests
                   ; so the demo serves tw puroses
                   ; 1. ilustrate links in web-app
                   ; 2. run unit tests 
                    :dependencies [#_[org.pinkgorilla/gorilla-ui "0.2.34" ; brings pinkie
                                      :exclusions [org.clojure/clojurescript]]]
                    :source-paths ["profiles/demo/src"]
                    :resource-paths  ["target/webly"
                                      "profiles/demo/resources"]}

             :dev {:resource-paths  ["target/webly" ; for lein test
                                     "profiles/demo/resources"
                                     "profiles/test/resources"
                                     ]

                   :dependencies [[clj-kondo "2021.04.23"]
                                  ; antq fucks up shadow-cljs
                                  ;[com.github.liquidz/antq "0.13.0"
                                   ;:exclusions [org.apache.httpcomponents/httpclient
                                  ;              org.clojure/tools.cli]
                                  ; ]
                                  [ring/ring-mock "0.4.0"]]
                   :plugins      [[lein-cljfmt "0.6.6"]
                                  [lein-cloverage "1.1.2"]
                                  [lein-shell "0.5.0"]
                                  [lein-ancient "0.6.15"]]
                   :aliases      {;"outdated" ["run" "-m" "antq.core"]
                                  "clj-kondo" ["run" "-m" "clj-kondo.main"]}
                   :cloverage    {:codecov? true
                                  ;; In case we want to exclude stuff
                                  ;; :ns-exclude-regex [#".*util.instrument"]
                                  ;; :test-ns-regex [#"^((?!debug-integration-test).)*$$"]
                                  }
                   ;; TODO : Make cljfmt really nice : https://devhub.io/repos/bbatsov-cljfmt
                   :cljfmt       {:indents {as->                [[:inner 0]]
                                            with-debug-bindings [[:inner 0]]
                                            merge-meta          [[:inner 0]]
                                            try-if-let          [[:block 1]]}}}}

  :aliases {"bump-version"
            ["change" "version" "leiningen.release/bump-version"]

            ;; copy resources

            "css"  ^{:doc "Copies npm package dependencies that are not managed by shadow-cljs"}
            ["shell" "./scripts/copy_res.sh"]

            "google-fonts"  ^{:doc "Installs google fonts in resources"}
            ["shell" "./scripts/get-fonts.sh"]

            "md"  ^{:doc "Copies markdown files to resources"}
            ["shell" "./scripts/copy-md.sh"]

            "prep-res"
            ["do" ["css"] ["google-fonts"] ["md"]]

            "lint"  ^{:doc "Lint for dummies"}
            ["clj-kondo"
             "--config" "clj-kondo.edn"
             "--lint" "src/webly"]

            ;; SHADOW-CLJS (for testing purposes only)

            ;"shadow-build"  ^{:doc "compiles bundle"}
            ;["with-profile" "+demo" "run" "-m" "shadow.cljs.devtools.cli" "compile" "webly"]

            ;"shadow-watch"  ^{:doc "compiles bundle"}
            ;["with-profile" "+demo" "run" "-m" "shadow.cljs.devtools.cli" "watch" "webly"]

            ;; DEMO 

            "webly"  ^{:doc "webly - add profile name"}
            ["with-profile" "+demo" "run" "-m" "demo.app"]

            ;; Unit Tests  

            "test-clj"  ^{:doc "run unit tests (they need demo profile)"}
            ["with-profile" "+demo" "test"]

            "test-js" ^{:doc "run unit test JavaScript."}
            ["do"
             ["webly" "ci"]
             ["shell" "npm" "test"]
             ;["shell" "./node_modules/karma/bin/karma" "start" "--single-run"]
             ]})
