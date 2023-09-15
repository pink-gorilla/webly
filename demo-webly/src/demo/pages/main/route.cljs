(ns demo.pages.main.route
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [frontend.page.viewer :refer [refresh-page]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

;; BIDI ROUTING

(defn demo-routing []
  [block2 "routes"
   [:div.flex.flex-col
    [link-dispatch [:bidi/goto "/help"]
     "help! (as an url)"]
    [link-dispatch [:bidi/goto "https://google.com"]
     "google (test for external url)"]
    [link-dispatch [:bidi/goto :demo/help]
     "help! (map with optional args))"]

    [link-dispatch [:bidi/goto :demo/save-non-existing]
     "save-as (test for not implemented)"]
    [link-dispatch [:bidi/goto :demo/party :location "Vienna"]
     "party in vienna (test for route-params)"]
    [link-dispatch [:bidi/goto :demo/party :location "Bali" :query-params {:expected-guests 299}]
     "party in Bali (test for query-params)"]
    [link-dispatch [:bidi/goto-route {:handler :demo/party
                                      :route-params {:location "Panama"}
                                      :query-params {:expected-guests 44}
                                      :tag nil}]
     "party in Panama (test for goto-route)"]

    [link-dispatch [:bidi/goto "/job"]
     "job! (test of bidi tags)"]
    [link-dispatch [:bidi/goto "/job2"]
     "job2! (test of bidi tags)"]

    [link-href "/api/test" "demo api test"]
    [link-href "/api/time" "demo api time"]
    [link-fn refresh-page "refresh page"]]])