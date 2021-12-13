(definterface ExprInterface
  (evaluate [elements])
  (toString [])
  (toStringSuffix [])
  (diff [diffName]))

(deftype AbstractOperation [func diffRule args opSymb]
  ExprInterface
  (evaluate [this x] (apply func (mapv #(.evaluate % x) (.args this))))
  (toString [this] (str "(" opSymb " " (clojure.string/join " " (map #(.toString %) (.args this))) ")"))
  (toStringSuffix [this] (str "("(clojure.string/join " " (map #(.toStringSuffix %) (.args this))) " " opSymb ")"))
  (diff [this diffName] (diffRule diffName)))

(deftype cnstVarCons [funcEval funcToStr funcDiff arg]
  ExprInterface
  (evaluate [this x] (funcEval x))
  (toString [this] (funcToStr))
  (toStringSuffix [this] (funcToStr))
  (diff [this diffName] (funcDiff diffName)))

(defn Constant [value] (cnstVarCons.
                         (constantly value)
                         (fn [] (str (format "%.1f" (double value))))
                         (fn [diffName] (Constant 0))
                         value))
(comment ":NOTE: 2 lines up this constant is duplicated")
(comment ":NOTE: still be")
(def ZERO (Constant 0))
(def ONE (Constant 1))
(def TWO (Constant 2))

(defn Variable [name] (cnstVarCons.
                        (fn [x] (x name))
                        (fn [] (str name))
                        (fn [diffName] (if (= diffName name) ONE ZERO))
                        name))

(defn evaluate [expression values] (.evaluate expression values))
(defn toString [expression] (.toString expression))
(defn toStringSuffix [expression] (.toStringSuffix expression))
(defn diff [expression diffName] (.diff expression diffName))

(defn abstractDiff [diffName] #(diff % diffName))

(defn createOperation [func diffFunc opSymbol]
  (fn [& args]
    (AbstractOperation.
      func
      (fn [diffName] (diffFunc args (abstractDiff diffName)))
      args
      opSymbol)))

(def Add (createOperation
           +
           #(Add (%2 (first %1)) (%2 (second %1)))
           "+"))

(def Subtract (createOperation
                -
                #(Subtract (%2 (first %1)) (%2 (second %1)))
                "-"))

(def Multiply (createOperation
                *
                #(Add (Multiply (first %1) (%2 (second %1))) (Multiply (second %1) (%2 (first %1))))
                "*"))

(def Divide (createOperation
              #(/ (double %1) (double %2))
              #(Divide (Subtract (Multiply (second %1) (%2 (first %1))) (Multiply (first %1) (%2 (second %1)))) (Multiply (second %1) (second %1)))
              "/"))

(def Negate (createOperation
              -
              #(Negate (%2 (first %1)))
              "negate"))

(def Square (createOperation
              #(* % %)
              #(Multiply (Multiply TWO (first %1)) (%2 (first %1)))
              "square"))

(def Sqrt (createOperation
            #(Math/sqrt (Math/abs %))
            #(Multiply (Divide (first %1) (Sqrt (Square (first %1)))) (Multiply (Divide ONE (Multiply TWO (Sqrt (first %1)))) (%2 (first %1))))
            "sqrt"))

(def Pow (createOperation
           #(Math/pow %1 %2)
           #(ONE)
           "**" ))

(def Log (createOperation
           #(/ (Math/log (Math/abs %2)) (Math/log (Math/abs %1)))
           #(ONE)
           "//"))

(def variables {
                'x (Variable "x")
                'y (Variable "y")
                'z (Variable "z")
                })

(def operations {
                 "+" Add
                 "-" Subtract
                 "*" Multiply
                 "/" Divide
                 "negate" Negate
                 "sqrt" Sqrt
                 "square" Square
                 "**" Pow
                 "//" Log
                 })

(defn parse [expression]
  (cond
    (list? expression)
    (apply (operations (str (first expression)))
           (mapv parse (next expression)))
    (contains? variables expression) (variables expression)
    :else (Constant expression)))

(defn parseObject [expression]
  (parse (read-string expression)))

(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)

(defn _empty [value] (partial -return value))

(defn _char [p]
  (fn [[c & cs]]
    (if (and c (p c)) (-return c cs))))

(defn _map [f result]
  (if (-valid? result)
    (-return (f (-value result)) (-tail result))))

(defn _combine [f a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar)
        (_map (partial f (-value ar))
              ((force b) (-tail ar)))))))

(defn _either [a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar) ar ((force b) str)))))

(defn _parser [p]
  (fn [input]
    (-value ((_combine (fn [v _] v) p (_char #{\u0000})) (str input \u0000)))))

(defn +char [chars] (_char (set chars)))

(defn +char-not [chars] (_char (comp not (set chars))))

(defn +map [f parser] (comp (partial _map f) parser))

(def +parser _parser)

(def +ignore (partial +map (constantly 'ignore)))

(defn iconj [coll value]
  (if (= value 'ignore) coll (conj coll value)))

(defn +seq [& ps]
  (reduce (partial _combine iconj) (_empty []) ps))

(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps)))

(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps))

(defn +or [p & ps]
  (reduce _either p ps))

(defn +opt [p]
  (+or p (_empty nil)))

(defn +star [p]
  (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec)))

(defn +plus [p] (+seqf cons p (+star p)))

(defn +str [p] (+map (partial apply str) p))

(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))

(def *digit (+char "0123456789"))
(def *number (+map read-string (+str (+plus *digit))))

(def *constant
  (+map (fn [[minus num1 dot num2]]  (Constant (read-string (str (str minus) (apply str num1) (str dot) (apply str num2)))))
        (+seq (+opt (+char "-")) (+plus *digit) (+opt (+char ".")) (+opt (+plus *digit)))))

(def *all-chars (mapv char (range 32 128)))
(apply str *all-chars)

(defn *name [word] (+str (apply +seq (mapv #(+char (str %)) word))))
(defn *words [& words] (apply +or (mapv #(*name %) words)))
(def *opers (*words "**" "//" "-" "+" "*" "/" "negate"))

(def *variable
  (+map #(Variable %)
        (*words "x" "y" "z")))

(defn *expression [p]
  (+map (fn [[& args]] (apply (operations (last args)) (butlast args)))
        (+seqn 1 (+char "(") (+opt (+seqf cons *ws p (+star (+seqn 1 *space *ws p)))) *ws (+char ")"))))

(def parseObjectSuffix
  (letfn [(*value []
            (delay (+or
                     *constant
                     *variable
                     *opers
                     (*expression (*value)))))]
    (+parser (+seqn 0 *ws (*value) *ws))))

(defn _parser [p]
  (fn [input]
    (-value ((_combine (fn [v _] v) p (_char #{\u0000})) (str input \u0000)))))

(defn +char [chars] (_char (set chars)))

(defn +char-not [chars] (_char (comp not (set chars))))

(defn +map [f parser] (comp (partial _map f) parser))

(def +parser _parser)

(def +ignore (partial +map (constantly 'ignore)))

(defn iconj [coll value]
  (if (= value 'ignore) coll (conj coll value)))

(defn +seq [& ps]
  (reduce (partial _combine iconj) (_empty []) ps))

(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps)))

(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps))

(defn +or [p & ps]
  (reduce _either p ps))

(defn +opt [p]
  (+or p (_empty nil)))

(defn +star [p]
  (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec)))

(defn +plus [p] (+seqf cons p (+star p)))

(defn +str [p] (+map (partial apply str) p))

(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))

(def *digit (+char "0123456789"))
(def *number (+map read-string (+str (+plus *digit))))

(def *constant
  (+map (fn [[minus num1 dot num2]]  (Constant (read-string (str (str minus) (apply str num1) (str dot) (apply str num2)))))
        (+seq (+opt (+char "-")) (+plus *digit) (+opt (+char ".")) (+opt (+plus *digit)))))

(def *all-chars (mapv char (range 32 128)))
(apply str *all-chars)

(defn *name [word] (+str (apply +seq (mapv #(+char (str %)) word))))
(defn *words [& words] (apply +or (mapv #(*name %) words)))
(def *opers (*words "**" "//" "-" "+" "*" "/" "negate"))

(def *variable
  (+map #(Variable %)
        (*words "x" "y" "z")))

(defn *expression [p]
  (+map (fn [[& args]] (apply (operations (last args)) (butlast args)))
        (+seqn 1 (+char "(") (+opt (+seqf cons *ws p (+star (+seqn 1 *space *ws p)))) *ws (+char ")"))))

(def parseObjectSuffix
  (letfn [(*value []
            (delay (+or
                     *constant
                     *variable
                     *opers
                     (*expression (*value)))))]
    (+parser (+seqn 0 *ws (*value) *ws))))