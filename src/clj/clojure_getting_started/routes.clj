(ns clojure-getting-started.routes
  (:require
   [clojure-getting-started.layout :as layout]
   [clojure-getting-started.db.core :as db]
   [clojure.java.io :as io]
   [clojure-getting-started.middleware :as middleware]
   [ring.util.response]
   [clojure.java.jdbc :as jdbc]
   [ring.util.http-response :as response])
  (:import
   org.jscience.physics.amount.Amount
   org.jscience.physics.model.RelativisticModel
   javax.measure.unit.SI))

(defn home [request]
  (layout/render request "home.html"))

(defn database [request]
  (layout/plain (let [ticks (jdbc/with-db-connection [connection {:datasource db/*db*}]
                              (do (jdbc/execute! connection "CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
                                  (jdbc/execute! connection "INSERT INTO ticks VALUES (now())")
                                  (map :tick (jdbc/query connection "SELECT tick FROM ticks"))))]
                  (str "Database Output\n\n" (clojure.string/join "\n" (map #(str "Read from DB: " %) ticks))))))

(defn convert [request]
  (layout/plain
   (let [energy-amount (Amount/valueOf "12 GeV")]
     (do (RelativisticModel/select)
         (str "E=mc^2: " energy-amount " = " (.to energy-amount SI/KILOGRAM))))))

(defn routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home}]
   ["/database" {:get database}]
   ["/convert" {:get convert}]])
