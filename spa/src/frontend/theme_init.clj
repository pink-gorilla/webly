(ns frontend.theme-init
  (:require
   [frontend.css :as theme]))

(defn config-theme [_module-name _config exts _default-config]
  ;(let [module-name (if (string? module-name)
  ;                    (keyword module-name)
  ;                    module-name)]
  ;  (or (get config module-name) default-config)) 
  (theme/get-theme-config exts))
