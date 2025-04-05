(ns demo.page.main.route
  (:require
   [demo.helper.ui :refer [link-href block2 link]]))

;; ROUTING

(defn demo-routing []
  [block2 "routes"
   [:div.flex.flex-col
    ; routing test. no parameters
    [link ['demo.page.help/help-page] "help! "]
    ; routing test. parameters
    [link ['demo.page.party/party-page {:location "Vienna"}]
     "party in vienna (test for route-params)"]
    [link ['demo.page.party/party-page {:location "Bali"} {:expected-guests 299}]
     "party in Bali (test for query-params)"]
    [link ['demo.page.party/party-page
           {:location "Panama"}
           {:expected-guests 44}
           {:mood #{:hot :beach :beer}}]
     "party in Panama (test for goto-route)"]
    ; routing test. 
    [link ['demo.page.job/job-page {} {:priority :high}]
     "job!"]
    [link ['demo.page.job/job-page {} {:priority :high} {:index 3}] "job2! "]
    ; api
    [link-href "/api/test" "demo api test"]
    [link-href "/api/time" "demo api time"]]])