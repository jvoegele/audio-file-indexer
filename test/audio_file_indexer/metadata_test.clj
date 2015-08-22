(ns audio-file-indexer.metadata-test
  (:require [clojure.test :refer :all]
            [audio-file-indexer.metadata :refer :all]))

(def common-map
  {"ALBUM" "Medúlla",
   "ALBUM_ARTIST" "Björk",
   "ALBUM_ARTIST_SORT" "Guðmundsdóttir, Björk",
   "ARTIST" "Björk",
   "ARTIST_SORT" "Guðmundsdóttir, Björk",
   "DISC_NO" "1",
   "GENRE" ["Alternative Rock" "Indie Rock" "Experimental Rock"],
   "ISRC" "USRE19800805",
   "MEDIA" "CD",
   "MIXER" "Jim Scott",
   "PRODUCER" ["John" "Paul" "George" "Ringo"],
   "RECORD_LABEL" "Reprise Records",
   "TITLE" "Miðvikudags",
   "TRACK" "13",
   "YEAR" "2004-08-31"})

(def common-flac-ogg-map
  (merge common-map
         {"AMAZON_ASIN" "B00000I5JS",
          "AMAZON_ID" "B00000I5JS",
          "ARRANGER" "Burt Bacharach",
          "ARTISTS" "Björk",
          "BARCODE" "093624728221",
          "CATALOG_NO" "9362-47282-2",
          "DISC_TOTAL" "1",
          "DURATION" "23066",
          "ENCODED-BY" "Jason Voegele (jason@jvoegele.com)",
          "MUSICBRAINZ_ARTISTID" "9e53f84d-ef44-4c16-9677-5fd4d78cbd7d",
          "MUSICBRAINZ_RELEASEARTISTID" "9e53f84d-ef44-4c16-9677-5fd4d78cbd7d",
          "MUSICBRAINZ_RELEASEID" "38a40944-ac73-4c8e-8638-ec0075b170ea",
          "MUSICBRAINZ_RELEASETRACKID" "141ec33b-05fc-3e15-bf50-8d6b31f70934",
          "MUSICBRAINZ_RELEASE_COUNTRY" "US",
          "MUSICBRAINZ_RELEASE_GROUP_ID" "bb7b90b8-bdb5-455c-9c8e-f87aeaf5d156",
          "MUSICBRAINZ_RELEASE_STATUS" "StatusOfficial",
          "MUSICBRAINZ_RELEASE_TYPE" "TypeAlbum",
          "MUSICBRAINZ_TRACK_ID" "6c21eaa7-ec49-47ff-994a-0ead2368da46",
          "ORIGINALDATE" "1999-03-09",
          "ORIGINALYEAR" "1999",
          "PERFORMER"
          ["Paul McCartney (bass guitar)"
           "John Entwistle (bass guitar)"
           "George Harrison (sitar)"],
          "RELEASESTATUS" "official",
          "RELEASETYPE" "album",
          "REPLAYGAIN_TRACK_GAIN" "+64.82 dB",
          "SCRIPT" "Latn",
          "TOTALDISCS" "1",
          "TOTALTRACKS" "14",
          "TRACK_TOTAL" "14"}))

(def flac-map
  (merge common-flac-ogg-map
         {"COMPRESSION-RATIO" "0.00210938995009887",
          "ENCODER" "reference libFLAC 1.1.2 20050205",
          "ENCODING" "flac 1.1.2",
          "ENCODING-DATE" "2007-04-25T19:59:14Z",
          "REPLAYGAIN_ALBUM_GAIN" "-9.16 dB",
          "REPLAYGAIN_ALBUM_PEAK" "0.99987793",
          "REPLAYGAIN_TRACK_PEAK" "0.00003052"}))

(def ogg-map
  (merge common-flac-ogg-map
         {"COMPRESSION-RATIO" "0.00166214432564781",
          "ENCODER" "Xiph.Org libVorbis I 20050304",
          "ENCODING" "OggEnc v1.0.2, quality: 4",
          "ENCODING-DATE" "2007-04-25T20:05:29Z",
          "REPLAYGAIN_ALBUM_GAIN" "-9.04 dB",
          "REPLAYGAIN_ALBUM_PEAK" "1.34383070",
          "REPLAYGAIN_TRACK_PEAK" "0.00002711"}))

(def mp3-map
  (merge common-map
         {"TXXX:MusicBrainz Album Release Country" "US",
          "TXXX:MusicBrainz Release Track Id"
          "141ec33b-05fc-3e15-bf50-8d6b31f70934",
          "TXXX:BARCODE" "093624728221",
          "TDTG" "2008-12-27T14:40:30",
          "TXXX:MusicBrainz Artist Id" "9e53f84d-ef44-4c16-9677-5fd4d78cbd7d",
          "PERFORMER:bass guitar" ["Paul McCartney" "John Entwistle"],
          "ORIGINAL_YEAR" "1999-03-09",
          "UFID" "Owner=\"http://musicbrainz.org\"; Data=\"36 bytes\"; ",
          "TXXX:Artists" "Björk",
          "TXXX:ASIN" "B00000I5JS",
          "TXXX:CATALOGNUMBER" "9362-47282-2",
          "TXXX:SCRIPT" "Latn",
          "TXXX:MusicBrainz Album Artist Id"
          "9e53f84d-ef44-4c16-9677-5fd4d78cbd7d",
          "TXXX:originalyear" "1999",
          "TXXX:MusicBrainz Album Id" "38a40944-ac73-4c8e-8638-ec0075b170ea",
          "ENCODER" "Jason Voegele (jason@jvoegele.com)",
          "PERFORMER:sitar" "George Harrison",
          "TXXX:MusicBrainz Album Status" "official",
          "TXXX:MusicBrainz Album Type" "album",
          "TXXX:MusicBrainz Release Group Id"
          "bb7b90b8-bdb5-455c-9c8e-f87aeaf5d156",
          "TSSE" "lame version 3.97, VBR Quality: 4, New VBR"}))

(deftest read-flac-metadata-test
  (testing "Read metadata from a FLAC file"
    (is (= flac-map (read-metadata (.getFile (clojure.java.io/resource "test.flac")))))))

(deftest read-ogg-metadata-test
  (testing "Read metadata from an Ogg Vorbis file"
    (is (= ogg-map (read-metadata (.getFile (clojure.java.io/resource "test.ogg")))))))

(deftest read-mp3-metadata-test
  (testing "Read metadata from an MP3 file"
    (is (= mp3-map (read-metadata (.getFile (clojure.java.io/resource "test.mp3")))))))

