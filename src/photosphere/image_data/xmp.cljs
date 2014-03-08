(ns photosphere.image-data.xmp
  (:require
    [cljs.reader :refer [read-string]]
    [clojure.string :as str]
    [dataview.ops :refer [create-reader]]
    [dataview.protocols :refer [read-utf8-string view find! seek!]]))

(def start-tag "<x:xmpmeta")
(def end-tag "</x:xmpmeta>")

(defn make-xmpmeta-reader
  "Wraps a view over a reader to only expose a section of the reader
   which has the XMP start and end tags"
  [reader]
  (when-let [start-offset (find! reader start-tag)]
    (when-let [end-offset (find! reader end-tag)]
      (seek! reader start-offset)
      (view reader (+ (count end-tag) end-offset)))))

(defn key-spec [reader]
  (let [key-str (read-utf8-string reader #{\=})]
    (keyword (subs key-str 0 (dec (count key-str))))))

(defn value-spec [reader]
  (let [value-str (read-utf8-string reader #{\newline})]
    (read-string (str/trim value-str))))

(defn attributes [reader]
  (when-let [xmpmeta-reader (make-xmpmeta-reader reader)]
    (loop [attrs {}]
      (if-not (find! xmpmeta-reader "GPano:")
        attrs
        (recur
          (assoc
            attrs
            (key-spec xmpmeta-reader)
            (value-spec xmpmeta-reader)))))))
