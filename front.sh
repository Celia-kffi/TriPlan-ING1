#!/bin/bash

cd xmart-frontend
mvn exec:java -Dexec.mainClass="edu.ezip.ing1.pds.MainFrontEnd"

if [ $? -eq 0 ]; then
    echo "Compilation went successfully."
else
    echo "Error compiling file."
fi