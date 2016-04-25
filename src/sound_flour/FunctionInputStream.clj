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

(def ^:private buffer-size 1024)

(defn -init [func]
  [[] (ref { :fn (comp short-little-endian func) :t 0 :buf nil})])

(defn -read-void [this]
  (let [state (.state this)
        buf (:buf @state)]
    (dosync
      (byte->int
        (first
          (if (empty? buf)
            (let [func  (:fn @state)
                  start (:t @state)
                  end   (+ start buffer-size)
                  buf   (mapcat func (range start end))]
              (alter state assoc :buf (next buf) :t end)
              buf)

            (do
              (alter state update :buf next)
              buf)))))))
