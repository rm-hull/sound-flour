(defproject sound-flour "0.0.1"
  :description "an experiment in collaborative broadcast streaming computer-generated music"
  :url "https://github.com/rm-hull/sound-flour"
  :license {:name "Creative Commons 3.0"
            :url "http://creativecommons.org/licenses/by/3.0/legalcode"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.2"]
                 [instaparse "1.1.0"]
                 [clj-http "0.7.2"]
                 [clj-time "0.5.1"]
                 [compojure "1.1.5"]
                 [ring/ring-core "1.2.0-RC1"]
                 [hiccup "1.0.3"]
                 [prismatic/dommy "0.1.1"]
                 ]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler sound-flour.handler/app}
  :source-path "src"
  :min-lein-version "2.2.0"
  :description "TODO" 
  :global-vars { *warn-on-reflection* true }) 
