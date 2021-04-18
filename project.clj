(defproject org.pinkgorilla/webly "0.1.29"
  :description "web (server / reagent) helper library."
  :url "https://github.com/pink-gorilla/webly"
  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/release_username
                                     :password :env/release_password
                                     :sign-releases false}]]

  :min-lein-version "2.9.4" ; nrepl 0.7.0

  ;:jvm-opts ["-Dtrust_all_cert=true" ; used when ssl certs are fucked up
  ;             ;"-Djavax.net.ssl.trustStore=/home/andreas/.keystore"
  ;           ]


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


  :managed-dependencies [[joda-time "2.10.10"]
                         [clj-time "0.15.2"]
                         [com.fasterxml.jackson.core/jackson-core "2.12.3"]
                         [com.cognitect/transit-cljs "0.8.264"]
                         [com.cognitect/transit-clj "1.0.324"]
                         [com.cognitect/transit-java "1.0.343"]
                         [org.apache.httpcomponents/httpcore "4.4.14"]
                         [com.google.javascript/closure-compiler-unshaded "v20200719"]
                         [org.apache.httpcomponents/httpasyncclient "4.1.4"]
                         [commons-codec "1.15"]
                         ;[com.google.code.findbugs/jsr305 "3.0.2"]
                         ;[org.ow2.asm/asm "8.0.1"]
                         [org.clojure/tools.reader "1.3.5"] ; sente, sci, encore
                         [cljsjs/react-dom "16.13.0-0"] ; reframe + reframe 10x
                         [io.undertow/undertow-core "2.2.4.Final"] ; ring-undertow and shadow-cljs
                         ]

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async "1.3.610"]
                 [com.taoensso/timbre "5.1.2"] ; clj/cljs logging
                 [clojure.java-time "0.3.2"]

                 ; encoding
                 [org.clojure/data.json "2.1.0"]
                 [luminus-transit "0.1.2"]
                 [cheshire "5.10.0"]  ; JSON parsings and pretty printing
                 [com.taoensso/encore "3.18.0"]

                 ; ring + middlewares
                 [ring/ring-core "1.9.1"]
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
                 [ring/ring-devel "1.9.2"] ; reload middleware

                 ; routing
                 [bidi "2.1.6"]
                 [clj-commons/pushy "0.3.10"]
                 [com.cemerick/url "0.1.1"]  ; url query-strings

                 ; server side templating
                 [hiccup "1.0.5"]

                 ; frontend
                 [reagent "0.10.0" :exclusions [org.clojure/tools.reader
                                                cljsjs/react
                                                cljsjs/react-dom]]
                 [re-frame "1.0.0"]
                 [cljs-ajax "0.8.3"] ; needed for re-frame/http-fx

                 [day8.re-frame/http-fx "0.2.3"  ; reframe based http requests
                  :exclusions [[re-frame]]] ; a more modern reframe comes from webly

                 [keybind "2.2.0"]

                 [day8.re-frame/re-frame-10x "0.6.2"]
                 [day8.re-frame/tracing "0.6.2"] ; https://github.com/day8/re-frame-debux
                 ;[day8.re-frame/tracing-stubs "0.6.2"]

                 ;shadow
                 ; shadow-cljs MAY NOT be a dependency in lein deps :tree -> if so, bundler will fail because shadow contains core.async which is not compatible with self hosted clojurescript
                ; [thheller/shadow-cljs "2.8.81"]
                 [thheller/shadow-cljs "2.10.19"]
                 [thheller/shadow-cljsjs "0.0.21"]
                 [org.clojure/clojurescript "1.10.773"]

                 [resauce "0.1.0"] ; resources
                 [cprop "0.1.17"] ; config management
                 [akiroz.re-frame/storage "0.1.4"] ; localstorage 

                 ; supported servers
                 [info.sunng/ring-jetty9-adapter "0.14.0"] ; last version with java 9
                 ;[luminus/ring-undertow-adapter "1.2.0"]
                 ;[http-kit "2.5.3"]

                 ; websockets
                 [com.taoensso/sente "1.16.2"
                  :exclusions [aleph
                               org.clojure/core.async
                               org.immutant
                               info.sunng/ring-jetty9-adapter]] ;  websocket

                 [fipp "0.6.23"] ; edn pretty printing
                 ]

  :target-path  "target/jar"
  :source-paths ["src"]
  :test-paths ["test"]
  :resource-paths  ["resources"  ; webly resources (svg/img)
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

             :dev {:dependencies [;[clj-kondo "2020.06.21"] ;
                                  [ring/ring-mock "0.4.0"]]
                   :plugins      [[lein-cljfmt "0.6.6"]
                                  ;[lein-cloverage "1.1.2"]
                                  [lein-shell "0.5.0"]
                                  [lein-ancient "0.6.15"]]
                   :aliases      {"clj-kondo" ["run" "-m" "clj-kondo.main"]}
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
