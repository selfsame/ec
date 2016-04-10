(ns game.macros)

(defmacro ? [& code] `(cond ~@code))

(defn- kw->str [s]
  (apply str (rest (str s))))

(defmacro el [-tag -m & -c]
  (let [tag (str -tag)
        [m c] (if (map? -m) [-m -c] [{} (cons -c -m)])
        setcode (mapv (fn [[k v]] (list '.setAttribute '__el (kw->str k) v)) -m)
        c-e '.createElement]
  `(let [~'__el (~c-e ~'js/document ~tag)]
     ~@setcode
     ~'__el)))


(defn- un-dot [syms]
  (loop [col syms]
    (if (> (count col) 2)
      (let [[obj op field] (take 3 col)
            form (cond (number? op) (list 'get obj op)
                       (= '. op) (list op obj field)
                       (= (symbol ":") op) (list (keyword field) obj ))]
        (recur (cons form (drop 3 col))))
        (first col))))

(defn- slice-or-symbol [s]
  (if-let [vn (re-find #"\<([0-9]+)\>" s)]
    (read-string (last vn))
    (symbol s)))

(defmacro .* [& more]
  (let [address  (last more)
        matcher (re-matcher #"\<[0-9]+\>|[\:\.]|[^\:\.\<\>]+" (str address))
        broken (loop [col []] (if-let [res (re-find matcher)] (recur (conj col res)) col))
        s-exp (un-dot (concat (butlast more) (map slice-or-symbol broken)))]
  `(~@s-exp
     ~broken)))

(re-find #"\[[0-9]+\]|[\:\.]|[^\:\.\[\]]+" "ball:rect.x")





