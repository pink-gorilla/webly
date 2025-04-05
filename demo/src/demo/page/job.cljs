(ns demo.page.job
  (:require
   [demo.helper.ui :refer [link]]))

(defn job-page [;{:keys [_route-params _query-params _handler _tag] :as p}
                match]
  (println "job page parameters: " (:parameters match))
  [:div
   [link ['demo.page.main/main-page] "main"]
   [:h1 "JOB PAGE"]
   [:p "This is a test for reitit route/query parameters."]
   [:p "parameters: " (pr-str (:parameters match))]])

