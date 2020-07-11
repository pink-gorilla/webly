(defproject org.pinkgorilla/webly "0.0.2-SNAPSHOT"
  :description "web (server / reagent) helper library."
  :url "https://github.com/pink-gorilla/webly"
  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/release_username
                                     :password :env/release_password
                                     :sign-releases false}]]

  :release-tasks [["vcs" "assert-committed"]
                  ["bump-version" "release"]
                  ["vcs" "commit" "Release %s"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["deploy"]
                  ["bump-version"]
                  ["vcs" "commit" "Begin %s"]
                  ["vcs" "push"]]

  :target-path  "target/jar"
  :source-paths ["src"]
  :test-paths ["test"]
  :resource-paths  ["resources" ; not from npm
                    "target/demo"] ; bundle

  :managed-dependencies [[joda-time "2.9.9"]
                         [clj-time "0.14.3"]
                         [com.fasterxml.jackson.core/jackson-core "2.11.0"]

                         [com.cognitect/transit-clj "1.0.324"]

                         [com.cognitect/transit-java "1.0.343"]]


  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async "1.1.582"]
                 ;[org.clojure/tools.cli "1.0.194"]
                 [com.taoensso/timbre "4.10.0"] ; clj/cljs logging
                 [clojure.java-time "0.3.2"]

                 ; encoding
                 [org.clojure/data.json "1.0.0"]
                 [luminus-transit "0.1.1"]
                 [cheshire "5.10.0"]  ; JSON parsings
                 [com.taoensso/encore "2.119.0"]

                 ; backend
                 [ring/ring-core "1.8.1"]
                 [hiccup "1.0.5"]
                 ; middlewares
                 [ring/ring-anti-forgery "1.3.0"]
                 [ring-cors "0.1.13"]
                 [ring/ring-defaults "0.3.2"
                  :exclusions [javax.servlet/servlet-api]]
                 ;; [ring.middleware.logger "0.5.0"]
                 [metosin/muuntaja "0.6.7"] ; 30x faster than ring-middleware-format
                 ;[ring-middleware-format "0.7.4"]
                 [ring/ring-json "0.5.0"]
                 [ring-cljsjs "0.2.0"]
                 [bk/ring-gzip "0.3.0"] ; from oz
                 [luminus/ring-ttl-session "0.3.3"]
                  ;[clj-oauth2 "0.2.0"] ;oauth2
                 ;[com.telenordigital.data-insights/clj-oauth2 "0.7.2"]
                 [ring-oauth2 "0.1.4"]
                 ;[expound "0.7.2"] ; see clojurewb

                 ; routing
                 [bidi "2.1.6"]
                 [clj-commons/pushy "0.3.10"]
                 [com.cemerick/url "0.1.1"]  ; url query-strings

                 ; frontend
                 [reagent "0.10.0" :exclusions [org.clojure/tools.reader
                                                cljsjs/react
                                                cljsjs/react-dom]]
                 [re-frame "0.10.9"]
                 [cljs-ajax "0.8.0"] ; needed for re-frame/http-fx
                 [day8.re-frame/http-fx "0.1.6"] ; reframe based http requests
                 [org.pinkgorilla/gorilla-ui "0.2.23"
                  :exclusions [org.clojure/clojurescript]]]

  :profiles {:dev {:source-paths ["profiles/demo/src"]
                   :resource-paths  ["target/demo"
                                     "profiles/demo/resources"]
                   :dependencies [[org.clojure/clojure "1.10.1"]
                                   ; shadow-cljs MAY NOT be a dependency in lein deps :tree -> if so, bundeler will fail because shadow contains core.async which is not compatible with self hosted clojurescript
                                  [thheller/shadow-cljs "2.8.81"]
                                  [thheller/shadow-cljsjs "0.0.21"]
                                  [org.clojure/clojurescript "1.10.773"]

                                  [clj-kondo "2020.03.20"]
                                  ;[http-kit "2.3.0"]
                                  [ring/ring-mock "0.4.0"]]
                   :plugins      [[lein-cljfmt "0.6.6"]
                                  [lein-cloverage "1.1.2"]]
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
  :plugins [[lein-ancient "0.6.15"]]

  :aliases {"bump-version"
            ["change" "version" "leiningen.release/bump-version"]

            "build-shadow-ci"  ^{:doc "Runs demo  via webserver."}
            ["with-profile" "dev" "run" "-m" "shadow.cljs.devtools.cli" "compile" "demo"]

            "demo"  ^{:doc "compiles & runs demo app and serves via webserver."}
            ["with-profile" "dev" "run" "-m" "demo.app"]})
