#!/bin/bash

#cd xmart-frontend
#mvn javafx:run
java --module-path "C:\Users\zs\Desktop\JavaFx\JavaFx\javafx-sdk-23.0.2\lib" --add-modules javafx.controls,javafx.fxml -jar "C:\Users\zs\Downloads\Github\xmart-frontend\target\xmart-frontend-1.0-SNAPSHOT-jar-with-dependencies.jar"


if [ $? -eq 0 ]; then
    echo "Lancement effectué avec succès."
else
    echo "Erreur lors du lancement de l'application."
fi