
   [ring.mock.request :refer [request] :rename {request mock-request}]

(comment

     ; the app is greedy.
  ["app" (-> #'app-handler)]
  ["app/" (-> #'app-handler)]
  ["/app" (-> #'app-handler)]
  ["/app/" (-> #'app-handler)]
    ;[#"^.*$" #'app-handler]  ;; redirect / to index.html


  [["/user/" :userid "/article"]
   (fn [req] {:status 201 :body (:route-params req)})]

  #_["/test" (-> #'test-handler (bidi.bidi/tag :index))]

  #_["/blog"
     [["/index.html" (fn [req] {:status 200 :body "Index"})]
      [["/b"] 'blog-article-handler]
      [["/article/" :id ".html"] 'blog-article-handler]
      [["/archive/" :id "/" :page ".html"] 'archive-handler]]]

                       ;["/images/" 'image-handler]
                       ; (fn [req] {:status 200 :body "Not found"})


  (def routes-app
    ["" {"" :index
         "a-items" {"" :a-items
                    ["/" :item-id] :a-item}
         "section-a" {"" :section-a
                      ["/item-" :item-id] :a-item}
         "section-b" :section-b
         "/this-route" {""                {:get :handler-1}
                        "/something"      {:get :handler-2}
                        "/something-else" {:get :handler-3}}
         "missing-route" :missing-route
         true :four-o-four}])

  (bidi/match-route routes-app "/this-route" :request-method :get)
  (bidi/match-route routes-app "/this-route/something" :request-method :get)
  (bidi/match-route routes-app "/this-route/something-else" :request-method :get)
  (bidi/match-route routes-app "a-items" :request-method :get)
  (bidi/match-route routes-app "a-items/kjhkjk" :request-method :get)

  (def serve-index-page
    (bidi/handler (java.io.File. "resources/public/index.html")))

;; the relevant routing part
  (def routes2
  ;["/"
   ;[#"^$" serve-index-page]                          ;; redirect / to index.html
    ["" {"" (bidi.ring/resources {:prefix "public/"})}]
;  ]
    )

  (bidi/match-route routes2 "/jjjj" :request-method :get)

  (defn blog-article-handler [req]
    {:k 7})

  (defn archive-handler [req]
    {:k 7})

  (defn image-handler [req]
    {:k 7})

  (GET "/" req (app-handler req))

  (handler (mock-request :get "/blog/index.html"))
;{:status 200 :body "Index"}))
  (handler (mock-request :get "/test"))

  (handler (mock-request :get "/user/4/article"))
  (handler (-> (mock-request :put "/user/8888/article")
               (assoc :params {"foo" "bar"})))

  (handler (mock-request :get "kjhjhjk"))

  (handler (mock-request :get "/blog/b"))
  (handler (mock-request :get "/blog/article/8.html"))

; huge comment
  )
