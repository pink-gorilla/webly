(ns frontend.css.dom
  (:require
   [taoensso.timbre :refer-macros [debugf infof warn warnf errorf]]
   [re-frame.core :as rf]))

(defn ^:export on-link-load [x & _]
  (debugf "css loaded: %s" x)
  (rf/dispatch [:css/loading-success x]))

(defn ^:export on-link-error [x & _]
  (errorf "css load error: %s" x)
  ; we just log css load errors.
  ; to calculate the status of number of css links tht need loading
  ; we can say we dont need more loading of a *failed* css download.
  (rf/dispatch [:css/loading-success x]))

(defn add-css-link [href]
  (debugf "adding css: %s" href)
  (rf/dispatch [:css/loading-add href])
  (let [head (.-head js/document)
        href (clj->js href)
        link (.createElement js/document "link")]
    (.setAttribute link "href" href)
    (.setAttribute link "rel" "stylesheet")
    (.setAttribute link "type" "text/css")
    (.setAttribute link "class" "webly")
    (.setAttribute link "onload" (str "frontend.css.dom.on_link_load ('" href "')"))
    (.setAttribute link "onerror"  (str "frontend.css.dom.on_link_error ('" href "')"))
    ;(println "link: " href)
    (.appendChild head link)))

(defn existing-css []
  (let [links (.querySelectorAll js/document "link.webly")
        get-link (fn [link] (.getAttribute link "href"))]
    (map get-link links)))

(defn remove-css-link [href]
  (when-let [elem (.querySelector js/document (str "link.webly[href='" href "']"))]
    (let [parent (.-parentElement elem)]
      ;(error "link: " elem "parent: " parent)
      (debugf "removing css: %s" href)
      (.removeChild parent elem))))

(defn update-css [current]
  (let [current-set (into #{} current)
        existing (existing-css)
        existing-set (into #{} existing)
        css-add (filter #(not (contains? existing-set %)) current)
        css-remove (filter #(not (contains? current-set %)) existing)]
    (infof "css current %s add: %s remove: %s " current css-add css-remove)
    (doall (map add-css-link css-add))
    (doall (map remove-css-link css-remove))))

