(ns sound-flour.oscillators)

(def buffer-len 8000)

(def sample-rate 8000)

(defn sine-wave [frequency amplitude]
  (let [data (->>
                (range buffer-len)
                (mapv #(* amplitude (Math/sin (/ (* 2 Math/PI % frequency) sample-rate))))
                )]
    (fn [t] (data (mod t buffer-len)))))


