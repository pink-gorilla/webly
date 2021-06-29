(ns webly.encoding.edn
  (:require
   [bidi.bidi :as bidi])
  (:import [bidi.bidi TaggedMatch]))

(def config
  {'bidi.bidi.TaggedMatch bidi/map->TaggedMatch})

(def readers
  {:readers config})