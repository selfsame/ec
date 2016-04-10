(ns game.util)

;dom interop

(defn animation-frame [f]
  (.requestAnimationFrame js/window f))

(defn append-child [el node]
  (.appendChild el node))

;canvas

(defn fill-style [ctx color] (aset ctx "fillStyle" color))

(defn fill-rect [ctx x y w h] (.fillRect ctx x y w h))

(defn clear-rect [ctx x y w h] (.clearRect ctx x y w h))


;vec op overloading

(defn- operate [op a -b]
  (let [b (cond (number? -b) (vec (take (count a) (repeat -b)))
             (list? -b) (vec -b)
             :else -b)]
  (map-indexed #(op %2 (get b %1 0)) a)))


(defn- redop [op col]
  (vec (reduce #(operate op %1 %2) col)))

(defn v+ [& more] (redop + more))
(defn v- [& more] (redop - more))
(defn v* [& more] (redop * more))
(defn vdiv [& more] (redop / more))




(defn intersect [[ax ay aw ah][bx by bw bh]]
  (v- (v+ [bx bx by by] (v* [bw (- bw) bh (- bh)] 0.5))
      (v+ [ax ax ay ay] (v* [aw (- aw) ah (- ah)] 0.5)) ))


(defn relation [rect]
  (let [[l r t b] (mapv pos? rect)]
    [(case [l r]
       [true true] :inside
       [false false] :outside
       [true false] :right
       [false true] :left nil)
     (case [t b]
       [true true] :inside
       [false false] :outside
       [true false] :above
       [false true] :below nil)] ))

(relation (intersect  [10 20 100 100] [70 40 50 50]))
(relation (intersect  [0 0 100 100] [50 50 10 10]))
(relation (intersect  [0 0 100 100] [-10 90 20 80]))



