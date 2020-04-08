;; Parsing of the org file
(ns chord-parse)

(use '[clj-org.org :refer [parse-org]])
(use '[clojure.string :only [trim split]])

(defn extract-songs [song-list]
  (def ul-contents (drop 1 (get song-list 2)))
  (map (fn [li] 
         (trim (get (get li 1) 1))) ul-contents))

(defn create-prog [progression]
  {:prog progression :songs nil})

(defn extract-prog [prog-section]
  (split 
   (get (get prog-section 1) 1) #" "))

(defn is-prog-section? [section-maybe]
  (and (vector? section-maybe) (= (get section-maybe 0) :h3)))

(defn is-beginning-song-list? [song-list-maybe]
  (and (vector? song-list-maybe)
       (>= (count song-list-maybe) 3)
       (vector? (get song-list-maybe 2))
       (>= (count (get song-list-maybe 2)) 1)
       (= (get (get song-list-maybe 2) 0) :ul)))

(defn parse-chord-org [org-contents]

  (with-local-vars [parsed-orgs (parse-org org-contents),
                    chord-progs []]

    ;; Why won't this print!?
    ;; (for [section [1 2 3 5]]
    ;;   (println section))

    (doseq [section (@parsed-orgs :content)]

      (when (is-prog-section? section)
       (var-set chord-progs
                (conj @chord-progs (create-prog (extract-prog section)))))


      (when (is-beginning-song-list? section)
        (var-set chord-progs
                 (conj (vec (drop-last @chord-progs)) ;; God... drop-last returns a list and conj acts differently on lists and vectors
                       (assoc (last @chord-progs) :songs (extract-songs section))))))

    @chord-progs))
