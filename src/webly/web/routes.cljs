 (ns webly.web.routes
   (:require
    [taoensso.timbre :refer-macros [info error]]
    [reagent.core :as r]
    [bidi.bidi :as bidi]
    [pushy.core :as pushy]
    [cemerick.url :as url]))


; bidi does not handle query params
; idea how to solve the problem:nhttps://github.com/juxt/bidi/issues/51


(defn window-query-params []
  (info "window query params: href: " (.. js/window -location -href))
  (-> (.. js/window -location -href)
      (url/url)
      ; (url/query->map)
      :query))

;; not yet used
;; todo : hard redirects for backend routes or exernal links


(defonce routes (r/atom nil))

(defn link
  ([handler]
   ;(info "link for handler: " handler "no-route-params")
   (let [url (bidi/path-for @routes handler)]
     ;(info "bidi link url: " url)
     url))
  ([handler route-params]
   ;(info "link for handler: " handler "route-params: " route-params)
   (let [url (apply (partial bidi/path-for @routes) handler route-params)]
     ;(info "bidi link url: " url)
     url)))

(defn- map->params [m]
  (let [add (fn [acc [k v]]
              (conj acc k v))]
    (reduce add [] m)))

(defn route->url [{:keys [handler query-params route-params]}]
  (let [url-handler (if (empty? route-params)
                      (link handler)
                      (link handler (map->params route-params)))]
    (if (empty? query-params)
      url-handler
      (str url-handler "?" (url/map->query query-params)))))

(defonce current (r/atom nil))

;; take some tricks from this 
;; https://github.com/timgilbert/haunting-refrain-posh/blob/develop/src/cljs/haunting_refrain/fx/navigation.cljs




; pushy


(defn- path->route
  [routes path-with-qp & {:as options}]
  (let [{:keys [path query]} (cemerick.url/url path-with-qp)
        query-params (->> query
                          (map (fn [[k v]] [(keyword k) v]))
                          (into {}))
        match (bidi/match-route* routes path options)] ;     ;(bidi/match-route @routes path)
    ;(info "match: " match)  ; weird {} ...  {{} nil, :route-params {:location "Bali"}, :handler :demo/party}
    (if match
      (let [handler (:handler match)
            route-params (:route-params match) ;(dissoc match :handler)
            route {:handler handler
                   :query-params query-params
                   :route-params (or route-params {})}]
        ;(info "path->route " path-with-qp "route: " route)
        route)
      (do (error "no route found for " path-with-qp)
          {:handler :webly/unknown-route
           :url path-with-qp}))))

(declare hard-redirect)

(defn pushy-goto! [match]
  (info "pushy/goto: " match)
  (reset! current match)
  (when (=  :webly/unknown-route (:handler match))
    (hard-redirect (:url match))))

(defn on-url-change [path & options]
  (let [options (or options {})]
    (info "url did change to: " path) ; " options:" options
    (path->route @routes path options))) ; options

; see: 
; https://github.com/clj-commons/pushy

(def history
  (pushy/pushy pushy-goto! on-url-change))

(defn hard-redirect! [url]
  (info "hard redirect to: " url)
  (pushy/stop! history)
  (set! (.-location js/window) url))

; bidi swagger:
; https://github.com/pink-junkjard/bidi-swagger


;(def query-params (r/atom nil))

; goto! 

(defn- params->map [params]
  (let [pairs (partition 2 params)
        add (fn [m [k v]]
              (assoc m k v))]
    (reduce add {} pairs)))

(defn goto! [handler & params]
  (let [;_ (info "goto! " handler "params: " params)
        params-map (params->map params) ; params is a map without {} example:  :a 1 :b 2  
        route-params (dissoc params-map :query-params)
        query-params (:query-params params-map)
        ;_ (info "m-route" m-route)
        ;route-params (map->params m-route)
        ;_ (info "p-route" p-route)
        route {:handler handler
               :route-params route-params
               :query-params (or query-params {})}
        url (route->url route)]
    (info "bidi/goto! route:" route "url:" url)
    (reset! current route)
    (pushy/set-token! history url)))

(defn nav! [url]
  (set! (.-location js/window) url))
