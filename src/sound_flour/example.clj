(ns sound-flour.example)

(def buffer-len 8000)

(def sample-rate 8000)

(defn sine-wave [frequency amplitude]
  (let [data (->> 
                (range buffer-len)
                (map #(* amplitude (Math/sin (/ (* 2 Math/PI % frequency) sample-rate))))
                (into []))]
    (fn [t] (data (mod t buffer-len)))))

(sine-wave 440 0.5) ; 440 Hz = Middle C 

