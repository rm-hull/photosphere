(ns photosphere.image-data.xmp-test
  (:use-macros
    [cljs-test.macros :only [deftest is= is is-thrown?]])
  (:require
    [cljs-test.core :as test]
    [dataview.ops :as ops]
    [dataview.protocols :as proto]
    [photosphere.image-data.xmp :as xmp]))

(def test-data "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\" x:xmptk=\"Adobe XMP Core 5.1.0-jc003\">
  <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">
    <rdf:Description rdf:about=\"\"
        xmlns:GPano=\"http://ns.google.com/photos/1.0/panorama/\"
      GPano:UsePanoramaViewer=\"True\"
      GPano:ProjectionType=\"equirectangular\"
      GPano:CroppedAreaImageHeightPixels=\"1645\"
      GPano:CroppedAreaImageWidthPixels=\"4316\"
      GPano:FullPanoHeightPixels=\"2158\"
      GPano:FullPanoWidthPixels=\"4316\"
      GPano:CroppedAreaTopPixels=\"182\"
      GPano:CroppedAreaLeftPixels=\"0\"
      GPano:FirstPhotoDate=\"2013-08-19T10:28:00.52Z\"
      GPano:LastPhotoDate=\"2013-08-19T10:29:55.73Z\"
      GPano:SourcePhotosCount=\"45\"
      GPano:PoseHeadingDegrees=\"262.0\"
      GPano:LargestValidInteriorRectLeft=\"0\"
      GPano:LargestValidInteriorRectTop=\"0\"
      GPano:LargestValidInteriorRectWidth=\"4316\"
      GPano:LargestValidInteriorRectHeight=\"1645\"/>
  </rdf:RDF>
</x:xmpmeta>")

(deftest xmpmeta-reader-check
  (let [reader (xmp/make-xmpmeta-reader (ops/create-reader test-data))]
    (is= (proto/read-fixed-string reader (count test-data))
         test-data
         "XMP meta spec slurped OK")))

(deftest keyspec-check
  (let [keyvalue "GPano:LargestValidInteriorRectWidth=\"4316\"\n"
        reader (ops/create-reader keyvalue)]
    (is= (xmp/key-spec reader) :GPano:LargestValidInteriorRectWidth "Key fetched OK")))

(deftest valuespec-check
  (let [value "\"4316\"\n"
        reader (ops/create-reader value)]
    (is= (xmp/value-spec reader) "4316" "Value fetched OK")))

(deftest read-attributes
  (let [reader (ops/create-reader test-data)
        attrs (xmp/attributes reader)]
    (is= (count attrs) 16 "Correct attribute count")

    (is= attrs {:GPano:FirstPhotoDate "2013-08-19T10:28:00.52Z",
                :GPano:FullPanoWidthPixels "4316",
                :GPano:LargestValidInteriorRectWidth "4316",
                :GPano:SourcePhotosCount "45",
                :GPano:FullPanoHeightPixels "2158",
                :GPano:CroppedAreaTopPixels "182",
                :GPano:PoseHeadingDegrees "262.0",
                :GPano:LargestValidInteriorRectHeight "1645",
                :GPano:LargestValidInteriorRectLeft "0",
                :GPano:CroppedAreaLeftPixels "0",
                :GPano:LargestValidInteriorRectTop "0",
                :GPano:CroppedAreaImageWidthPixels "4316",
                :GPano:CroppedAreaImageHeightPixels "1645",
                :GPano:LastPhotoDate "2013-08-19T10:29:55.73Z",
                :GPano:ProjectionType "equirectangular",
                :GPano:UsePanoramaViewer "True"}
         "Attributes match expected"
         )))
