#!/bin/bash

cd xmart-frontend

mvn javafx:run

if [ $? -eq 0 ]; then
    echo "Lancement effectué avec succès."
else
    echo "Erreur lors du lancement de l'application."
fi