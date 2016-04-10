(ns ec.macros)

(defn- kw->str [s]
  (apply str (rest (str s))))

(defmacro dom [-tag -m & -c]
  (let [tag (str -tag)
        [m c] (if (map? -m) [-m -c] [{} (cons -c -m)])
        setcode (mapv (fn [[k v]] (list '.setAttribute '__el (kw->str k) v)) -m)
        c-e '.createElement]
  `(let [~'__el (~c-e ~'js/document ~tag)]
     ~@setcode
     ~'__el)))


(defmacro gentype [s args & code]
  (let [sym# (gensym s)]
   `(deftype ~sym# ~args ~@code)))



(defmacro def-api [sym args & spec]
  (let [apiname (str sym)
        o (first args)
        entries (filter #(string? (first %)) (partition 2 spec))
        api (str sym "\n=======\n" (apply str
              (mapv (fn [[k v]]
                      (str k ": " (or (:doc v) "no doc.") "\n"))
               entries)) "\n")
        code (mapcat
               (fn [[s f]]
                 (cond
                   (and (map? f) (:get f))
                   [(list 'install-js-hidden-get-prop o s (:get f))]

                   (and (map? f) (:value f))
                   [(list 'aset o s (:value f))]

                   (:lock f)
                   [(list 'property-lock! o s)]

                   :else
                   [])) entries)]
    `(def ~sym
       (fn ~args
         ~@code
         (~'aset ~o "_api_" (str (~'aget ~o "_api_") ~api))
         (~'install-js-hidden-get-prop ~o "API" #(~'report (~'aget ~o "_api_")))
         ~o))))




(defn- un-dot [syms]
  (loop [col syms]
    (if (< (count col) 2)
      (first col)
      (let [[obj op field] (take 3 col)
            form (cond (number? field) (list 'get obj field)
                       (#{"." '.} op) (list '. obj (symbol (str "-" field))))]
        (recur (cons form (drop 3 col)))))))



(defn break-symbol [sym]
  (let [matcher (re-matcher #"[\.]|[^\.]+" (str sym))
        res (loop [col []] (if-let [res (re-find matcher)] (recur (conj col res)) col))]
    (cons (symbol (first res)) (rest res))))

(defmacro .* [& more]
  (let [-address  (map (fn [part]
                        (cond (vector? part)
                              (list "." (get part 0))
                              (symbol? part)
                              (break-symbol part)))
                      more)
        address (apply concat -address)
        s-exp (un-dot address)]
  `(~@s-exp)))



(macroexpand '(.* root.children[0].position.x))


