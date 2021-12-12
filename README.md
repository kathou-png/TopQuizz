# TopQuizz - Android course

## UI
- [x] Musique en boucle
- [ ] Animation question (lors de click...)
- [ ] Voix narratrice
- [x] Mode nuit/jour, thème application => Cathy

## Fonctionnalités
- [ ] Rajout de joker (3 indices) => Sami
- [x] Plusieurs modes de jeux (facile, normal, difficile) avec timer => ALEX \
  -> Timer qui décompte à partir d'un entier prédéfini. \
  -> Modification de la fonction endGame() pour prendre en compte le GameState (enum : win, lose, pause). \
  -> Appel de la fonction endGame() à la fin du décompte en précisant un état de perte de partie. \
  -> Ajout de choix de difficulté (attribut de user). \
  -> La difficulté impacte maintenant les points (+/- en fonction de la difficulté et points negatifs en mode hard) \
- [x] Partage du score sur les réseaux sociaux => Cathy
- [x] Rajout de questions par l'utilisateur => Cathy
- [x] 3 vies => Alex \
  -> 3 vies représentées par 3 coeurs rouges \
  -> À la perte d'une vie, appel d'une fonction qui va changer la couleur d'un coeur (Rouge > Gris) \
  -> Si la perte d'une vie est causée par un timer à 0, relance le timer après avoir enlevé une vie.

## Autres
- [ ] Alimentation de la base de données de questions
- [x] Rajout de langues (EN)
