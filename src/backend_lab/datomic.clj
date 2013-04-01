(ns backend-lab.datomic
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
(pprint "person query")
(pprint (q '[:find ?c ?fn ?n ?profile
             :where
             [?c :person/firstname ?fn]
             [?c :person/name ?n]
             [?c :person/profile ?p]
             [?p :db/ident ?profile]] (db conn)))

;; schools
(pprint "school query")
(pprint (q '[:find ?c ?sn ?st ?sz
             :where
             [?c :school/name ?sn]
             [?c :school/town ?st]
             [?c :school/zipcode ?sz]]
           (db conn)))

;; classe
(pprint "classe query - 1")
(pprint (q '[:find ?c ?n ?y ?sn
             :where
             [?c :classe/name ?n]
             [?c :classe/year ?y]
             [?c :classe/school ?i]
             [?i :school/name ?sn]]
           (db conn)))

(pprint "classe query - 2")
(pprint (q '[:find ?n ?y ?tn ?tfn
             :where
             [?c :classe/name ?n]
             [?c :classe/year ?y]
             [?c :classe/teachers ?t]
             [?t :person/name ?tn]
             [?t :person/firstname ?tfn]]
           (db conn)))

(pprint "cycle query")
(pprint (q '[:find ?cn
             :where
             [?c :cycle/name ?cn]]
           (db conn)))

;; (->> (select domaines)
;;      (map (fn [{:keys [id_cycle domaine_nom]}]
;;             (let [m {1 -1000007 2 -1000008 3 -1000009}
;;                   cid (m id_cycle)]
;;               {:db/id (str "#db/id[:db.part/user " -1000010 "]")
;;                :domain/name domaine_nom
;;                :domain/cycle (str "#db/id[:db.part/user " cid "]")}))))


(pprint (q '[:find ?dn ?cn
             :where
             [?c :domain/name ?dn]
             [?c :domain/cycle ?cc]
             [?cc :cycle/name ?cn]]
           (db conn)))

;; (->> (select matieres)
;;      (map (fn [{:keys [id_domaine matiere_nom matiere_id]}]
;;             (let [m (zipmap (range 1 19) (map (partial * -1) (range 1000010 1000028)))
;;                   id (m id_domaine)]
;;               {:db/id (str "#db/id[:db.part/user " (- -1000028 matiere_id) "]")
;;                :subject/name matiere_nom
;;                :subject/domain (str "#db/id[:db.part/user " id "]")}))))

(pprint (q '[:find ?sn ?dn
             :where
             [?c :subject/name ?sn]
             [?c :subject/domain ?d]
             [?d :domain/name ?dn]]
           (db conn)))
