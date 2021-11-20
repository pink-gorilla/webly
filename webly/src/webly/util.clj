(ns webly.util)

(defmacro lazy-component [the-sym]
  `(webly.util/lazy-component* (shadow.lazy/loadable ~the-sym)))