#!/bin/bash

# Build the project
mvn clean package

# Send the jar to the server
cd /d/propre/propre/xmart-city-backend/target

scp -P 121  xmart-zity-backend-1.0-SNAPSHOT-jar-with-dependencies.jar backend@172.31.250.95:backend2.jar

# put thepassword manually for the backend

if [ $? -eq 0 ]; then
    echo "File sent successfully."
else
    echo "Error sending file."
fi

# Check if the file was sent successfully and run the jar
if [ $? -eq 0 ]; then
    ssh -p 121 backend@172.31.250.95
    java -jar backend2.jar
else
    echo "Error running the jar."
fi


