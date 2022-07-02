#!/bin/sh
java -jar target/rest-service-0.0.1-SNAPSHOT.jar > log_now &
sleep 10 &
curl -H "Accept: application/json" -H "Content-type: application/json"  --data-binary @inputLemon.json -X POST  http://localhost:8080/createLemon > results_json/results_person_new_2.json &
