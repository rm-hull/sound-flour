(ns sound-flour.handler
  (:require
    [compojure.handler :as handler]
    [compojure.core :refer [defroutes GET]]
    [ring.util.response :refer [response content-type]]
    [ring.logger.timbre :as logger.timbre]
    [metrics.ring.expose :refer [expose-metrics-as-json]]
    [metrics.ring.instrument :refer [instrument]]
    [infix.macros :refer [infix]]
    [sound-flour.encoder :refer [audio-stream clip scale]]
    [sound-flour.oscillators :refer [sine-wave]])
  (:import
    [sound-flour FunctionInputStream]))

(def sin
  (comp
    scale
    clip
    (sine-wave 440 0.5))) ; 440 Hz = Middle C

(defn a [t]
  (infix (t * (t >> 5 | t >> 8)) >> (t >> 16)))

(defn b [t]
  (infix (t * ((t >> 12 | t >> 8) & 63 & t >> 4))))

(defn c [t]
  (infix  (t >> 6 | t | t >> (t >> 16)) * 10 + ((t >> 11) & 7)))

(defn upscale-8-bit [v]
  (infix 0xff * (0xff & v)))

(defroutes app-routes
  (GET "/sine-wave" []
    (->
      (response (audio-stream 8000 16 (FunctionInputStream. sin)))
      (content-type "audio/x-wav")))

  (GET "/a" []
    (->
      (response (audio-stream 8000 16 (FunctionInputStream. (comp upscale-8-bit a))))
      (content-type "audio/x-wav")))

  (GET "/b" []
    (->
      (response (audio-stream 8000 16 (FunctionInputStream. (comp upscale-8-bit b))))
      (content-type "audio/x-wav")))

  (GET "/c" []
    (->
      (response (audio-stream 8000 16 (FunctionInputStream. (comp upscale-8-bit c))))
      (content-type "audio/x-wav"))))

(def app
  (->
    app-routes
    (logger.timbre/wrap-with-logger)
    (expose-metrics-as-json)
    (instrument)
    (handler/api)))
