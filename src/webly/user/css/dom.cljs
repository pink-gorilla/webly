(ns webly.user.css.dom)

; var link = document.createElement ('link');
;link.rel  = 'stylesheet';
;link.href = cssURL;
;document.head.appendChild (link);

;var head  = document.getElementsByTagName('head')[0];
;    var link  = document.createElement('link');
;    link.id   = cssId;
;    link.rel  = 'stylesheet';
;    link.type = 'text/css';
;    link.href = 'http://website.com/css/stylesheet.css';
;    link.media = 'all';
;    head.appendChild(link);

; curently not used
; reagent view works fine.

(defn add-css [href]
  (let [_ (println "adding css: " href)
        head (.-head js/document)
        href (clj->js href)
        link (.createElement js/document "link")]
    (.setAttribute link "href" href)
    (.setAttribute link "rel" "stylesheet")
    (.setAttribute link "type" "text/css")
    (println "link: " href)
    (.appendChild head link)))



