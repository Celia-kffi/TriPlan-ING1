#!/bin/bash


set -e  # Arrêter le script en cas d'erreur

#Compilation du projet
mvn clean package

#Déplacement vers le dossier du fichier généré
cd xmart-city-backend/target || { echo "Erreur : dossier introuvable"; exit 1; }

#Envoi du fichier JAR sur la VM
 scp -P 121 xmart-zity-backend-1.0-SNAPSHOT-jar-with-dependencies.jar backend@172.31.250.95:/home/backend/backend3.jar

#Vérification de l'envoi
if [ $? -eq 0 ]; then
    echo "Fichier envoyé avec succès !"
else
    echo "Erreur lors de l'envoi du fichier."
    exit 1
fi

#Connexion SSH et exécution du JAR dans la VM
ssh -p 121 backend@172.31.250.95 "java -jar /home/backend/backend3.jar"

#Message de confirmation
echo "Déploiement réussi : Le backend a été mis à jour et lancé avec succès!"

