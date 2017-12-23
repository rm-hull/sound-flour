(defproject rm-hull/sound-flour "0.1.0"
  :description "Renders images and audio streams into a format palatable for ring."
  :url "https://github.com/rm-hull/sound-flour"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [compojure "1.6.0"]
    [ring "1.6.3"]
    [metrics-clojure-ring "2.10.0"]
    [ring-logger-timbre "0.7.6"]
    [com.taoensso/timbre "4.10.0"]
    [rm-hull/infix "0.3.1"]]
  :scm {:url "git@github.com:rm-hull/sound-flour.git"}
  :ring {
    :handler sound-flour.handler/app }
  :aot [sound-flour.FunctionInputStream]
  :source-paths ["src"]
  :resource-paths ["resources"]
  :jar-exclusions [#"(?:^|/).git"]
  :uberjar-exclusions [#"\.SF" #"\.RSA" #"\.DSA"]
  :codox {
    :source-paths ["src"]
    :output-path "doc/api"
    :source-uri "http://github.com/rm-hull/sound-flour/blob/master/{filepath}#L{line}" }
  :min-lein-version "2.8.1"
  :profiles {
    :uberjar {:aot :all}
    :dev {
      :global-vars {*warn-on-reflection* true}
    :dependencies [
      [org.clojure/clojure "1.9.0"]]
    :plugins [
      [lein-ring "0.12.2"]
      [lein-codox "0.10.3"]
      [lein-cloverage "1.0.10"]]}})
