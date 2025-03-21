#!/bin/bash

cd xmart-frontend/target
java -jar "xmart-frontend-1.0-SNAPSHOT-jar-with-dependencies.jar"

if [ $? -eq 0 ]; then
    echo "lancement went successfully."
else
    echo "Error compiling file."
fi