(ns prog-player)

(use '[overtone.live :only
       [ctl
        apply-by
        at
        metronome
        stop]])

(use '[overtone.synth.stringed :only
       [guitar
        guitar-strum]])

(use '[overtone.inst.drum :only
       [kick2
        closed-hat]])

(use '[utils :only [root-to-keys
                    get-chord]])

(def g (guitar))

(defn pattern-to-beat-strum-seq
  "given a string describing a one-measure up/down strum pattern like
'ud-udu-', return a sequence of vector [beats :up/:down] pairs"
  [cur-pattern]
  (let [strums-per-measure (count cur-pattern)
        beats-per-measure 4.0
        beats-per-strum (/ beats-per-measure strums-per-measure)
        ud-keywords {\u :up, \d :down}]
    (for [[i s] (map-indexed vector cur-pattern)]
      (when (contains? ud-keywords s)
        [(* i beats-per-strum) (ud-keywords s)]))))

(defn strum-pattern [the-guitar metro cur-measure cur-chord cur-pattern]
  (let [cur-beat (* 4 cur-measure)]
    (doall
     (doseq [[b d] (pattern-to-beat-strum-seq cur-pattern)]
       (when-not (= b nil)
         (guitar-strum the-guitar cur-chord d 0.07 (metro (+ b cur-beat))))))))

(defn play-prog [metro measure-offset chord-list]
     (doseq [[i cur-chord] (map-indexed vector chord-list)]
       (let [cur-pattern "d-du-udu"]
         (strum-pattern g metro (+ measure-offset i) cur-chord cur-pattern))))

(def BPM-INCREMENT 4)

;; Using the JS module pattern
(defn ProgPlayer [chord-progs]

  (let [playing (atom true) ;; Needs to be an atom to use reset!
        playing-drums (atom true)
        prog-index (atom (rand-int (count chord-progs)))
        root-index (atom (rand-int 12)) ;; 12 intervals
        metro (metronome 104)
        play-loop (fn loop [beat]
                    (when @playing

                      ;; 16 = 4 chords * 4 beats (or one measure) per chord
                      ;; Offsetting beat by one to give the loop a chance initialise quicker
                      (when (= 1 (mod (metro) 16))

                        (when @playing-drums 
                          (at (metro beat) (kick2 :freq 40 :noise 0.1 :sustain 0.8))
                          (at (metro (+ 1 beat)) (closed-hat))
                          (at (metro (+ 2 beat)) (closed-hat))
                          (at (metro (+ 3 beat)) (closed-hat))

                          (at (metro (+ 4 beat)) (kick2 :freq 40 :noise 0.1 :sustain 0.8))
                          (at (metro (+ 5 beat)) (closed-hat))
                          (at (metro (+ 6 beat)) (closed-hat))
                          (at (metro (+ 7 beat)) (closed-hat))

                          (at (metro (+ 8 beat)) (kick2 :freq 40 :noise 0.1 :sustain 0.8))
                          (at (metro (+ 9 beat)) (closed-hat))
                          (at (metro (+ 10 beat)) (closed-hat))
                          (at (metro (+ 11 beat)) (closed-hat))

                          (at (metro (+ 12 beat)) (kick2 :freq 40 :noise 0.1 :sustain 0.8))
                          (at (metro (+ 13 beat)) (closed-hat))
                          (at (metro (+ 14 beat)) (closed-hat))
                          (at (metro (+ 15 beat)) (closed-hat))
                          (at (metro (+ 15.5 beat)) (closed-hat)))

                        (let [chord-intervals (map (fn [chord-str]
                                                     (get-chord chord-str @root-index))
                                                   ((nth chord-progs @prog-index) :prog))]
                          (play-prog metro (/ (metro) 4) chord-intervals)))
                      ;; (at (metro beat) (kick2 :freq 40 :noise 0.1 :sustain: 0.8))
                      ;; (at (metro (+ 0.5 beat)) (closed-hat))
                      )
                    (apply-by (metro (inc beat)) loop (inc beat) []))]

    (play-loop (metro))

    {
     :play-pause-toggle (fn []
                          (reset! playing (not @playing)))

     :drums-toggle (fn []
                          (reset! playing-drums (not @playing)))

     :quit stop

     :faster (fn []
               (metro :bpm (+ (metro :bpm) BPM-INCREMENT)))

     :slower (fn []
               (metro :bpm (- (metro :bpm) BPM-INCREMENT)))

     :select-key (fn [idx]
                   (reset! root-index idx))

     :select-chord-prog (fn [idx]
                          (reset! prog-index idx))

     :status (fn []
               {:playing @playing
                :drums @playing-drums
                :bpm (metro :bpm)
                :cur-chord-prog (nth chord-progs @prog-index)
                :cur-root (nth root-to-keys @root-index)})

     :shuffle-chord-prog (fn []
                           (reset! prog-index (rand-int (count chord-progs))))

     :shuffle-key (fn []
                    (reset! root-index (rand-int 12)))}))
