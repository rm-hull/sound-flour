(ns sound-flour.byte-converters)

(defn- unsigned [f bits]
  (let [max-val (bit-shift-left 1 (dec bits))
        neg (bit-shift-left 1 bits)]
    (fn [n]
      (f
        (if (>= n max-val)
          (- n neg)
          n)))))

(def ubyte (unsigned byte 8))
(def ushort (unsigned short 16))
(def ulong (unsigned long 32))

(defn- little-endian [size]
  (let [rng (range 0 (* size 8) 8)]
    (fn [n]
      (map #(ubyte (bit-and 0xff (unsigned-bit-shift-right n %))) rng))))

(def short-little-endian
  (little-endian 2))

(def int-little-endian
  (little-endian 4))

(def long-little-endian
  (little-endian 8))

(defn byte->int [b]
  (int (if (neg? b) (+ b 256) b)))
