(ns sound-flour.encoder
  (:require
    [sound-flour.byte-converters :refer :all])
  (:import
    [javax.sound.sampled AudioFormat AudioInputStream]
    [java.io BufferedInputStream ByteArrayInputStream SequenceInputStream]))

;; Based loosely on:
;;   - https://github.com/gre/zound/blob/master/playapp/app/encoders/MonoWaveEncoder.scala
;;   - http://noisetube.googlecode.com/svn/mobile/java/shared/trunk/src/net/noisetube/audio/format/WAVEAudioStream.java
;;   - http://computermusicblog.com/blog/2008/08/29/reading-and-writing-wav-files-in-java/
;;   - http://www.computermusicblog.com/sourcecode/javaWavIO/wavIO.java
;;
;; RIFF/WAVE specification described here:
;;   - http://ccrma.stanford.edu/courses/422/projects/WaveFormat
;;   - http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
;;   - http://www.sonicspot.com/guide/wavefiles.html
;;   - http://wiki.multimedia.cx/index.php?title=PCM
;;   - http://en.wikipedia.org/wiki/WAV
;;   - http://billposer.org/Linguistics/Computation/LectureNotes/AudioData.html
;;
;; SPECIFICATION (RIFF/WAVE file containing PCM data):
;;   The canonical WAVE format starts with the RIFF header:
;;     0    4 (char[4])     ChunkID             Contains the letters "RIFF" in ASCII form (0x52494646 big-endian form).
;;     4    4 (uint)        ChunkSize           = 4 + (8 + SubChunk1Size) + (8 + SubChunk2Size); = 36 + SubChunk2Size if SubChunk1Size=16
;;                                                   This is the size of the rest of the chunk following this number.  This is the size of the
;;                                                   entire file in bytes minus 8 bytes for the two fields not included in this count:
;;                                                   ChunkID and ChunkSize.
;;     8    4 (char[4])     Format              Contains the letters "WAVE" (0x57415645 big-endian form).
;;
;;   The "WAVE" format consists of two subchunks: "fmt " and "data":
;;    The "fmt " subchunk describes the sound data's format:
;;     12   4 (char[4])     Subchunk1ID         Contains the letters "fmt " (0x666d7420 big-endian form).
;;     16   4 (uint)        Subchunk1Size       16 for PCM. This is the size of the rest of the Subchunk which follows this number.
;;     20   2 (ushort)      AudioFormat         PCM = 1 (i.e. Linear quantization). Values other than 1 indicate some form of compression.
;;     22   2 (ushort)      NumChannels         Mono = 1, Stereo = 2, etc.
;;     24   4 (uint)        SampleRate          8000, 44100, etc.
;;     28   4 (uint)        ByteRate            = SampleRate * NumChannels * BitsPerSample/8 = SampleRate * BlockAlign
;;     32   2 (ushort)      BlockAlign          = NumChannels * BitsPerSample/8 (= the number of bytes for one sample including all channels)
;;     34   2 (ushort)      BitsPerSample       8 bits = 8, 16 bits = 16, etc.
;;    [36   2 (ushort)      ExtSize             Size of format chunk extension (0 or 22)]       --> only present is Subchunk1Size = 18 or 40
;;    [38   2 (ushort)      ValidBitsPerSample  Number of valid bits]                           --> only present is Subchunk1Size = 40
;;    [40   4 (uint)        ChannelMask         Speaker position mask (for multichannel files)] --> only present is Subchunk1Size = 40
;;    [44   16 (GUID)       SubFormat           GUID, including the data format code]           --> only present is Subchunk1Size = 40
;;
;;    The "data" subchunk contains the size of the data and the actual sound:
;;     20+Subchunk1Size   (default 36)  4 (char[4])     Subchunk2ID         Contains the letters "data" (0x64617461 big-endian form).
;;     20+Subchunk1Size+4 (default 40)  4 (uint)        Subchunk2Size       = NumSamples * NumChannels * BitsPerSample/8
;;                                                           This is the number of bytes in the data. You can also think of this
;;                                                           as the size of the read of the subchunk following this number.
;;     20+Subchunk1Size+8 (default 44)  *               Data                        The actual sound data, subChunk2Size bytes long.
(defn- header [frame-rate bits-per-sample]
  (let [num-channels 1 ;; mono
        bytes-per-sample (int (/ (+ bits-per-sample 7) 8))
        riff (concat
               (map byte "RIFF")
               (int-little-endian 0x7fffffff)
               (map byte "WAVE"))
        fmt  (concat
               (map byte "fmt ")
               (int-little-endian 16)  ; Subchunk1Size for PCM = 16
               (short-little-endian 1) ; AudioFormat for PCM = 1 (linear quantization)
               (short-little-endian num-channels)
               (int-little-endian frame-rate)
               (int-little-endian (* frame-rate num-channels bytes-per-sample))
               (short-little-endian (* num-channels bytes-per-sample))
               (short-little-endian bits-per-sample))
        data (concat
               (map byte "data")
               (int-little-endian 0x7fffffff))]
  (byte-array (concat riff fmt data))))

(defn audio-format [frame-rate bits-per-sample]
  (AudioFormat.
    frame-rate
    bits-per-sample
    1        ; num-channels = 1 (mono)
    false    ; unsigned
    false))  ; little-endian

(defn audio-stream [raw-data-stream frame-rate bits-per-sample]
  (AudioInputStream.
    (SequenceInputStream.
      (ByteArrayInputStream. (header frame-rate bits-per-sample))
      (BufferedInputStream. raw-data-stream))
    (audio-format frame-rate bits-per-sample)
    Long/MAX_VALUE))

