(ns webly.resource-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [ring.mock.request :refer [request] :rename {request mock-request}]
   [bidi.bidi]
   [bidi.ring]
   [webly.web.handler :refer [make-handler]]
   [webly.user.app.handler :refer [app-handler]]
   ;[webly.web.resources] ; side-effects
   [webly.routes :refer [routes-api routes-app]]
   [webly.user.app.routes :refer [make-routes-backend make-routes-frontend]]))

(def routes-backend (make-routes-backend routes-app routes-api))
(def routes-frontend (make-routes-frontend routes-app))

(def handler (make-handler app-handler routes-backend routes-frontend))

(defn GET [url]
  (handler (mock-request :get url)))

(defn content-type [res]
  ;(println "checking content-type for :" res)
  (get-in res [:headers "Content-Type"]))

; resource handler

(testing "ResourcesMaybe"
  (let [foo (bidi.ring/->ResourcesMaybe {:id :foo})
        bar (bidi.ring/->ResourcesMaybe {:id :bar})
        routes ["/" [["foo" foo]
                     ["bar" bar]]]]
    (is (= "/foo" (bidi.bidi/path-for routes foo)))
    (is (= "/bar" (bidi.bidi/path-for routes bar)))))

; resources

(deftest resource-webly []
  (is (= "image/x-icon"
         (-> "/r/webly/favicon.ico" GET content-type))))

(deftest resources-tailwind []
  (is (= "text/css"
         (-> "/r/tailwindcss/dist/tailwind.css" GET content-type))))

(deftest resources-demo []
  (is (= "text/plain"
         (-> "/r/hello.txt" GET content-type)))
  (is (= "image/jpeg"
         (-> "/r/moon.jpg" GET content-type))))

; application

; this actually tests the hiccup conversion
; routing is tested in routes-test

(deftest app-html []
  (is (= "text/html; charset=utf-8"
         (-> "/" GET content-type)))) ; the url can be any valid frontend route

; cljs-bundle

; TODO: this test require bundle compile.
; I am afraid that the bundle might end up in the jar, so I took the test out

#_(deftest cljs-bundle-main []
    (is (= "text/javascript"
           (-> "/r/main.js" GET content-type))))

#_(deftest cljs-bundle-runtime []
    (is (= "text/javascript"
           (-> "/r/cljs-runtime/cljs.core.js" GET content-type)))
    (is (= "application/octet-stream"
           (-> "/r/cljs-runtime/cljs.core.js.map" GET content-type))))