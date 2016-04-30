(ns sound-flour.FunctionInputStream
  (:require
    [sound-flour.byte-converters :refer :all])
  (:gen-class
    :name sound-flour.FunctionInputStream
    :state state
    :extends java.io.InputStream
    :init init
    :constructors {[clojure.lang.Fn java.lang.Number] []}
    :main false))

(def ^:private buffer-size 1024)

(def ^:private converter {
  8 (comp list ubyte)
  16 short-little-endian
  32 int-little-endian
  64 long-little-endian})

(defn -init [func bits-per-sample]
  [[] (ref { :fn (comp (converter bits-per-sample) func) :t 0 :buf nil})])

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
