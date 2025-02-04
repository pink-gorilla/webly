(ns demo.page.main.lazy
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [shadowx.build.lazy :refer-macros [wrap-lazy] :refer [available]]
   [shadowx.module.build :refer [load-namespace-raw load-namespace webly-resolve]]
   [demo.helper.ui :refer [link-fn block2]]))

(defn lazy1 []
  (let [ui-add (wrap-lazy snippets.snip/ui-add)]
    [:div.bg-red-500
     [:p "I am lazy:"]
     [ui-add 7 7]]))

(defn load-namespace-raw-highcharts [& _]
  (let [rp (load-namespace-raw 'ui.highcharts)]
    (p/then rp (fn [r]
                 (println "*** NS LOAD SUCCESS: " r)))
    (p/catch rp (fn [x]
                  (println "*** NS LOAD ERROR: " x)))))

(defn load-namespace-test-bad [& _]
  (let [rp (load-namespace-raw :bongistan1234567)]
    (p/then rp (fn [r]
                 (println "*** NS LOAD SUCCESS: " r)))
    (p/catch rp (fn [x]
                  (println "*** NS LOAD ERROR: err: " x)))))

(defn load-namespace-highcharts [& _]
  (let [rp (load-namespace 'ui.highcharts)]
    (p/then rp (fn [r]
                 (println "*** NS HIGHCHARTS LOAD SUCCESS: " r)))
    (p/catch rp (fn [x]
                  (println "*** NS HIGHCHARTS LOAD ERROR: err: " x)))))

(defn resolve-highcharts [& _]
  (let [rp (webly-resolve 'ui.highcharts/highstock)]
    (p/then rp (fn [r]
                 (println "*** webly resolve SUCCESS: " r)))
    (p/catch rp (fn [x]
                  (println "*** webly resolve ERROR: err: " x)))))
(defn demo-lazy []
  (let [show-lazy1 (r/atom false)]
    (fn []
      [block2 "lazy ui"
       [link-fn #(reset! show-lazy1 true) "lazy load1"]

       [link-fn load-namespace-raw-highcharts "lazy-highcharts-raw"]
       [link-fn load-namespace-test-bad "lazy-non-existing-namespace"]
       [link-fn load-namespace-highcharts "lazy-highcharts"]
       [link-fn resolve-highcharts "webly-resolve-highcharts"]
       [:div "loaded lazy renderer: " (pr-str (available))]
       (when @show-lazy1
         [lazy1])
      ])))

