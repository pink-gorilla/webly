

(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (println {:what :uncaught-exception
                 :exception ex
                 :where (str "Uncaught exception on" (.getName thread))}))))


(.addShutdownHook 
 (Runtime/getRuntime) 
 (Thread. 
    (shutdown-agents)
          
          ))

