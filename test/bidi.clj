


(comment
  (bidi/path-for demo-routes-api :demo/main)
  (bidi/path-for demo-routes-api :ui/explorer)
  (bidi/path-for demo-routes-api :ui/notebook)
  (bidi/path-for demo-routes-api :ui/unknown)

  (bidi/path-for demo-routes-api :api/explorer)
  (bidi/path-for demo-routes-api  :api/notebook-load)

  (bidi.bidi/match-route demo-routes-api "/explorer")
  (bidi.bidi/match-route demo-routes-api "/api/explorer" :request-method :get)
;
  )

#_(defmethod page-contents :index []
    [:span
     [:h1 "Routing example: Index"]
     [:ul
      [:li [:a {:href (bidi/path-for app-routes :section-a)} "Section A"]]
      [:li [:a {:href (bidi/path-for app-routes :section-b)} "Section B"]]
      [:li [:a {:href (bidi/path-for app-routes :missing-route)} "Missing-route"]]
      [:li [:a {:href "/borken/link"} "Borken link"]]]])
