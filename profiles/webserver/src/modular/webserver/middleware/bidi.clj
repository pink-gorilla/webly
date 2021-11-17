(ns modular.webserver.middleware.bidi
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   [bidi.bidi :as bidi]
   [bidi.ring]))

(defn wrap-bidi [routes-backend]
  (fn [{:keys [uri path-info] :as req}]
    (let [path (or path-info uri)
          _ (info "path: " path)
          {:keys [handler route-params] :as match-context}
          (bidi/match-route* routes-backend path req)
          _ (info "path: " path)]
      (info "route params:  " route-params)
      (info "handler: " handler)
      (bidi.ring/request
       handler
       (-> req
           (update-in [:route-params] merge route-params))
       (apply dissoc match-context :handler (keys req))))))

