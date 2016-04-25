(defproject rm-hull/sound-flour "0.1.0"
  :description "Renders images and audio streams into a format palatable for ring."
  :url "https://github.com/rm-hull/sound-flour"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [compojure "1.5.0"]
    [ring "1.4.0"]
    [metrics-clojure-ring "2.6.1"]
    [ring-logger-timbre "0.7.5"]
    [com.taoensso/timbre "4.3.1"]
    [rm-hull/infix "0.2.6"]]
  :scm {:url "git@github.com:rm-hull/sound-flour.git"}
  :ring {
    :handler sound-flour.handler/app }
  :plugins [
    [lein-ring "0.9.7"]
    [codox "0.9.4"]]
  :aot [sound-flour.FunctionInputStream]
  :source-paths ["src"]
  :resource-paths ["resources"]
  :jar-exclusions [#"(?:^|/).git"]
  :uberjar-exclusions [#"\.SF" #"\.RSA" #"\.DSA"]
  :codox {
    :sources ["src"]
    :output-dir "doc/api"
    :src-dir-uri "http://github.com/rm-hull/sound-flour/blob/master/"
    :src-linenum-anchor-prefix "L" }
  :min-lein-version "2.6.1"
  :profiles {
    :uberjar {:aot :all}
    :dev {
      :global-vars {*warn-on-reflection* true}
      :plugins [
        [lein-cloverage "1.0.6"]]}})
