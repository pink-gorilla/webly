(ns webly.user.markdown.view
  (:require
   [clojure.walk]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [re-frame.core :refer [subscribe dispatch]]
   [pinkgorilla.ui.ui.markdown :refer [markdown]]
   [webly.web.handler :refer [reagent-page]]))

(defn markdown-view [document]
  [:div.free-markup.prose
   [:link {:rel "stylesheet" :href "/r/notebook-ui/prose.css"}]
   [markdown document]])

(defn err [document message]
  [:div.m-6.p-6.bg-red-300.border-solid
   [:h1 message]
   (when document
     [:p (pr-str (:error document))])])

(defn markdown-viewer [file document]
  [:div
       ;[:h1 "Markdown Viewer"]
       ;[:p file]
       ;[:p @document]
   (cond

     (nil? @document)
     [err  @document "requesting document"]

     (= :markdown/loading @document)
     [err  @document "Loading .."]

     (:error @document)
     [err @document "Error Loading Markdown"]

     :else
     [markdown-view @document])])

(defn markdown-page [{:keys [route-params query-params handler]}]
  (let [{:keys [file]} route-params
        ;{:keys [expected-guests]} query-params
        _ (info "showing markdown file: " file)
        available (subscribe [:markdown/available])
        showing-file (subscribe [:markdown/showing-file])
        showing-doc (subscribe [:markdown/showing-doc])]
    (when (and file (not (= file  @showing-file)))
      (info "loading markdown: " file)
      (dispatch [:markdown/load file]))
    [:div.flex.flex-row
     [:div.flex-none.w-24.bg-yellow-300
      (for [a @available]
        ^{:key a}
        [:p [:a.b-2
             {:href (str "/md/" a)}
             (subs a 0 (- (count a) 3))]])]
     [:div.flex-auto
      [markdown-viewer file showing-doc]]]))

(defmethod reagent-page :ui/markdown [args]
  [markdown-page args]) ; @query-params