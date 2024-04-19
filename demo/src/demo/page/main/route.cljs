(ns demo.page.main.route
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [frontend.page.viewer :refer [refresh-page]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

;; BIDI ROUTING

(defn demo-routing []
  [block2 "routes"
   [:div.flex.flex-col
    ; routing test. no parameters
    [link-dispatch [:bidi/goto 'demo.page.help/help-page]
     "help! "]
    ; routing test. parameters
    [link-dispatch [:bidi/goto 'demo.page.party/party-page :location "Vienna"]
     "party in vienna (test for route-params)"]
    [link-dispatch [:bidi/goto 'demo.page.party/party-page :location "Bali" :query-params {:expected-guests 299}]
     "party in Bali (test for query-params)"]
    [link-dispatch [:bidi/goto-route {:handler 'demo.page.party/party-page
                                      :route-params {:location "Panama"}
                                      :query-params {:expected-guests 44}
                                      :tag nil}]
     "party in Panama (test for goto-route)"]
    ; routing test. bidi-tags
    [link-dispatch [:bidi/goto 'demo.page.job/job-page]
     "job! (test of bidi tags)"]
    [link-dispatch [:bidi/goto 'demo.page.job/job-page]
     "job2! (test of bidi tags)"]
   ; routing - external url    
    [link-dispatch [:bidi/goto "https://google.com"]
     "google (test for external url)"]
    ; routing - unknown url
    [link-dispatch [:bidi/goto 'demo.page.unknown/unknown-page]
     "save-as (test for not implemented)"]

    [link-href "/api/test" "demo api test"]
    [link-href "/api/time" "demo api time"]
    [link-fn refresh-page "refresh page"]]])