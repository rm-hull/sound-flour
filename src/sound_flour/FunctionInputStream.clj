(ns sound-flour.FunctionInputStream
  (:require
    [sound-flour.byte-converters :refer [short-little-endian byte->int]])
  (:gen-class
    :name sound-flour.FunctionInputStream
    :state state
    :extends java.io.InputStream
    :init init
    :constructors {[clojure.lang.Fn] []}
    :main false))

(defn -init [func]
  [[] (ref { :fn func :t 0 :buf nil})])

(defn -read-void [this]
  (let [state (.state this)
        buf (:buf @state)]
    (dosync
      (if (empty? buf)
        (alter state assoc :buf (short-little-endian ((:fn @state) (:t @state))))
        (alter state update :t inc))
      (let [fst (first (:buf @state))]
        (alter state update :buf next)
        (byte->int fst)))))
