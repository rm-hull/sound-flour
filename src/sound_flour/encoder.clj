(ns sound-flour.encoder
  (:require
    [sound-flour.byte-converters :refer :all])
  (:import
    [javax.sound.sampled AudioFormat AudioInputStream]
    [java.io ByteArrayInputStream SequenceInputStream]))

;; Based on https://github.com/gre/zound/blob/master/playapp/app/encoders/MonoWaveEncoder.scala

(def content-type "audio/wav")

(defn- header [frame-rate bits-per-sample]
  (let [samples-per-frame 1
        bytes-per-sample (int (/ (+ bits-per-sample 7) 8))
        riff (concat
               (map byte "RIFF")
               (int-little-endian 0x7fffffff)
               (map byte "WAVE"))
        fmt  (concat
               (map byte "fmt ")
               (int-little-endian 16)  ; Subchunk1Size for PCM = 16
               (short-little-endian 1) ; AudioFormat for PCM = 1 (mono)
               (short-little-endian samples-per-frame)
               (int-little-endian frame-rate)
               (int-little-endian (* frame-rate samples-per-frame bytes-per-sample))
               (short-little-endian (* samples-per-frame bytes-per-sample))
               (short-little-endian bits-per-sample))
        data (concat
               (map byte "data")
               (int-little-endian 0x7fffffff))]
  (byte-array (concat riff fmt data))))

(defn clip [^double d]
  (Math/max -1.0 (Math/min d 1.0)))

(defn scale [^double d]
  (ushort (* 0x7fff d)))

(defn encode-data [data]
  (mapcat #(short-little-endian (scale (clip %))) data))

(defn audio-format [samples-per-frame bits-per-sample]
  (AudioFormat.
    samples-per-frame
    bits-per-sample
    1 ; channels
    false ; signed
    false)) ; little-endian

(defn audio-stream [frame-rate bits-per-sample raw-data-stream]
  ;(AudioInputStream.
    (SequenceInputStream.
      (ByteArrayInputStream. (header frame-rate bits-per-sample))
      raw-data-stream)
  ;  (audio-format)
  ;  Long/MAX_VALUE)

  )

