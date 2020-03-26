(ns utils)

;; Assumes the key (major/minor) of entire song to infer interval
(def numeral-to-interval-quality
  {
   ;; Minor
   "i" [0 :min]
   ;; 2nd chord is diminished; not normally used in pop
   "III" [3 :maj]
   "iv" [5 :min]
   "v" [7 :min]
   "VI" [8 :maj]
   "VII" [10 :maj]

   ;; Major
   "I" [0 :maj]
   "ii" [2 :min]
   "iii" [4 :min]
   "IV" [5 :maj]
   "V" [7 :maj]
   "vi" [9 :min]
   "bVII" [10 :maj]
   ;; 7th chord is diminished; not normally used in pop
   })

(def chord-intervals
  {:min [:Cm :Dbm :Dm :Ebm :Em :Fm :Gbm :Gm :Abm :Am :Bbm :Bm]
   :maj [:C :Db :D :Eb :E :F :Gb :G :Ab :A :Bb :B]})

(def root-to-keys ["C" "Db" "D" "Eb" "E" "F" "Gb" "G" "Ab" "A" "Bb" "B"])

(defn get-chord [chord-str root]

  (let [[interval quality] (numeral-to-interval-quality chord-str)]
    (nth (chord-intervals quality) (mod (+ interval root) 12))))
