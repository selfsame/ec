(ns game.core
  (:use-macros [ec.macros :only [dom .*]])
  (:use [ec.core :only [C E report log init destroy -uid -o
                        array-remove array-unique-add]])
  (:require
   [clojure.browser.event :as event]))

(enable-console-print!)

(def KEYS (js/Array.))

(defn listen! []
  (event/listen (aget js/document "body") "keydown"
                #(do 
                   (array-unique-add KEYS (aget % "keyCode"))))
  (event/listen (aget js/document "body") "keyup" #(array-remove KEYS (aget % "keyCode")))
  (report "keycodes in window.KEYS"))

(aset js/window "KEYS" KEYS)
(aset js/window "eventListen" listen!)


(def abs (aget js/Math "abs"))


(defn in? [a v] (not= (.indexOf a v) -1))

(defn- op=fn [op]
  (fn [o prop v] (aset o prop (op (aget o prop) v))))
(def += (op=fn +))
(def -= (op=fn -))
(def *= (op=fn *))




(defn v2get [idx o nf]
  (cond (number? o) o
        (instance? js/Array o) (or (aget o idx) nf)
        (vector? o) (get o idx nf)
        (instance? js/Object o) (or (aget o ({0 "x" 1 "y"} idx)) nf)
        :else nf))

(defn- operate [op a b]
  #js [(op (v2get 0 a 0) (v2get 0 b 0))
       (op (v2get 1 a 1) (v2get 1 b 1))])

(defn- reduce-operate [op col]
  (cond (number? (first col))  (reduce #(operate op %1 %2) col)
        (instance? js/Array (first col)) (to-array (reduce #(operate op %1 %2) col))
        (vector? (first col)) (vec (reduce #(operate op %1 %2) col))
        (instance? js/Object (first col))  (clj->js (zipmap ["x" "y" "z" "w"] (reduce #(operate op %1 %2) col)))))

(defn v+ [& more] (reduce-operate + more))
(defn v- [& more] (reduce-operate - more))
(defn v* [& more] (reduce-operate * more))
(defn vdiv [& more] (reduce-operate / more))
(defn -v [op & more] (reduce-operate op more))


(def cap (fn [n lb ub] (min (max lb n) ub)))

(defn lerp [v1 v2 r]
   (v+ v1 (v* (v- v2 v1) r)))


(defn make
  ([cstr] (make cstr {}))
  ([cstr data]
   (when-let [ctor (aget (aget C "new") cstr)]
     (ctor (clj->js data)))))


(aset js/window "std"
 #js {"randInt" rand-int
      "randNth" rand-nth
      "cap" cap
      "lerp" lerp})

(defn owner [c] (aget c "owner"))
(defn ancestor-comps [o s] ((aget o "findAncestorComponents") s))
(defn comps [o s] ((aget o "findComponents") s))


(defn pivot! [cont v2]
  (aset (aget cont "pivot") "x" (v2get 0 v2 0))
  (aset (aget cont "pivot") "y" (v2get 1 v2 0)))

(defn index! [c n]
  (when-let [-i (get c "instance") ]
  (let [container (get -i "parent")
        -children (or (get container "children") [])
        idx (cap n 0 (dec (get -children "length" )))]
    (when container
      (.setChildIndex container -i idx)))))



(def HW (* js/WIDTH 0.5))
(def HH (* js/HEIGHT 0.5))


(C "camera"
   #js {:x 0 :y 0 :scale 1 :target nil :display nil}
   {"mount"
    (fn [c]
      (when-let [renderer (get (comps (owner c) "renderer") 0)]
        (let [focus (make "rect" {:w 20 :h 20 :x -3 :y -3 :fill 16711680})]
          (aset c "display" #js [(get renderer "w") (get renderer "h" )])
          (aset c "focus" focus)
          (.add (.find (owner c) "actors") focus))))
    "update"
    (fn [c]
      (let [scale (cond (in? KEYS 109) (aset c "scale" (cap (- (get c "scale") .01) 0.01 3))
                        (in? KEYS 107) (aset c "scale" (cap (+ (get c "scale") .01) 0.01 3))
                        :else (get c "scale"))
            [dw dh] (get c "display")

            target (get (owner c) "transform")
            x (get c "x")
            y (get c "y")
            [tx ty] [(aget target "x")(aget target "y")]
            dx (cond (in? KEYS 37) -10 (in? KEYS 39) 10 :else 0)
            dy (cond (in? KEYS 38) -10 (in? KEYS 40) 10 :else 0)

            [cx cy]  (v+ [x y] [dx dy])
            [lcx lcy] (lerp
                        [tx ty]
                       (vdiv (v+ (v* [x y] -1) ) scale)
                        0.2)]

        (pivot! target #js [tx ty])
        (aset (get target "scale") "x" scale)
        (aset (get target "scale") "y" scale)

        (aset target "x" lcx)
        (aset target "y" lcy)

        (aset c "x" cx)
        (aset c "y" cy)

        (aset (get (get c "focus") "instance") "x" (+ lcx (* dw 0.5)))
        (aset (get (get c "focus") "instance") "y" (+ lcy (* dh 0.5)))

        ;(index! (get c "focus") 1000)
      ))})


