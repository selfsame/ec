(ns ec.core
  (:refer-clojure :exclude [update])
  (:require-macros
   [ec.macros :refer [dom gentype def-api]]))

(enable-console-print!)

(declare Ent)

(def NAME->UID (js/Object.))
(defonce UID->OBJ (atom {}))
(defonce BIND->NEWFN (atom {}))
(defonce BIND->UIDSET (atom {}))

(defonce COMPOCOLS (atom {}))

(defonce MANDATORY (atom []))

(defonce UID (atom 0))

(def reserved #{"e" "uid"})

(defn remove! [o k] (goog.object.remove o k))

(defn log [& x] (mapv #(.log js/console %) x))

(defn report [x] (.log js/console (str "%c cljs %c " x) "background: #bada55;" "background:white;"))


(defn- kw->str [s]
  (apply str (rest (str s))))

(defn array-remove [o v]
  (let [i (.indexOf o v)]
    (when (not= -1 i) (.splice o i 1) true)))

(defn array-unique-add [o v]
  (let [i (.indexOf o v)]
    (if (= -1 i) (.push o v))))




(def __cache__ (js/Array.))

(defn __all__ [f] (.every __cache__ (fn [o] (f o) true)))



(defprotocol IUid
  (-uid [o])
  (-o [o]))


(defprotocol IThing
  (init [o])
  (mount [o])
  (unmount [o])
  (update [o] [o delta])
  (destroy [o])
  (serialize [o])
  (deserialize [o data])
  (HTML [o]))

(def proto-map
  {:init        #(init %)
   :update      (fn ([] (__all__ update))([o] (__all__ update)))
   :mount       #(mount %)
   :unmount     #(unmount %)
   :destroy     #(destroy %)
   :serialize   #(serialize %)
   :deserialize #(deserialize %1 %2)
   :HTML        #(HTML %)})




;; from https://github.com/dribnet/mrhyde/blob/master/src/cljs/mrhyde/typepatcher.cljs
(def install-js-hidden-get-prop
  ((fn []
     (let [desc (js-obj)]
       (aset desc "configurable" true)
       (aset desc "enumerable" false)
       (fn [obj nam getfn]
         (aset desc "get" getfn)
         (.defineProperty js/Object obj nam desc))))))

(def property-lock!
  ((fn []
     (let [desc (js-obj)]
       (aset desc "enumerable" false)
       (aset desc "writable" false)
       (fn [obj nam]
         (.defineProperty js/Object obj nam desc))))))



(defn object-display [o]
  (let [ks ((aget js/window "locals") o)
        res (.map ks 
              (fn [k]
                (if-not (or (= "_api_" k) (re-find #"^ec\$core" k))
                  (let [v (aget o k)]
                  (str "<object> " k ":"
                    (cond 
                      (number? v) v
                      (-uid v) (str "<button onclick='var tUID = _oO(" (-uid v) "); inspect(tUID);'>" (-uid v)"</button>")
                      (fn? v) (str "<function>" "fn" "</function>" )
                      :else (prn-str v))
                    "</object>")) )))]
    (if (aget o "type") 
    (str "<type>" 
      (aget o "type") 
      "<uid>" (-uid o) "</uid></type>"
      (apply str res) "")
    (apply str res))))


(defn fast-iterate [col f]
  (let [c (count col)]
    (loop [i 0]
      (when (< i c)
        (f i (aget col i))
        (recur (inc i))))))


(defn forget! [v]
  "[& o] will remove uid'd object from all internal lookup maps"
  (when-let [uid (-uid v)]
    (when-let [n (aget v "name")]
      (when-let [namearray (aget NAME->UID n)]
        (array-remove namearray uid)))
    (array-remove __cache__ v)
    (swap! UID->OBJ dissoc uid) uid))



(extend-type default
  ICloneable
  (-clone [o]
   (let [o2 (js/____c o)]
     (.map (js/locals o) #(aset o2 % (-clone (aget o %)))) o2))
  ISeqable
  (-seq [o] (map #(list % (aget o %)) (.keys js/Object o)))
  ILookup
  (-lookup
    ([this k] (-lookup this (if (keyword? k) (kw->str k) k) nil))
    ([this k not-found]
      (let [v (aget this (if (keyword? k) (kw->str k) k))]
        (or v not-found))))
  IMapEntry
  (-key [o] (first o))
  (-val [o] (last o))
 IThing
  (init [me])
  (update ([o])([o d]))
  (mount [o])
  (unmount [o])
  (destroy [o])
  (serialize [o])
  (deserialize [o data])
  (HTML [o] (object-display o)))


(extend-type array
  ICloneable
  (-clone [o]
   (let [o2 (js/____c o (.-length o))]
     (fast-iterate o #(aset o2 %1 (-clone %2))) o2)))

(extend-type js/Uint8Array
  ICloneable
  (-clone [o]
   (let [o2 (js/____c o (.-length o))]
     (fast-iterate o #(aset o2 %1 (-clone %2))) o2)))

(extend-type string
  ICloneable
  (-clone [o] (.valueOf o)))

(extend-type function
  ICloneable
  (-clone [o] (.valueOf o)))

(extend-type number
  ICloneable
  (-clone [o] (.valueOf o)))

(extend-type nil
  IUid
  (-uid [o] nil)
  (-o [o] nil)
  ICloneable
  (-clone [o] nil)
 IThing
  (init [o])
  (mount [o])
  (unmount [o])
  (update ([o])([o d]))
  (destroy [o])
  (serialize [o])
  (deserialize [o data])
  (HTML [o] " undefined "))




(extend-type number
  IUid
  (-uid [o] (.valueOf o))
  (-o [o] (get @UID->OBJ (int o))))

(extend-type default
  IUid
  (-uid [o] (aget o "uid"))
  (-o [o] o))

(defn propagate [o f]
  (.map (.map (:c @o) -o) f)
  (.map (.map (:e @o) -o) f))




(deftype Ent [data]
  Object
  (toString [o] (str "<E " (aget o "name") ">"))
  IDeref
  (-deref [this] data)
  ISwap
  (-swap! [o f] (aset o "data" (f data)))
  (-swap! [o f x] (aset o "data" (f data x)))
  (-swap! [o f x y] (aset o "data" (f data x y)))
  (-swap! [o f x y z] (aset o "data" (f data x y z)))
  IThing
     (init [o])
     (update [o])
     (update [o d])
     (mount [o]
       (aset o "mounted" true)
       (propagate o mount))
     (unmount [o]
       (aset o "mounted" false)
       (propagate o unmount))
     (destroy [o]
       (if (aget o "owner") (.remove (aget o "owner") o))
       (propagate o destroy)
       (forget! o))
     (serialize [o] (propagate o serialize))
     (deserialize [o v] (propagate o deserialize))
     (HTML [o] (str "<entity><type>"(aget o "name")"<uid>"(-uid o)"</uid></type>"
                (apply str (map #(str "<component>" (HTML %) "</component>") (aget o "components")))
                "<children>"
                (apply str (.map (aget o "children") HTML))
                "</children></entity>")))





(def-api ChildAPI [o p]
  "owner" {:doc "Returns the owning entity."
            :get (fn [] p)})


(def-api UIDAPI [o]
  "uid" {:lock "uid"
         :doc "protected uid int"})



(def-api EntityAPI [o]
  "type" {:doc "always 'Entity'" :get (fn [] "Entity")}
  "mounted" {:doc "don't touch" :value false}
  "children" {:doc " Array of direct child entities."
             :get (fn [] (.map (:e @o) -o))}
  "components" {:doc " Array of components."
          :get (fn [] (.map (:c @o) -o))}
  "recur" {:doc " [f] applies f to all children and components."
          :value (fn [f] )}
  "remove"
  {:doc " [o] unmounts a component or entity."
   :value (fn [v] (if-let [uid (-uid v)]
                    (if-let [obj (-o uid)]
                      (if (instance? Ent obj)
                        (when (array-remove (:e @o) uid)
                          (if (aget o "mounted") (unmount obj))
                          (ChildAPI obj nil))
                        (if-let [comp-type (aget obj "type")]
                          (when (array-remove (:c @o) uid)
                            (destroy obj)
                            (aset o comp-type (aget (.filter (:c @o) #(= (aget % "type") comp-type)) 0))))
                        ))))}
  "add"
  {:doc " [o] mounts a component or entity."
   :value (fn [v]
            (if-let [uid (-uid v)]
              (if-let [obj (-o uid)]
                (if (instance? Ent obj)
                  (when (array-unique-add (:e @o) uid)
                    (ChildAPI obj o)
                    (if (aget o "mounted") (mount obj)))
                  (if-let [comp-type (aget obj "type")]
                    (when (array-unique-add (:c @o) uid)
                      (ChildAPI obj o)
                      (if (aget o "mounted") (mount obj))
                      (when-not (aget o comp-type)
                        (aset o comp-type obj))))))))}

  "destroy"
  {:doc " [] destroys this entity and all ancestor components and children."
   :value (fn [] (destroy o))})



(def-api ComponentAPI [o bind]
  "type" {:doc "the component type string" :get (fn [] bind)})


(def-api FinderAPI [o]
  "find" {:doc "[string *boolean] globally finds entity or undefined (optional boolean true will return Array)"
          :value (fn [s & all]
                   (when-let [named (aget NAME->UID s)]
                     (if (first all)
                       (.map named -o)
                       (-o (aget named 0)))))}
  "findComponents" {:doc "[string] returns Array of found components attached to this entity."
          :value (fn [s] (let [here (.filter (.map (:c @o) -o) #(= (aget % "type") s))]
                           (.apply (.-concat here) here
                                   (.map (.map (:e @o) -o) #((aget % "findComponents") % s)))))}
  "findAncestorComponents" {:doc "[string] returns Array of found components attached to this entity."
          :value (fn [s]
                   (if-not (aget o "owner")  (js/Array.)
                     (loop [obj (aget o "owner")
                            res  (js/Array.)]
                       (let [found (.concat res (.filter
                                               (.map (:c @obj) -o)
                                              #(= (aget % "type") s)))]
                       (if-not (aget obj "owner") found
                         (recur (aget obj "owner")
                                found))))))})




(defn E [tag & more]
  (let [[nombre parts] (if (string? tag) [tag more] ["" (or (remove nil? (cons tag more)) '())])
        o (Ent. {:e (js/Array.) :c (js/Array.)})]
    (let [uid (swap! UID inc)]
      (aset o "uid" uid)
      (aset o "name" nombre)
        (when-let [named (or (aget NAME->UID nombre)
                           (do (aset NAME->UID nombre (js/Array.))
                               (aget NAME->UID nombre)))]
        (array-unique-add named uid))
      (swap! UID->OBJ conj {uid o}))
  (EntityAPI (FinderAPI (UIDAPI o)))
  (mapv #(.add o (%)) @MANDATORY)
  (mapv #(.add o %) parts)

  o))



(defn C [bind data protocols]
  (let [ctor (if (fn? data)
               (fn [] (js/____pc data (get protocols "args")))
               (fn [] data))
        valid-protocols
        (select-keys (js->clj protocols) (map clj->js (keys proto-map)))
        _init (or (get valid-protocols "init") (fn [o]))
        _mount (or (get valid-protocols "mount") (fn [o]))
        _unmount (or (get valid-protocols "unmount") (fn [o]))
        _update (or (get valid-protocols "update") (fn ([o])([o d])))
        _destroy (or (get valid-protocols "destroy") (fn [o]))
        _HTML (or (get valid-protocols "HTML")
               (fn [o] (apply str (object-display o))))
        structor
        (fn [o]
          (let [uid (swap! UID inc)
                instance
               (specify o
                IThing
                  (init [o] (_init o))
                  (mount [o] (array-unique-add __cache__ o) (_mount o))
                  (unmount [o] (array-remove __cache__ o) (_unmount o))
                  (update [o] (_update o))
                  (destroy [o] (_destroy o) (forget! o))
                  (HTML [o] (_HTML o))

                 )]
            (aset instance "uid" uid)
            (UIDAPI instance)

            (ComponentAPI instance bind)

            (swap! UID->OBJ conj {uid instance})

            instance))
        newfn (fn [& more]
                (let [o (structor (ctor))
                      opts (first more)]
                  (if opts (.map (js/locals opts) #(aset o % (aget opts %))))
                  (init o)
                   o)) ]
    (swap! COMPOCOLS conj {bind valid-protocols})
    (swap! BIND->NEWFN conj {bind newfn})
    (aset (aget js/C "new") bind newfn)
    newfn))


(defn add-mandatory [& c]
  (swap! MANDATORY concat c))

(mapv
  (fn [[k v]]
    (aset E (clj->js k) v)
    (property-lock! E (clj->js k)))
  proto-map)

(aset E "mandate" add-mandatory)

(aset C "new" (js-obj))

(when-not (.-C js/window )
  (aset js/window "C" C)
  (aset js/window "E" E))



(defn inspect [e]
  (let [debug (or (.getElementById js/document "debug")
                  (dom div {:id "debug" :style "position:absolute;right:0px;top:0px;width:50%;white-space:pre;"}))]
    (aset debug "innerHTML" (HTML e))
    (.appendChild (.-body js/document) debug)))

(aset js/window "inspect" inspect)

(aset js/window "__all__" __all__)

(aset js/window "_oO" -o)
(aset js/window "_uid" -uid)

((aget js/window "ec_hello"))



