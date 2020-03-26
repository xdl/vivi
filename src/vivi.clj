;; http://www.flyingmachinestudios.com/programming/how-clojure-babies-are-made-lein-run/
(ns vivi
  (:gen-class))

(use '[clojure.string :only (join split)])

(use '[chord-parse :only [parse-chord-org]])
(use '[prog-player :only [ProgPlayer]])
(use '[utils :only [root-to-keys]])

(defn print-toplevel-help []

  (println "
h: Help
r: Reveal chord progression and key

c: Shuffle chord progression
cs: Select chord progression

k: Shuffle key
ks: Select key

p: Play/Pause
pd: Toggle drums

f: Faster
s: Slower

q: Quit
"))

(defn print-key-help []

  (let [key-list-formatted (map-indexed (fn [idx key]
                                          (format "%d: %s" idx key)) root-to-keys)]
    (println (join "\n" key-list-formatted) "\n\nb: back")))

(defn print-chord-help [chord-progs]

  (let [chord-prog-list (map (comp (partial join " ") :prog) chord-progs)
        chord-prog-list-formatted (map-indexed (fn [idx chord-prog]
                                                 (format "%d: %s" idx chord-prog)) chord-prog-list)]
    (println (join "\n" chord-prog-list-formatted) "\n\nb: back")))

(defn print-status [status]

  (let [status-lines [(format "Playing: %s" (status :playing))
                      (format "Drums: %s" (status :drums))
                      (format "BPM: %s" (status :bpm))
                      (format "Root: %s" (status :cur-root))
                      (format "Chord Progression: %s" (join " " ((status :cur-chord-prog) :prog)))
                      (format "Songs\n%s" (join "\n" ((status :cur-chord-prog) :songs)))]]
    (println (join "\n" status-lines))))

(defn -main [org-path]

  (let [chord-progs (parse-chord-org (slurp org-path))
        prog-player (ProgPlayer chord-progs)

        cur-command-process-fn (atom :toplevel)

        process-toplevel-command (fn [char]
                                   (cond 
                                     (= char "c") ((prog-player :shuffle-chord-prog))
                                     (= char "cs") (reset! cur-command-process-fn :chord)
                                     (= char "f") ((prog-player :faster))
                                     (= char "h") (print-toplevel-help)
                                     (= char "k") ((prog-player :shuffle-key))
                                     (= char "ks") (reset! cur-command-process-fn :key)
                                     (= char "p") ((prog-player :play-pause-toggle))
                                     (= char "pd") ((prog-player :drums-toggle))
                                     (= char "q") (prog-player :quit)
                                     (= char "r") (print-status ((prog-player :status)))
                                     (= char "s") ((prog-player :slower))))

        process-key-command (fn [char]
                              (cond
                                (number? (read-string char)) ((prog-player :select-key) (read-string char))
                                (= char "b") (reset! cur-command-process-fn :toplevel)))

        process-chord-command (fn [char]
                                (cond
                                  (number? (read-string char)) ((prog-player :select-chord-prog) (read-string char))
                                  (= char "b") (reset! cur-command-process-fn :toplevel)))

        command-processors {
                            :toplevel process-toplevel-command
                            :key process-key-command
                            :chord process-chord-command
                            }

        command-processor-help {
                                :toplevel print-toplevel-help
                                :key print-key-help
                                :chord (partial print-chord-help chord-progs)
                            }
        ]

    (print-toplevel-help)

    (loop [command-char (read-line)]

      ((command-processors @cur-command-process-fn) command-char)

      ((command-processor-help @cur-command-process-fn))

      (when (not (= "q" command-char))
        (recur (read-line))))

    (println "Finishing program")

    (System/exit 0)))
