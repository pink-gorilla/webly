 (ns webly.web.routes
   (:require
    [taoensso.timbre :refer-macros [info]]
    [reagent.core :as r]
    [bidi.bidi :as bidi]
    [pushy.core :as pushy]
    [cemerick.url :as url]))

;; take some tricksfrom this 
;; https://github.com/timgilbert/haunting-refrain-posh/blob/develop/src/cljs/haunting_refrain/fx/navigation.cljs


; query param handling
; bidi does not handle query params
; idea how to solve the problem:
; https://github.com/juxt/bidi/issues/51
(defn match-route-with-query-params
  [routes path & {:as options}]
  (let [query-params (->> (:query (cemerick.url/url path))
                          (map (fn [[k v]] [(keyword k) v]))
                          (into {}))]
    (-> (bidi/match-route* routes path options)
        (assoc :query-params query-params))))

; bidi swagger:
; https://github.com/pink-junkjard/bidi-swagger

(defn window-query-params []
  (info "window query params: href: " (.. js/window -location -href))
  (-> (.. js/window -location -href)
      (url/url)
      ; (url/query->map)
      :query))

(def query-params (r/atom nil))

(defn set-query-params [params]
  (info "setting query params to: " params)
  (reset! query-params params))

(defn set-initial-query-params []
  (let [params (window-query-params)]
    (info "initial query params: " params)
    (set-query-params params)))

; bidi routing

(def current (r/atom nil))
(def routes (r/atom nil))

(defn bidi-goto! [match]
  (info "setting current page to handler: " match)
  (reset! current match))

(defn on-url-change [path & options]
  (let [options (or options {})]
    (info "url did change to: " path " options:" options)
    ;(bidi/match-route @routes path)
    (match-route-with-query-params @routes path options))) ; options

; see: 
; https://github.com/clj-commons/pushy

(def history
  (pushy/pushy bidi-goto! on-url-change))

(defn link
  ([handler]
   (info "link for handler: " handler "no-route-params")
   (let [url (bidi/path-for @routes handler)]
     (info "bidi link url: " url)
     url))
  ([handler route-params]
   (info "link for handler: " handler "route-params: " route-params)
   (let [url (apply (partial bidi/path-for @routes) handler route-params)]
     (info "bidi link url: " url)
     url)))

(defn nav! [url]
  (set! (.-location js/window) url))

; old version only suports query params
#_(defn goto!-OLD [handler & params]
    (let [[qp] params]
      (info "goto! handler: " handler " query-params: " qp)
      (reset! current {:handler handler})
      (if qp
        (let [url (str (link handler) "?" (url/map->query qp))]
          (set-query-params qp)
          (pushy/set-token! history url))
        (pushy/set-token! history (link handler)))))

(defn params->map [params]
  (let [pairs (partition 2 params)
        add (fn [m [k v]]
              (assoc m k v))]
    (reduce add {} pairs)))

(defn map->params [m]
  (let [add (fn [acc [k v]]
              (conj acc k v))]
    (reduce add [] m)))

(defn goto! [handler & params]
  (let [_ (info "goto! " handler "params: " params)
        m (params->map params) ; params is a map without {} example:  :a 1 :b 2  
        m-route (dissoc m :query-params)
        ;_ (info "m-route" m-route)
        p-route (map->params m-route)
        ;_ (info "p-route" p-route)
        p-query (:query-params m)
        base-url (if p-route
                   (link handler p-route)
                   (link handler))]
    (info "goto! handler: " handler " route-p: " p-route  "query-p (map):" p-query "base-url:" base-url)
    (reset! current {:handler handler})
    (if p-query
      (let [url (str base-url "?" (url/map->query p-query))]
        (set-query-params p-query)
        (pushy/set-token! history url))
      (do (set-query-params {})
          (pushy/set-token! history base-url)))))


;; not yet used
;; todo : hard redirects for backend routes or exernal links


(defn hard-redirect! [history place]
  (pushy/stop! history)
  (set! (.-location js/window) place))