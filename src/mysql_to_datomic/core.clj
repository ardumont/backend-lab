(ns mysql-to-datomic.core
  "Play with korma"
  (:use [korma.db]
        [korma.core])
  (:require [clojure.string :as s]))

(defdb db (mysql {:db       "eleves"
                  :user     "eleves"
                  :password "eleves"
                  :naming {:keys   s/lower-case
                           :fields s/lower-case}}))

(declare professeurs classes)

(defentity professeur_classe
  (table :PROFESSEUR_CLASSE :professeur_classe)
  (belongs-to professeurs)
  (belongs-to classes))

(defentity professeurs
  (pk :PROFESSEUR_ID)
  (table :PROFESSEURS :professeurs)
  (has-many professeur_classe))

(comment
  (select professeur_classe
          (join professeurs (= :professeurs.PROFESSEUR_ID :professeur_classe.ID_PROFESSEUR)))

  (select professeurs
          (join professeur_classe (= :professeur_classe.ID_PROFESSEUR :professeurs.PROFESSEUR_ID))
          (join classes           (= :classes.CLASSE_ID :professeur_classe.ID_CLASSE))))

(defentity classes
  (pk :CLASSE_ID)
  (table :CLASSES :classes)
  (belongs-to professeur_classe))

(comment
  (select classes
          (with professeur_classe)))

(defentity ecoles                    (table :ECOLES :ecoles))
(defentity commentaires              (table :COMMENTAIRES :commentaires))
(defentity comm_conseil_maitres      (table :COMM_CONSEIL_MAITRES :comm_conseil_maitres))
(defentity competences               (table :COMPETENCES :competences))
(defentity cycles                    (table :CYCLES :cycles))
(defentity domaines                  (table :DOMAINES :domaines))
(defentity eleves                    (table :ELEVES :eleves))
(defentity eleve_classe              (table :ELEVE_CLASSE :eleve_classe))
(defentity evaluations_collectives   (table :EVALUATIONS_COLLECTIVES :evaluations_collectives))
(defentity evaluations_individuelles (table :EVALUATIONS_INDIVIDUELLES :evaluations_individuelles))
(defentity hidden_objects            (table :HIDDEN_OBJECTS :hidden_objects))
(defentity matieres                  (table :MATIERES :matieres))
(defentity niveaux                   (table :NIVEAUX :niveaux))
(defentity niveau_classe             (table :NIVEAU_CLASSE :niveau_classe))
(defentity notes                     (table :NOTES :notes))
(defentity periodes                  (table :PERIODES :periodes))
(defentity profils                   (table :PROFILS :profils))
(defentity profils_rel_rights        (table :PROFILS_REL_RIGHTS :profils_rel_rights))
(defentity cycles                    (table :CYCLES :cycles))

;; parameters for the installation of the db
(defentity parametres                (table :PARAMETRES :parametres))

(comment
  (select parametres)
  (select profils)
  (select cycles)
  (select professeurs)
  (select classes)
  (select ecoles)
  (select commentaires)
  (select competences))
