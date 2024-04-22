(ns webly.spa.env
  (:require 
    [webly.spa.mode]))

(defn get-mode []
  (webly.spa.mode/get-mode))

(defn get-resource-path []
  (webly.spa.mode/get-resource-path))

(defn get-routing-path []
  (webly.spa.mode/get-routing-path))