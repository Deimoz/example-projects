(defn checkSize [args]
  (every? #(= (count (first args)) (count %)) args))

(defn checkNumbers [s]
  (every? number? s))

(defn checkVectors [v]
  (and (checkSize v) (every? #(and (vector? %) (every? number? %)) v)))

(defn matrix? [m]
  (and (vector? m) (checkVectors m)))

(defn checkMatrix [m]
  (and (every? matrix? m)))

(defn tensor? [t]
  (if (and (every? vector? t) (checkSize t))
    (every? tensor? (apply mapv vector t))
    (if (every? number? t)
      true
      false)))

(defn abstractVectorFunction [func]
  (fn [& v]
    {:pre [(checkVectors v)]
     :post [(vector? %)]}
    (apply mapv (partial func) v)))

(def v+ (abstractVectorFunction +))
(def v- (abstractVectorFunction -))
(def v* (abstractVectorFunction *))

(comment "common 3, hard is not accepted")
(defn v*s [v & s]
  {:pre [(vector? v), (checkNumbers s)]
   :post [(vector? %)]}
  (def x
    (if (= (count s) 0)
      1
      (reduce * s)))
  (mapv #(* % x) v))

(defn scalar [& v]
  {:pre [(checkVectors v)]
   :post [(number? %)]}
  (reduce + (reduce v* v)))

(defn det2 [v1 v2]
  {:pre [(vector? v1), (vector? v2)]}
  (fn [index1 index2]
    {:pre [(number? index1), (number? index2)]
     :post [(number? %)]}
    (- (* (nth v1 index1) (nth v2 index2)) (* (nth v1 index2) (nth v2 index1)))))

(defn vect [& v]
  {:pre [(checkVectors v), (every? #(= (count %) 3) v)]
   :post [(vector? %), (= (count %) 3)]}
  (reduce
    (fn [v1 v2]
      (def d (det2 v1 v2))
      (vector
        (d 1 2)
        (d 2 0)
        (d 0 1)))
    v))

(defn abstractMatrixFunction [func]
  (fn [& m]
    {:pre [(checkMatrix m)]
     :post [(matrix? %)]}
    (apply mapv (partial func) m)))

(def m+ (abstractMatrixFunction v+))
(def m- (abstractMatrixFunction v-))
(def m* (abstractMatrixFunction v*))

(defn transpose [m]
  (apply mapv vector m))

(defn m*s [m & s]
  {:pre [(matrix? m), (checkNumbers s)]
   :post [(matrix? %)]}
  (def x
    (if (= (count s) 0)
      1
      (reduce * s)))
  (into [] (for [v m] (v*s v x))))

(defn m*v [m & v]
  {:pre [(matrix? m), (checkVectors v)]
   :post [(vector? %)]}
  (mapv (fn [x] (apply scalar x v)) m))

(comment ":NOTE: too many transposes")
(defn m*m [& m]
  {:pre [(checkMatrix m)]
   :post [(matrix? %)]}
  (reduce
    (fn [m1 m2]
      (mapv #(m*v (transpose m2) %) m1))
    m))

(defn abstractTensorFunction [func & args]
  {:pre [(checkSize args)]}
  (if (vector? (first (first args)))
    (apply mapv (partial abstractTensorFunction func) args)
    (apply func args)))

(defn createTensorFunction [func]
  (fn [& args]
    {:pre [(every? tensor? args)]
     :post [(tensor? %)]}
    (apply abstractTensorFunction func args)))

(def t+ (createTensorFunction v+))
(def t- (createTensorFunction v-))
(def t* (createTensorFunction v*))