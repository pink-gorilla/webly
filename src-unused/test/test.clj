(ns demo.test
  (:require
   [taoensso.timbre :as timbre :refer-macros [tracef debugf info infof warnf errorf info]]
   [bidi.bidi :as bidi]
   [clj-http.client :as client]
   [pinkgorilla.explorer.default-config :refer [explorer-routes-api]]))

(def query-params-load
  {:storagetype "file"
   :filename    "target/test-save.cljg"})

(def query-params-save
  (assoc query-params-load
         :notebook
         ";; gorilla-repl.fileformat = 2\n"))


(bidi/path-for explorer-routes-api :explorer/index)

(def path (bidi/path-for explorer-routes-api :storage/load))

(def url (str "http://localhost:8001" path))

(info "url storage get: " url)

(println url)

(:body (client/post url {:accept :json
                 :query-params query-params-save}))

(:body 
 (client/get url {:accept :json 
                 :query-params query-params-load}))


; notebook has some interesting mock tests.

#_(deftest default-handler-save-test
    (let [resp (#'pinkgorilla.notebook-app.route/default-handler
                (mock/request :post "/save" {:notebook    ";; gorilla-repl.fileformat = 2\n"
                                             :storagetype "file"
                                             :filename    "target/test-save.cljg"
                                            ;; :tokens[default-kernel]: "clj"
                                            ;; :tokens[editor]: "text"
                                            ;; :tokens[github-token]: ""
                                             }))
          status (:status resp)
          headers (:headers resp)
          content-type (get headers "Content-Type")]
      (is (= 200 status))
      (is (= "application/json; charset=utf-8" content-type))))