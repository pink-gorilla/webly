(ns webly.user.markdown.views
  (:require
   [clojure.walk]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [re-frame.core :refer [subscribe dispatch]]
   ;[pinkgorilla.ui.ui.markdown :refer [markdown]]
   ["marked" :as marked]))


; stolen from gorilla-ui
; but we dont want dependency to it in webly.


(defn ^{:category :ui}
  markdown
  "reagent markdown render component
   usage:
    [markdown markdown-string]"
  [md]
  (if (nil? md) ; marked will crash on (nil? md), so we catch nil. 
    [:p "Empty Markdown"]
    [:div.gorilla-markdown
     {:dangerouslySetInnerHTML
      {:__html (marked md)}}]))

(defn markdown-view [document]
  [:div.free-markup.prose
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

(defn markdown-explorer [file]
  (let [;{:keys [expected-guests]} query-params
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

(defn markdown-explorer-react [{:keys [file]}]
  [markdown-explorer file])