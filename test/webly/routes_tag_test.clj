(ns webly.routes-tag-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [bidi.bidi :as bidi]
   [incognito.edn :refer [read-string-safe]]
   [cognitect.transit :as transit]
   [incognito.transit :refer [incognito-write-handler incognito-read-handler]])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]
           [bidi.bidi TaggedMatch]
           [com.cognitect.transit.impl WriteHandlers$MapWriteHandler]))

(def routes
  ["/" {"" :demo/main
        "x" :demo/x
        "z" (bidi/tag :demo/z :trott)}])

; tags can be used to pass non route-specific parameters to handler

(deftest route-tag-test []
  (is (= (bidi/match-route routes "/x") {:handler :demo/x}))
  (is (= (bidi/match-route routes "/z") {:handler :demo/z :tag :trott})))

(defrecord Bar [a b])

;; edn

(defmethod print-method Bar [v ^java.io.Writer w]
  (.write w (str "#webly.routes-tag-test.Bar" (into {} v))))

(deftest edn-tag-test []
  (let [bar (map->Bar {:a [1 2 3] :b {:c "Fooos"}})]
    (is (= bar (->> bar
                    pr-str
                    (read-string-safe {})
                    pr-str
                    (read-string-safe {'webly.routes-tag-test.Bar map->Bar}))))))

;; transit

(def write-handlers (atom {'webly.routes-tag-test.Bar (fn [bar] bar)}))
(def read-handlers (atom {'webly.routes_tag_test.Bar map->Bar}))

; fails because records get converted to map

(deftest transit-test []
  (let [t (map->Bar {:a [1 2 3] :b {:c "Fooos"}})]
    (with-open [baos (ByteArrayOutputStream.)]
      (let [writer (transit/writer baos :json {:handlers {Bar
                                                          (incognito-write-handler
                                                           write-handlers)}})]
        (transit/write writer t)
        (let [bais (ByteArrayInputStream. (.toByteArray baos))
              reader (transit/reader bais :json {:handlers {"incognito"
                                                            (incognito-read-handler
                                                             read-handlers)}})
              t2 (transit/read reader)]
          (is (= t t2)))))))

; transit bidi

(def write-handlers2 (atom {'bidi.bidi.TaggedMatch (fn [bar] bar)}))
(def read-handlers2 (atom {'bidi.bidi.TaggedMatch bidi.bidi/map->TaggedMatch}))

(deftest seri-tag-test []
  (let [t (bidi/tag :demo/z :trott)]
    (with-open [baos (ByteArrayOutputStream.)]
      (let [writer (transit/writer baos :json {:handlers {bidi.bidi.TaggedMatch
                                                          (incognito-write-handler
                                                           write-handlers2)}})]
        (transit/write writer t)
        (let [bais (ByteArrayInputStream. (.toByteArray baos))
              reader (transit/reader bais :json {:handlers {"incognito"
                                                            (incognito-read-handler
                                                             read-handlers2)}})
              t2 (transit/read reader)]
          (is (= t t2)))))))
