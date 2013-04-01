(ns backend-lab.datomic-tryout
  "Play with datomic"
  (:use [datomic.api :only [q db] :as d]
        [clojure.pprint]))

;; store database uri
(def uri "datomic:mem://eleves")

;; recreate database each compilation time
(let [already-up? (-> uri d/create-database not)]
  (if already-up?
    (do
      (d/delete-database uri)
      (d/create-database uri))))

;; connect to database
(def conn (d/connect uri))

(def schema-tx (-> "samples/eleves-schema.dtm" slurp read-string))

@(d/transact conn schema-tx)
;; {:db-before datomic.db.Db@6a643996, :db-after datomic.db.Db@18cd6827, :tx-data [#Datum{:e 13194139534316 :a 50 :v #inst "2013-03-31T17:38:40.417-00:00" :tx 13194139534316 :added true} #Datum{:e 0 :a 13 :v 62 :tx 13194139534316 :added true} #Datum{:e 0 :a 13 :v 63 :tx 13194139534316 :added true} #Datum{:e 0 :a 13 :v 64 :tx 13194139534316 :added true}], :tempids {-9223350046623220293 17592186045419, -9223350046623220292 17592186045418, -9223350046623220291 17592186045417, -9223367638809264706 64, -9223367638809264705 63, -9223367638809264704 62}}

(def data-tx (-> "samples/eleves-data.dtm" slurp read-string))

@(d/transact conn data-tx)

;; persons
(pprint (q '[:find ?c ?fn ?n ?profile
             :where [?c :person/firstname ?fn]
                    [?c :person/name ?n]
                    [?c :person/profile ?p]
             [?p :db/ident ?profile]] (db conn)))

;; schools
(pprint (q '[:find ?sn ?st ?sz
             :where [?c :school/name ?sn]
                    [?c :school/town ?st]
                    [?c :school/zipcode ?sz]]
           (db conn)))

;; classe
(pprint (q '[:find ?n ?y ?sn
             :where [?c :classe/name ?n]
                    [?c :classe/year ?y]
                    [?c :classe/school ?i]
                    [?i :school/name ?sn]]
           (db conn)))
