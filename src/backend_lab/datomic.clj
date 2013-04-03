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
(-> '[:find ?c ?fn ?n ?profile
      :where
      [?c :person/firstname ?fn]
      [?c :person/name ?n]
      [?c :person/profile ?p]
      [?p :db/ident ?profile]]
    (q (db conn))
    seq
    p/pprint)

;; persons
(p/pprint "teacher query")
(-> '[:find ?fn ?n
      :where
      [?c :teacher/firstname ?fn]
      [?c :teacher/name ?n]]
    (q (db conn))
    seq
    p/pprint)

;; schools
(p/pprint "school query")
(-> '[:find ?c ?sn ?st ?sz
      :where
      [?c :school/name ?sn]
      [?c :school/town ?st]
      [?c :school/zipcode ?sz]]
    (q (db conn))
    seq
    p/pprint)

;; classe
(p/pprint "classe query - 1")
(-> '[:find ?c ?n ?y ?sn
      :where
      [?c :class/name ?n]
      [?c :class/year ?y]
      [?c :class/school ?i]
      [?i :school/name ?sn]]
    (q (db conn))
    seq
    p/pprint)

(p/pprint "classe query - 2")
(-> '[:find ?n ?y ?tn ?tfn
      :where
      [?c :class/name ?n]
      [?c :class/year ?y]
      [?c :class/teachers ?t]
      [?t :teacher/name ?tn]
      [?t :teacher/firstname ?tfn]]
    (q (db conn))
    seq
    p/pprint)

;; (p/pprint "teacher query - 2 - teacher's class")
;; (-> '[:find ?n ?y ?tn ?tfn
;;       :where
;;       [?t :teacher/name ?tn]
;;       [?t :teacher/firstname ?tfn]
;;       [?cc :class/teachers ?t]]
;;     (q (db conn))
;;     seq
;;     p/pprint)

(p/pprint "cycle query")
(-> '[:find ?cn
      :where
      [?c :cycle/name ?cn]]
    (q (db conn))
    seq
    p/pprint)

;; (->> (select domaines)
;;      (map (fn [{:keys [id_cycle domaine_nom]}]
;;             (let [m {1 -1000007 2 -1000008 3 -1000009}
;;                   cid (m id_cycle)]
;;               {:db/id (load-string (str "#db/id[:db.part/user " -1000010 "]"))
;;                :domain/name domaine_nom
;;                :domain/cycle (load-string (str "#db/id[:db.part/user " cid "]"))}))))

(-> '[:find ?dn ?cn
      :where
      [?c :domain/name ?dn]
      [?c :domain/cycle ?cc]
      [?cc :cycle/name ?cn]]
    (q (db conn))
    seq
    p/pprint)

;; (->> (select matieres)
;;      (map (fn [{:keys [id_domaine matiere_nom matiere_id]}]
;;             (let [m (zipmap (range 1 19) (map (partial * -1) (range 1000010 1000028)))
;;                   id (m id_domaine)]
;;               {:db/id (load-string (str "#db/id[:db.part/user " (- -1000027 matiere_id) "]"))
;;                :subject/name matiere_nom
;;                :subject/domain (load-string (str "#db/id[:db.part/user " id "]"))}))))

(-> '[:find ?sn ?dn
      :where
      [?c :subject/name ?sn]
      [?c :subject/domain ?d]
      [?d :domain/name ?dn]]
    (q (db conn))
    seq
    p/pprint)

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
(-> '[:find ?sn ?subn
      :where
      [?c :skill/name ?sn]
      [?c :skill/subject ?ss]
      [?ss :subject/name ?subn]]
    (q (db conn))
    seq
    p/pprint)

(p/pprint "query levels")
(-> '[:find ?ln ?cn
      :where
      [?c :level/name ?ln]
      [?c :level/cycle ?lc]
      [?lc :cycle/name ?cn]]
    (q (db conn))
    seq
    p/pprint)

(p/pprint "query periods")
(-> '[:find ?pn ?ps ?pe
      :where
      [?c :period/name ?pn]
      [?c :period/begin ?ps]
      [?c :period/end ?pe]]
    (q (db conn))
    seq
    p/pprint)

;; (->> (select eleves)
;;      (map (fn [{:keys [eleve_nom eleve_date_naissance eleve_actif]}]
;;             (let [pn  (clojure.string/split eleve_nom #" ")
;;                   name (first pn)
;;                   fname (second pn)]
;;               {:db/id (load-string (str "#db/id[:db.part/user]"))
;;                :pupil/name name
;;                :pupil/firstname fname
;;                :pupil/birthdate eleve_date_naissance
;;                :pupil/active? eleve_actif}))))

(p/pprint "pupils query")
(-> '[:find ?pn ?pfn ?pb ?pa
      :where
      [?c :pupil/name ?pn]
      [?c :pupil/firstname ?pfn]
      [?c :pupil/birthdate ?pb]
      [?c :pupil/active? ?pa]]
    (q (db conn))
    seq
    p/pprint)

(p/pprint "marks query")
(-> '[:find ?mn ?ml ?mv
      :where
      [?c :mark/name ?mn]
      [?c :mark/label ?ml]
      [?c :mark/value ?mv]]
    (q (db conn))
    seq
    p/pprint)

(p/pprint "profiles query")
(-> '[:find ?pn ?pd
      :where
      [?c :profile/name ?pn]
      [?c :profile/description ?pd]]
    (q (db conn))
    seq
    p/pprint)

;; (->> (select evaluations_collectives)
;;      (map (fn [{:keys [id_classe id_periode eval_col_date eval_col_description eval_col_nom eval_col_id]}]
;;             (let [m-eval (->> (range 1034317 1034323)
;;                               (map (partial * -1))
;;                               (zipmap (range 1 6)))
;;                   m-class {1 -1000005
;;                            2 -1000006}
;;                   m-period {1 -1000691
;;                             2 -1000692
;;                             3 -1000693
;;                             4 -1000694
;;                             5 -1000695}]
;;               {:db/id (load-string (str "#db/id[:db.part/user " (m-eval eval_col_id) "]"))
;;                :coll-eval/name eval_col_nom
;;                :coll-eval/description eval_col_description
;;                :coll-eval/date eval_col_date
;;                :coll-eval/class (load-string (str "#db/id[:db.part/user " (m-class id_classe) "]"))
;;                :coll-eval/period (load-string (str "#db/id[:db.part/user " (m-period id_periode) "]"))}))))

(p/pprint "collectives evaluations query")
(-> '[:find ?cen ?ced ?cedate ?cep ?cn ?pn
      :where
      [?c :coll-eval/name ?cen]
      [?c :coll-eval/description ?ced]
      [?c :coll-eval/date ?cedate]
      [?c :coll-eval/period ?cep]
      [?c :coll-eval/class ?cec]
      [?cec :class/name ?cn]
      [?c :coll-eval/period ?cep]
      [?cep :period/name ?pn]]
    (q (db conn))
    seq
    p/pprint)

;; utility function to help in transcoding id from mysql to datomic
;; (defn map-ids
;;   "Compute the map of tempids from mysql to datomic"
;;   [s e]
;;   (->> (range s e)
;;        (map (partial * -1))
;;        (zipmap (range 1 (+ 1 (- e s))))))

;; (into (sorted-map) (map-ids 1100000 (+ 1100000 5872)))

;; (let [m-ind-evals (map-ids 1100000 (+ 1100000 (count (select evaluations_individuelles))))
;;       m-notes (map-ids 1034309 (+ 1034309 (count (select notes))))
;;       m-eleves (map-ids 1034265 (+ 1034265 (count (select eleves))))
;;       m-coll-evals (map-ids 1034317 (+ 1034317 (count (select evaluations_collectives))))
;;       m-comp (->> (select competences)
;;                   (reduce (fn [m {:keys [competence_id]}]
;;                             (assoc m competence_id (- -1000107 competence_id)))
;;                           {}))]
;;   (->> (select evaluations_individuelles)
;;        (map (fn [{:keys [id_note id_eleve id_eval_col id_competence eval_ind_commentaire eval_ind_id]}]
;;               {:db/id              (load-string (str "#db/id[:db.part/user " (m-ind-evals eval_ind_id) "]"))
;;                :ind-eval/mark      (load-string (str "#db/id[:db.part/user " (m-notes id_note) "]"))
;;                :ind-eval/comment   eval_ind_commentaire
;;                :ind-eval/pupil     (load-string (str "#db/id[:db.part/user " (m-eleves id_eleve) "]"))
;;                :ind-eval/coll-eval (load-string (str "#db/id[:db.part/user " (m-coll-evals id_eval_col) "]"))
;;                :ind-eval/skill     (load-string (str "#db/id[:db.part/user " (m-comp id_competence) "]"))}))
;;        (clojure.string/join "\n")
;;        (spit "./samples/eval-ind.dtm")))

(p/pprint "individual evaluations query")
(-> '[:find ?iec ?pn ?pfn ?ieml ?iesn
      :where
      [?c :ind-eval/comment ?iec]
      [?c :ind-eval/pupil ?iep]
      [?iep :pupil/name ?pn]
      [?iep :pupil/firstname ?pfn]
      [?c :ind-eval/mark ?iem]
      [?iem :mark/label ?ieml]
      [?c :ind-eval/skill ?ies]
      [?ies :skill/name ?iesn]]
    (q (db conn))
    seq
    p/pprint)
