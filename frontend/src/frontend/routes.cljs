 (ns frontend.routes
   (:require
    [taoensso.timbre :refer-macros [info infof error]]
    [reagent.core :as r]
    [bidi.bidi :as bidi]
    [pushy.core :as pushy]
    [cemerick.url :as url]))

; bidi does not handle query params
; idea how to solve the problem: https://github.com/juxt/bidi/issues/51

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
(defn reset-current! [trigger match]
  (when (not (= match @current))
    (info "reset-current! " match "trigger: " trigger)
    (reset! current match)))

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
    ;(info "match: " match) 
    ; {{} nil, 
    ;   :route-params {:location "Bali"}, 
    ;   :handler :demo/party
    ;   :tag  :wunderbar}
    (if match
      (let [{:keys [handler route-params tag]} match
            route {:handler handler
                   :query-params query-params
                   :route-params (or route-params {})
                   :tag (or tag nil)}]
        route)
      (do (error "no route found for " path-with-qp)
          {:handler :webly/unknown-route
           :url path-with-qp}))))

(declare hard-redirect)

(defn pushy-goto! [match]
  (reset-current! "pushy/goto" match)
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

; goto! 

(defn- params->map [params]
  (let [pairs (partition 2 params)
        add (fn [m [k v]]
              (assoc m k v))]
    (reduce add {} pairs)))

(defn goto-route! [route]
  (let [url (route->url route)]
    (infof "bidi/goto route: %s url: %s" route url)
    (reset-current! "goto-route" route)
    (pushy/set-token! history url)))

(defn goto! [handler & params]
  (let [params-map (params->map params) ; params is a map without {} example:  :a 1 :b 2  
        ; _ (error "params map: " params-map)
        route-params (dissoc params-map :query-params :tag)
        query-params (or (:query-params params-map) {})
        tag (:tag params-map)
        route {:handler handler
               :route-params route-params
               :query-params query-params
               :tag tag}]
    (goto-route! route)))

(defn nav! [url]
  (let [{:keys [handler] :as h} (path->route @routes url {})]
    (info "nav!: " h) ; {:handler :demo/help, :query-params {}, :route-params {}}
    (if (= handler :webly/unknown-route)
      (set! (.-location js/window) url)
      (do (reset-current! "bidi/goto!" h)
          (pushy/set-token! history url)))))

