(ns utils-test)

(use '[utils :only [get-chord]])

(use 'clojure.test)

(deftest chord-conversion
  (testing "Should convert correctly"
    (is (= (get-chord "IV" 2) :G)) ; fourth of D
    (is (= (get-chord "vi" 3) :Cm)) ; sixth of Db
    (is (= (get-chord "bVII" 1) :B)) ; flattened seventh of Db
    (is (= (get-chord "VII" 3) :Db)))) ; seventh of Bb
