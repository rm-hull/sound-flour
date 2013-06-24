(ns sound-flour.handler
  (:use [compojure.core]
        [clojure.pprint]
        [hiccup.middleware :only [wrap-base-url]]
        [ring.middleware.params :only [wrap-params]] 
        [ring.util.response :only [response content-type]])
  (:require [compojure.handler :as handler]))


(defroutes app-routes
  (GET "/" [] (-> 
                (response "We're not quite there yet")
                (content-type "text/plain"))))

(def app 
  (-> 
    (handler/site app-routes)
    (wrap-base-url)
    (wrap-params)))
