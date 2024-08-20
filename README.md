vidéo de démonstration
https://youtu.be/OMQpdHi_bPw

Pour lancer l'app il faut d'abord lancer le server dans UE2202_2324_Labo_Q3/src/main/java/server/PictionaryServer donc le main()

Et au minimum deux instance du client dans UE2202_2324_Labo_Q3/src/main/java/client/PictionaryClient donc le main()


++
Plusieurs pair peuvent être lancé.
Le jeux fonctionne normalement 
Le server pioche les mots aléatoirement dans le fichier mots.txt, il ne peut y avoir plusieurs mots similaires.
L'instance qui devine les dessins ne reçoit pas cette liste, elle demande juste vérification auprès de la seconde instance.

+-
L'instance drawer envoie les coordonnées du curseur des canvas (moins lourd que d'envoyé le canvas en entier)
Mais l'instance guesser recois des dessins où il manque des pixel

--
Pas de javadoc, quelques fonctions beaucoup trop longues, des commentaires et du code de test laissé
Messages d'erreur au niveau du server quand on kill un client, 
Fermer une fenêtre ne tue pas l'instance du client (il faut le kill dans intelij)
Une fenêtre en trop s'affiche à la fin de la partie
La communication entre les clients est lourd à comprendre 
Temps passés ~37-42h 
L'architecture
