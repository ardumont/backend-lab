(ns backend-lab.datomic
  "Play with datomic"
  (:use [datomic.api :only [q db] :as d])
  (:require [clojure.pprint :as p]))

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

(def data-tx (-> "samples/eleves-data.dtm" slurp read-string))

@(d/transact conn data-tx)

;; persons
(p/pprint "person query")
(p/pprint (q '[:find ?c ?fn ?n ?profile
               :where
               [?c :person/firstname ?fn]
               [?c :person/name ?n]
               [?c :person/profile ?p]
               [?p :db/ident ?profile]] (db conn)))

;; schools
(p/pprint "school query")
(p/pprint (q '[:find ?c ?sn ?st ?sz
               :where
               [?c :school/name ?sn]
               [?c :school/town ?st]
               [?c :school/zipcode ?sz]]
             (db conn)))

;; classe
(p/pprint "classe query - 1")
(p/pprint (q '[:find ?c ?n ?y ?sn
               :where
               [?c :classe/name ?n]
               [?c :classe/year ?y]
               [?c :classe/school ?i]
               [?i :school/name ?sn]]
             (db conn)))

(p/pprint "classe query - 2")
(p/pprint (q '[:find ?n ?y ?tn ?tfn
               :where
               [?c :classe/name ?n]
               [?c :classe/year ?y]
               [?c :classe/teachers ?t]
               [?t :person/name ?tn]
               [?t :person/firstname ?tfn]]
             (db conn)))

(p/pprint "cycle query")
(p/pprint (q '[:find ?cn
               :where
               [?c :cycle/name ?cn]]
             (db conn)))

;; (->> (select domaines)
;;      (map (fn [{:keys [id_cycle domaine_nom]}]
;;             (let [m {1 -1000007 2 -1000008 3 -1000009}
;;                   cid (m id_cycle)]
;;               {:db/id (load-string (str "#db/id[:db.part/user " -1000010 "]"))
;;                :domain/name domaine_nom
;;                :domain/cycle (load-string (str "#db/id[:db.part/user " cid "]"))}))))

(p/pprint (q '[:find ?dn ?cn
             :where
             [?c :domain/name ?dn]
             [?c :domain/cycle ?cc]
             [?cc :cycle/name ?cn]]
           (db conn)))

;; (->> (select matieres)
;;      (map (fn [{:keys [id_domaine matiere_nom matiere_id]}]
;;             (let [m (zipmap (range 1 19) (map (partial * -1) (range 1000010 1000028)))
;;                   id (m id_domaine)]
;;               {:db/id (load-string (str "#db/id[:db.part/user " (- -1000027 matiere_id) "]"))
;;                :subject/name matiere_nom
;;                :subject/domain (load-string (str "#db/id[:db.part/user " id "]"))}))))

(p/pprint (q '[:find ?sn ?dn
             :where
             [?c :subject/name ?sn]
             [?c :subject/domain ?d]
             [?d :domain/name ?dn]]
           (db conn)))

;; (->> (select competences)
;;      (map (fn [{:keys [id_matiere competence_nom competence_id]}]
;;             (let [m (->> (range 1000028 1000107)
;;                          (map (partial * -1))
;;                          (zipmap (range 1 79)))
;;                   id (m id_matiere)]
;;               {:db/id (load-string (str "#db/id[:db.part/user " (- -1000107 competence_id) "]"))
;;                :skill/name competence_nom
;;                :skill/subject (load-string (str "#db/id[:db.part/user " id "]"))}))))

(p/pprint "query skills")
(p/pprint (q '[:find ?sn ?subn
               :where
               [?c :skill/name ?sn]
               [?c :skill/subject ?ss]
               [?ss :subject/name ?subn]]
             (db conn)))
