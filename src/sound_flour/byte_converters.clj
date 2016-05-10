(ns sound-flour.byte-converters)

(defn- little-endian [size]
  (let [rng (range 0 (* size 8) 8)]
    (fn [n]
      (map #(unchecked-byte (unsigned-bit-shift-right n %)) rng))))

(def short-little-endian
  (little-endian 2))

(def int-little-endian
  (little-endian 4))

(def long-little-endian
  (little-endian 8))

(defn byte->int [b]
  (int (if (neg? b) (+ b 256) b)))
