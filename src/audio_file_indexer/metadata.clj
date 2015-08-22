(ns audio-file-indexer.metadata
  (:require [clojure.string :as str])
  (:import [org.jaudiotagger.audio AudioFile AudioFileIO AudioHeader]
           [org.jaudiotagger.tag Tag FieldKey]))

(def ^:private common-field-keys (seq (FieldKey/values)))

(def null-char #"\x00")

(defn- delete-field [tag field-key]
  (.deleteField tag field-key))

(defn- delete-troublesome-fields! [tag]
  (doseq [field-key [FieldKey/COVER_ART]]
    (delete-field tag field-key)))

(defn- assoc-append [map key value]
  (if (and key value)
    (let [current-value (get map key)]
      (assoc map key
             (cond
               (nil? current-value) value
               (coll? current-value) (conj current-value value)
               :else [current-value value])))
    map))

(defn- split
  ([data] (split data null-char))
  ([data splitter]
   (str/split data splitter)))

(defn- split-if-null-char [s]
  (let [split-string (str/split s null-char)]
    (if (= 1 (count split-string))
      (first split-string)
      split-string)))

(defn- keyed-seq->map [in-seq out-map key-map]
  (let [map-keys (fn [pair]
                   (let [key (first pair) val (second pair)]
                     [(key-map key) val]))
        entries (map map-keys (partition 2 in-seq))]
    (reduce
      (fn [acc e] (assoc-append acc (first e) (second e)))
      out-map
      entries)))

(defn- extract-id3-special! [tag metadata-map field-key key-map]
  (if-let [tipl (.getFirstField tag field-key)]
    (let [values (split (.getContent tipl))]
      (delete-field tag field-key)
      (keyed-seq->map values metadata-map key-map))))

(defn- tipl-map [tipl-key]
  (get {"mix" "MIXER" "DJ-mix" "DJMIXER"}
       tipl-key
       (str/upper-case tipl-key)))

(defn- extract-tipl! [tag metadata-map]
  (extract-id3-special! tag metadata-map "TIPL" tipl-map))

(defn- extract-tmcl! [tag metadata-map]
  (extract-id3-special! tag metadata-map "TMCL" #(str "PERFORMER:" %)))

(def ^:private txxx "TXXX")

(defn- extract-txxx-fields! [tag metadata-map]
  (let [txxx-fields (.getFields tag txxx)]
    (.deleteField tag txxx)
    (reduce (fn [field-map field]
              (let [field-key (str txxx ":" (.getDescription (.getBody field)))
                    val (.getContent field)]
                (assoc field-map field-key val)))
            metadata-map
            txxx-fields)))

(defn- filter-not-binary [fields]
  (filter #(not (.isBinary %)) fields))

(defn- field-value [tag field-key]
  (let [values (filter-not-binary (.getFields tag field-key))]
    (cond
      (empty? values) nil
      (= 1 (count values)) (split-if-null-char (.getContent (first values)))
      :else (vec (map #(.getContent %) values)))))

(defn- extract-common-fields! [tag metadata-map]
  (reduce (fn [field-map field-key]
            (let [val (field-value tag field-key)]
              (if val
                (do (.deleteField tag field-key)
                    (assoc field-map (str field-key) val))
                field-map)))
          metadata-map
          common-field-keys))

(defn- assoc-field [metadata-map field]
  (let [content (.getContent field)
        field-id (.getId field)
        value (get metadata-map field-id)]
    (assoc-append metadata-map field-id content)))

(defn- extract-remaining-fields! [tag metadata-map]
  (let [fields (iterator-seq (.getFields tag))]
    (reduce assoc-field metadata-map fields)))

(defn- tag->map [tag]
  (delete-troublesome-fields! tag)
  (into (sorted-map)
        (->> {}
             (extract-tipl! tag)
             (extract-tmcl! tag)
             (extract-txxx-fields! tag)
             (extract-common-fields! tag)
             (extract-remaining-fields! tag))))

(defn- read-tag [audio-file]
  (.getTag (AudioFileIO/read (clojure.java.io/file audio-file))))

(defn read-metadata [audio-file]
  (tag->map (read-tag audio-file)))
