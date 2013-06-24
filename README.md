# Sound-Flour [![Build Status](https://secure.travis-ci.org/rm-hull/sound-flour.png)](http://travis-ci.org/rm-hull/sound-flour)

Sound-flour is:

* my 2013 [Lisp in Summer Projects](http://lispinsummerprojects.org/) submission,
* written in clojure & clojurescript,
* uses HTML5's web audio API,
* an experiment in collaborative broadcast streaming computer-generated music,
* hosted in heroku: http://sound-flour.destructuring-bind.org, 
* nothing more than an idea in my head (at the moment),
* is randomly documented in this readme,
* subject to change,
* a tragic pun on http://pouet.net/nfo.php?which=57115 ...
* ... but will probably end up more like https://www.youtube.com/watch?v=3zoTKXXNQIU

# Background

Watching https://www.youtube.com/watch?v=GtQdIYUtAHg and subsequently trying it 
blew me away somehow.

The fact that a simple C program fragment like:

```c
(t*(t>>5|t>>8))>>(t>>16)
```

or

```c
(t%(t/(t>>9|t>>13)))
```

can produce an audio wave that, although arguably isn't music in the 
conventional sense, produces something that is rhythmic, fanciful and 
interestingly rich, but somehow intensely annoying. Its not white 
noise by any stretch, as you can discern repeating structure amongst
the chaos: nevertheless, it is addictive to hang on (and on) waiting 
for a fleeting moment of melody to only to melt away almost before it
it happened.

## The Proposition

So it's easy to write a program to generate random sound, right? This pretty 
much nails it here: http://wurstcaptures.untergrund.net/music/, so maybe we
should pack up now and go home.

Well... while that's pretty good, its a bit low-level, and you can only have
_so_ much fun munging bits all day. We want something more... social... gamific...
We have a [clojure sandbox](https://github.com/Licenser/clj-sandbox), 
so lets allow gists (or listen in on tweets) to provide some clojure SEXPs to 
generate some interesting stream data.

In theory, we could generate something altogether a bit smoother maybe:

```clojure
(def buffer-len 8000)

(def sample-rate 8000)

(defn sine-wave [frequency amplitude]
  (let [data (->> 
                (range buffer-len)
                (map #(* amplitude (Math/sin (/ (* 2 Math/PI % frequency) sample-rate))))
                (into []))]
    (fn [t] (data (mod t buffer-len)))))

(sine-wave 440 0.5) ; 440 Hz = Middle C 
```
Which returns a function that driven with a sampling tick, returns a value in
the range ±1.0 that when streamed out, would approximates middle C.

A sine wave on its own is pretty boring; it only _becomes_ interesting when it 
can be composed with other functions, 
c.f. [Functional Composition by Chris Ford](http://www.youtube.com/watch?v=Mfsnlbd-4xQ),
so perhaps we can build on top of this basic function, perhaps there should be multiple 
channels that get mixed before being streamed, maybe there should be stereo output, ...

## What this project actually is

A web framework, in clojure, for processing gists (also in clojure) in which 
a nominated function translates an ever increating time-quantum in some musically 
interesting way, mixes it with other gists' functions, collects the resultant data
into a stream, encodes to a WAV before chunking and broadcasting to anyone who may be
listening. And, as time permits, to include some real-time visualization of broadcast
audio data. 

## What will make it work?

I see it working at three levels: 

* Framework + nothing = something not interesting

* Framework + my gists = something possibly interesting

* Framework + YOUR gists = something really interesting

## So why shouldn't I just use Overtone?

You probably should, and I may end up doing so too...

## Examples

TODO

## Inspiration

In no particular order, ideas borrowed heavily from:

* https://www.youtube.com/watch?v=Pgw_nVqSTLw
* https://www.youtube.com/watch?v=GtQdIYUtAHg
* https://www.youtube.com/watch?v=tCRPUv8V22o
* http://www.youtube.com/watch?v=Mfsnlbd-4xQ
* http://wurstcaptures.untergrund.net/music/
* http://greweb.me/2012/08/zound-a-playframework-2-audio-streaming-experiment-using-iteratees/
* http://devslovebacon.com/conferences/bacon-2013/talks/defining-music-recreational-programming-and-pure-data
* http://js1k.com/2013-spring/demo/1558

## TODO

TODO

## References

* http://www.sonicspot.com/guide/wavefiles.html
* https://ccrma.stanford.edu/courses/422/projects/WaveFormat/
* http://chimera.labs.oreilly.com/books/1234000001552

## License

MIT / [Creative Commons 3.0](http://creativecommons.org/licenses/by/3.0/legalcode)
