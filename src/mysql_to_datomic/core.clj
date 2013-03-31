(ns mysql-to-datomic.core
  "Play with korma"
  (:use [korma.db]
        [korma.core])
  (:require [clojure.string :as s]))

(defdb db (mysql {:db       "eleves"
                  :user     "eleves"
                  :password "eleves"
                  :naming {:keys   s/lower-case
                           :fields s/upper-case}}))

(declare professeurs classes)

(defentity professeur_classe
  (table :PROFESSEUR_CLASSE :professeur_classe)
  (belongs-to professeurs {:fk :ID_PROFESSEUR})
  (belongs-to classes {:fk :ID_CLASSE}))

(defentity professeurs
  (pk :PROFESSEUR_ID)
  (table :PROFESSEURS :professeurs)
  (has-many professeur_classe {:fk :ID_PROFESSEUR}))

(comment
  (select professeurs)
  [{:professeur_pwd "21232f297a57a5a743894a0e4a801fc3", :professeur_nom "administrateur", :professeur_profil_id 1, :professeur_id 1}
   {:professeur_pwd "4584fb39733c2deae3340f0e30a9d629", :professeur_nom "Mme Heritier", :professeur_profil_id 1, :professeur_id 2}]

  (select professeur_classe)
  [{:id_professeur 2, :id_classe 1}
   {:id_professeur 2, :id_classe 2}]

  (select professeur_classe
          (with classes)
          (with professeurs))

  [{:classe_id 1, :professeur_nom "Mme Heritier", :id_classe 1, :classe_nom "ce1a", :professeur_id 2, :professeur_pwd "4584fb39733c2deae3340f0e30a9d629", :professeur_profil_id 1, :id_professeur 2, :classe_annee_scolaire "2011-2012", :id_ecole 1}
   {:classe_id 2, :professeur_nom "Mme Heritier", :id_classe 2, :classe_nom "CE1A", :professeur_id 2, :professeur_pwd "4584fb39733c2deae3340f0e30a9d629", :professeur_profil_id 1, :id_professeur 2, :classe_annee_scolaire "2012-2013", :id_ecole 1}]

  (select professeurs
          (with professeur_classe))

  ({:professeur_classe [], :professeur_pwd "21232f297a57a5a743894a0e4a801fc3", :professeur_nom "administrateur", :professeur_profil_id 1, :professeur_id 1}
   {:professeur_classe [], :professeur_pwd "4584fb39733c2deae3340f0e30a9d629", :professeur_nom "Mme Heritier", :professeur_profil_id 1, :professeur_id 2})

  (select professeurs
          (join professeur_classe)
          (join classes))
  [{:professeur_pwd "21232f297a57a5a743894a0e4a801fc3", :professeur_nom "administrateur", :professeur_profil_id 1, :professeur_id 1}
   {:professeur_pwd "4584fb39733c2deae3340f0e30a9d629", :professeur_nom "Mme Heritier", :professeur_profil_id 1, :professeur_id 2}
   {:professeur_pwd "4584fb39733c2deae3340f0e30a9d629", :professeur_nom "Mme Heritier", :professeur_profil_id 1, :professeur_id 2}])

(defentity classes
  (pk :CLASSE_ID)
  (table :CLASSES :classes)
  (has-many professeurs))

(comment

  (select classes)
  [{:id_ecole 1, :classe_annee_scolaire "2011-2012", :classe_nom "ce1a", :classe_id 1}
   {:id_ecole 1, :classe_annee_scolaire "2012-2013", :classe_nom "CE1A", :classe_id 2}]

  (select classes
          (with professeur_classe))
  ({:professeur_classe [], :id_ecole 1, :classe_annee_scolaire "2011-2012", :classe_nom "ce1a", :classe_id 1}
   {:professeur_classe [], :id_ecole 1, :classe_annee_scolaire "2012-2013", :classe_nom "CE1A", :classe_id 2})

  (select classes
          (join professeur_classe (= :professeur_classe.ID_CLASSE :classes.CLASSE_ID))
          (join professeurs       (= :professeur_classe.ID_PROFESSEUR :professeurs.PROFESSEUR_ID)))

  [{:id_ecole 1, :classe_annee_scolaire "2011-2012", :classe_nom "ce1a", :classe_id 1}
   {:id_ecole 1, :classe_annee_scolaire "2012-2013", :classe_nom "CE1A", :classe_id 2}])

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
  [{:date_version #inst "2011-11-27T16:46:46.000000000-00:00", :version "2.2.0", :first_install_version "2.2.0", :install_uid "none"}]

  (select profils)
  [{:profil_comment "Profil de l'administrateur général de l'application", :profil_name "Administrateur", :profil_id 1}
   {:profil_comment "Professeur utilisateur de l'application", :profil_name "Professeur", :profil_id 2}
   {:profil_comment "Directeur ou directrice d'une école", :profil_name "Directeur/Directrice", :profil_id 3}]

  (select cycles)
  [{:cycle_nom "I", :cycle_id 1}
   {:cycle_nom "II", :cycle_id 2}
   {:cycle_nom "III", :cycle_id 3}]

  (select ecoles)
  [{:ecole_departement "93150", :ecole_ville "Blanc-Mesnil", :ecole_nom "Edouard Vaillant", :ecole_id 1}]

  (select commentaires)
  [{:id_classe 1, :id_eleve 3, :id_periode 5, :commentaire_valeur "Cyril a fait de gros progrès tout au long de l'année \net finit son CE1 avec d'excellents résultats. \nJe compte sur lui pour maintenir ses efforts \nl'an prochain et ne pas se laisser perturber \npar ses camarades.\nPassage en CE2.", :commentaire_id 1}
   {:id_classe 2, :id_eleve 42, :id_periode 1, :commentaire_valeur "", :commentaire_id 2}]

  (select competences)
  [{:id_matiere 1, :competence_nom "Etre capable de répondre aux sollicitations de l'adulte en se faisant comprendre dès la fin de la première année de scolarité (à trois ou quatre ans).", :competence_id 1}
   {:id_matiere 1, :competence_nom "Etre capable de prendre l'initiative d'un échange et le conduire au-delà de la première réponse.", :competence_id 2}
   {:id_matiere 1, :competence_nom "Etre capable de participer à un échange collectif en acceptant d'écouter autrui, en attendant son tour de parole et en restant dans le propos de l'échange.", :competence_id 3}
   {:id_matiere 2, :competence_nom "Etre capable de comprendre les consignes ordinaires de la classe.", :competence_id 4}
   {:id_matiere 2, :competence_nom "Etre capable de dire ce que l'on fait ou ce que fait un camarade (dans une activité, un atelier...).", :competence_id 5}
   {:id_matiere 2, :competence_nom "Etre capable de prêter sa voix à une marionnette.", :competence_id 6}
   {:id_matiere 3, :competence_nom "Etre capable de rappeler en se faisant comprendre un événement qui a été vécu collectivement (sortie, activité scolaire, incident...).", :competence_id 7}
   {:id_matiere 3, :competence_nom "Etre capable de comprendre une histoire adaptée à son âge et le manifester en reformulant dans ses propres mots la trame narrative de l'histoire.", :competence_id 8}
   {:id_matiere 3, :competence_nom "Etre capable d'identifier les personnages d'une histoire, les caractériser physiquement et moralement, les dessiner.", :competence_id 9}
   {:id_matiere 3, :competence_nom "Etre capable de raconter un conte déjà connu en s'appuyant sur la succession des illustrations.", :competence_id 10}
   {:id_matiere 3, :competence_nom "Etre capable d'inventer une courte histoire dans laquelle les acteurs seront correctement posés, où il y aura au moins un événement et une clôture.", :competence_id 11}
   {:id_matiere 3, :competence_nom "Etre capable de dire ou chanter chaque année au moins une dizaine de comptines ou de jeux de doigts et au moins une dizaine de chansons et de poésies.", :competence_id 12}
   {:id_matiere 4, :competence_nom "Etre capable de savoir à quoi servent un panneau urbain, une affiche, un journal, un livre, un cahier, un écran d'ordinateur... (c'est-à-dire donner des exemples de textes pouvant être trouvés sur l'u", :competence_id 13}
   {:id_matiere 5, :competence_nom "Etre capable de dicter individuellement un texte à un adulte en contrôlant la vitesse du débit et en demandant des rappels pour modifier ses énoncés.", :competence_id 14}
   {:id_matiere 5, :competence_nom "Etre capable de dans une dictée collective à l'adulte, restaurer la structure syntaxique d'une phrase non grammaticale, proposer une amélioration de la cohésion du texte (pronominalisation, connexion ", :competence_id 15}
   {:id_matiere 5, :competence_nom "Etre capable de reformuler dans ses propres mots un passage lu par l'enseignant.", :competence_id 16}
   {:id_matiere 5, :competence_nom "Etre capable d'évoquer, à propos de quelques grandes expériences humaines, un texte lu ou raconté par le maître.", :competence_id 17}
   {:id_matiere 5, :competence_nom "Etre capable de raconter brièvement l'histoire de quelques personnages de fiction rencontrés dans les albums ou dans les contes découverts en classe.", :competence_id 18}
   {:id_matiere 6, :competence_nom "Etre capable de rythmer un texte en en scandant les syllabes orales.", :competence_id 19}
   {:id_matiere 6, :competence_nom "Etre capable de reconnaître une même syllabe dans plusieurs énoncés (en fin d'énoncé, en début d'énoncé, en milieu d'énoncé).", :competence_id 20}
   {:id_matiere 6, :competence_nom "Etre capable de produire des assonances ou des rimes.", :competence_id 21}
   {:id_matiere 7, :competence_nom "Etre capable d'écrire son prénom en capitales d'imprimerie et en lettres cursives.", :competence_id 22}
   {:id_matiere 7, :competence_nom "Etre capable de copier des mots en capitales d'imprimerie, en cursive avec ou sans l'aide de l'enseignant.", :competence_id 23}
   {:id_matiere 7, :competence_nom "Etre capable de reproduire un motif graphique simple en expliquant sa façon de procéder.", :competence_id 24}
   {:id_matiere 7, :competence_nom "Etre capable de représenter un objet, un personnage, réels ou fictifs.", :competence_id 25}
   {:id_matiere 7, :competence_nom "Etre capable d'en fin d'école maternelle, copier une ligne de texte en écriture cursive en ayant une tenue correcte de l'instrument, en plaçant sa feuille dans l'axe du bras et en respectant le sens d", :competence_id 26}
   {:id_matiere 8, :competence_nom "Etre capable de dès la fin de la première année passée à l'école maternelle (à trois ou quatre ans), reconnaître son prénom écrit en capitales d'imprimerie.", :competence_id 27}
   {:id_matiere 8, :competence_nom "Etre capable de pouvoir dire où sont les mots successifs d'une phrase écrite après lecture par l'adulte.", :competence_id 28}
   {:id_matiere 8, :competence_nom "Etre capable de connaître le nom des lettres de l'alphabet.", :competence_id 29}
   {:id_matiere 8, :competence_nom "Etre capable de proposer une écriture alphabétique pour un mot simple en empruntant des fragments de mots au répertoire des mots affichés dans la classe.", :competence_id 30}
   {:id_matiere 9, :competence_nom "Etre capable de jouer son rôle dans une activité en adoptant un comportement individuel qui tient compte des apports et des contraintes de la vie collective.", :competence_id 31}
   {:id_matiere 9, :competence_nom "Etre capable d'identifier et connaître les fonctions et le rôle des différents  adultes de l'école.", :competence_id 32}
   {:id_matiere 9, :competence_nom "Etre capable de respecter les règles de la vie commune (respect de l'autre, du matériel, des règles de la politesse...) et appliquer dans son comportement vis-à-vis de ses camarades quelques principes", :competence_id 33}
   {:id_matiere 10, :competence_nom "Etre capable de courir, sauter, lancer de différentes façons (par exemple : courir vite, sauter loin avec ou sans élan).", :competence_id 34}
   {:id_matiere 10, :competence_nom "Etre capable de courir, sauter, lancer dans des espaces et avec des matériels", :competence_id 35}
   {:id_matiere 11, :competence_nom "Etre capable de se déplacer dans des formes d'actions inhabituelles remettant en cause l'équilibre (sauter, grimper, rouler, se balancer, se déplacer à quatre pattes, se renverser...).", :competence_id 36}
   {:id_matiere 11, :competence_nom "Etre capable de se déplacer (marcher, courir) dans des environnements proches, puis progressivement dans des environnements étrangers et incertains (cour, parc public, petit bois...).", :competence_id 37}
   {:id_matiere 11, :competence_nom "Etre capable de se déplacer avec ou sur des engins présentant un caractère d'instabilité (tricycles, trottinettes, vélos, rollers...).", :competence_id 38}
   {:id_matiere 11, :competence_nom "Etre capable de se déplacer dans ou sur des milieux instables (eau, neige, glace, sable...).", :competence_id 39}
   {:id_matiere 12, :competence_nom "Etre capable de s'opposer individuellement à un adversaire dans un jeu de lutte : tirer, pousser, saisir, tomber avec, immobiliser....", :competence_id 40}
   {:id_matiere 12, :competence_nom "Etre capable de coopérer avec des partenaires et s'opposer collectivement à un ou plusieurs adversaires dans un jeu collectif : transporter, lancer (des objets, des balles), courir pour attraper, pour", :competence_id 41}
   {:id_matiere 13, :competence_nom "Etre capable d'exprimer corporellement des images, des personnages, des sentiments, des états.", :competence_id 42}
   {:id_matiere 13, :competence_nom "Etre capable de communiquer aux autres des sentiments ou des émotions.", :competence_id 43}
   {:id_matiere 13, :competence_nom "Etre capable de s'exprimer de façon libre ou en suivant un rythme simple, musical ou non, avec ou sans matériel.", :competence_id 44}
   {:id_matiere 14, :competence_nom "Etre capable de décrire, comparer et classer des perceptions élémentaires (tactiles, gustatives, olfactives, auditives et visuelles).", :competence_id 45}
   {:id_matiere 14, :competence_nom "Etre capable d'associer à des perceptions déterminées les organes des sens qui correspondent.", :competence_id 46}
   {:id_matiere 15, :competence_nom "Etre capable de reconnaître, classer, sérier, désigner des matières, des objets, leurs qualités et leurs usages.", :competence_id 47}
   {:id_matiere 15, :competence_nom "Etre capable d'utiliser des appareils alimentés par des piles (lampe de poche, jouets, magnétophone...).", :competence_id 48}
   {:id_matiere 15, :competence_nom "Etre capable d'utiliser des objets programmables.", :competence_id 49}
   {:id_matiere 15, :competence_nom "Etre capable de choisir des outils et des matériaux adaptés à une situation, à des actions techniques spécifiques (plier, couper, coller, assembler, actionner...).", :competence_id 50}
   {:id_matiere 15, :competence_nom "Etre capable de réaliser des jeux de construction simples, construire des maquettes simples.", :competence_id 51}
   {:id_matiere 15, :competence_nom "Etre capable d'utiliser des procédés empiriques pour faire fonctionner des mécanismes simples.", :competence_id 52}
   {:id_matiere 16, :competence_nom "Etre capable de retrouver l'ordre des étapes du développement d'un animal ou d'un végétal.", :competence_id 53}
   {:id_matiere 16, :competence_nom "Etre capable de reconstituer l'image du corps humain, d'un animal ou d'un végétal à partir d'éléments séparés.", :competence_id 54}
   {:id_matiere 16, :competence_nom "Etre capable de reconnaître des manifestations de la vie animale et végétale, les relier à de grandes fonctions : croissance, nutrition, locomotion, reproduction.", :competence_id 55}
   {:id_matiere 16, :competence_nom "Etre capable de repérer quelques caractéristiques des milieux.", :competence_id 56}
   {:id_matiere 16, :competence_nom "Etre capable de connaître et appliquer quelques règles d'hygiène du corps (lavage des mains...), des locaux (rangement, propreté), de l'alimentation (régularité des repas, composition des menus).", :competence_id 57}
   {:id_matiere 16, :competence_nom "Etre capable de prendre en compte les risques de la rue (piétons et véhicules) ainsi que ceux de l'environnement familier proche (objets et comportements dangereux, produits toxiques) ou plus lointain", :competence_id 58}
   {:id_matiere 16, :competence_nom "Etre capable de repérer une situation inhabituelle ou de danger, demander de l'aide, pour être secouru ou porter secours.", :competence_id 59}
   {:id_matiere 17, :competence_nom "Etre capable de repérer des objets ou des déplacements dans l'espace par rapport à soi.", :competence_id 60}
   {:id_matiere 17, :competence_nom "Etre capable de décrire des positions relatives ou des déplacements à l'aide d'indicateurs spatiaux et en se référant à des repères stables variés.", :competence_id 61}
   {:id_matiere 17, :competence_nom "Etre capable de décrire et représenter simplement l'environnement proche (classe, école, quartier...).", :competence_id 62}
   {:id_matiere 17, :competence_nom "Etre capable de décrire des espaces moins familiers (espace vert, terrain vague, forêt, étang, haie, parc animalier).", :competence_id 63}
   {:id_matiere 17, :competence_nom "Etre capable de suivre un parcours décrit oralement (pas à pas), décrire ou représenter un parcours simple.", :competence_id 64}
   {:id_matiere 17, :competence_nom "Etre capable de savoir reproduire l'organisation dans l'espace d'un ensemble limité d'objets (en les manipulant, en les représentant).", :competence_id 65}
   {:id_matiere 17, :competence_nom "Etre capable de s'intéresser à des espaces inconnus découverts par des documentaires.", :competence_id 66}
   {:id_matiere 18, :competence_nom "Etre capable de reconnaître le caractère cyclique de certains phénomènes, utiliser des repères relatifs aux rythmes de la journée, de la semaine et de l'année, situer des événements les uns par rappor", :competence_id 67}
   {:id_matiere 18, :competence_nom "Etre capable de pouvoir exprimer et comprendre les oppositions entre présent et passé, présent et futur en utilisant correctement les marques temporelles et chronologiques.", :competence_id 68}
   {:id_matiere 18, :competence_nom "Etre capable de comparer des événements en fonction de leur durée.", :competence_id 69}
   {:id_matiere 18, :competence_nom "Etre capable d'exprimer et comprendre, dans le rappel d'un événement ou dans un récit, la situation temporelle de chaque événement par rapport à l'origine posée, leurs situations relatives (simultanéi", :competence_id 70}
   {:id_matiere 19, :competence_nom "Etre capable de différencier et classer des objets en fonction de caractéristiques liées à leur forme.", :competence_id 71}
   {:id_matiere 19, :competence_nom "Etre capable de reconnaître, classer et nommer des formes simples : carré, triangle, rond.", :competence_id 72}
   {:id_matiere 19, :competence_nom "Etre capable de reproduire un assemblage d'objets de formes simples à partir d'un modèle (puzzle, pavage, assemblage de solides).", :competence_id 73}
   {:id_matiere 19, :competence_nom "Etre capable de comparer, classer et ranger des objets selon leur taille, leur masse ou leur contenance.", :competence_id 74}
   {:id_matiere 20, :competence_nom "Etre capable de comparer des quantités en utilisant des procédures non numériques ou numériques.", :competence_id 75}
   {:id_matiere 20, :competence_nom "Etre capable de réaliser une collection qui comporte la même quantité d'objets qu'une autre collection (visible ou non, proche ou éloignée) en utilisant des procédures non numériques ou numériques, or", :competence_id 76}
   {:id_matiere 20, :competence_nom "Etre capable de résoudre des problèmes portant sur les quantités (augmentation, diminution, réunion, distribution, partage) en utilisant les nombres connus, sans recourir aux opérations usuelles.", :competence_id 77}
   {:id_matiere 20, :competence_nom "Etre capable de reconnaître globalement et exprimer de très petites quantités (d'un à trois ou quatre).", :competence_id 78}
   {:id_matiere 20, :competence_nom "Etre capable de reconnaître globalement et exprimer des petites quantités organisées en configurations connues (doigts de la main, constellations du dé).", :competence_id 79}
   {:id_matiere 20, :competence_nom "Etre capable de connaître la comptine numérique orale au moins jusqu'à trente.", :competence_id 80}
   {:id_matiere 20, :competence_nom "Etre capable de dénombrer une quantité en utilisant la suite orale des nombres connus.", :competence_id 81}
   {:id_matiere 20, :competence_nom "Etre capable d'associer le nom des nombres connus avec leur écriture chiffrée en se référant à une bande numérique.", :competence_id 82}
   {:id_matiere 21, :competence_nom "Etre capable d'adapter son geste aux contraintes matérielles (outils, supports, matières).", :competence_id 83}
   {:id_matiere 21, :competence_nom "Etre capable de surmonter une difficulté rencontrée.", :competence_id 84}
   {:id_matiere 21, :competence_nom "Etre capable de tirer parti des ressources expressives d'un procédé et d'un matériau donnés.", :competence_id 85}
   {:id_matiere 21, :competence_nom "Etre capable d'exercer des choix parmi des procédés et des matériaux déjà expérimentés.", :competence_id 86}
   {:id_matiere 21, :competence_nom "Etre capable d'utiliser le dessin comme moyen d'expression et de représentation.", :competence_id 87}
   {:id_matiere 21, :competence_nom "Etre capable de réaliser une composition en plan ou en volume selon un désir d'expression.", :competence_id 88}
   {:id_matiere 21, :competence_nom "Etre capable de reconnaître des images d'origines et de natures différentes.", :competence_id 89}
   {:id_matiere 21, :competence_nom "Etre capable d'identifier les principaux constituants d'un objet plastique (image, oeuvre d'art, production d'élève...).", :competence_id 90}
   {:id_matiere 21, :competence_nom "Etre capable d'établir des rapprochements entre deux objets plastiques (une production d'élève et une reproduction d'oeuvre par exemple) sur leplan de la forme, de la couleur, du sens ou du procédé de", :competence_id 91}
   {:id_matiere 21, :competence_nom "Etre capable de dire ce qu'on fait, ce qu'on voit, ce qu'on ressent, ce qu'on pense.", :competence_id 92}
   {:id_matiere 21, :competence_nom "Etre capable d'agir en coopération dans une situation de production collective.", :competence_id 93}
   {:id_matiere 22, :competence_nom "Etre capable d'avoir mémorisé un répertoire varié de comptines et de chansons.", :competence_id 94}
   {:id_matiere 22, :competence_nom "Etre capable d'interpréter avec des variantes expressives un chant, une comptine, en petit groupe.", :competence_id 95}
   {:id_matiere 22, :competence_nom "Etre capable de jouer de sa voix pour explorer des variantes de timbre, d'intensité, de hauteur, de nuance.", :competence_id 96}
   {:id_matiere 22, :competence_nom "Etre capable de marquer la pulsation corporellement ou à l'aide d'un objet sonore, jouer sur le tempo en situation d'imitation.", :competence_id 97}
   {:id_matiere 22, :competence_nom "Etre capable de repérer et reproduire des formules rythmiques simples corporellement ou avec des instruments.", :competence_id 98}
   {:id_matiere 22, :competence_nom "Etre capable de coordonner un texte parlé ou chanté et un accompagnement corporel ou instrumental.", :competence_id 99}
   {:id_matiere 22, :competence_nom "Etre capable de tenir sa place dans des activités collectives et intervenir très brièvement en soliste.", :competence_id 100}
   ...]

  (exec-raw ["SELECT * FROM PROFESSEURS"] :results)
  (exec-raw ["SELECT * FROM PARAMETRES"] :results))
