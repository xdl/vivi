(ns chord-parse-test)

(use '[chord-parse :only [parse-chord-org]])

(use 'clojure.test)

(deftest org-parsing
  (testing "Should parse chord progressions properly"
    (let [test-org-content "
* Arbitrary heading

*** I VI vi V
*** ii VI vi I

- Song in bullet point
"
          expected-chord-progs [{:prog '["I" "VI" "vi" "V"] :songs nil}
                                {:prog '["ii" "VI" "vi" "I"] :songs '("Song in bullet point")}]]
    (is (= (parse-chord-org test-org-content) expected-chord-progs)))))
