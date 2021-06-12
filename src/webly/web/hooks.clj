(ns webly.web.hooks
  (:require
   [taoensso.timbre  :refer [debug info warn error]]))

(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (error {:what :uncaught-exception
             :exception ex
             :where (str "Uncaught exception on" (.getName thread))}))))

#_(.addShutdownHook
   (Runtime/getRuntime)
   (Thread.
    (shutdown-agents)))

#_(let [t (new Thread (fn []
                        (warn "shutting down...")
                        (shutdown-agents)))]
    (.. java.lang.Runtime (getRuntime) (addShutdownHook t)))