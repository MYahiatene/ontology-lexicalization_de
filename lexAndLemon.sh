#!/bin/sh
java -jar -Xmx16G target/rest-service-0.0.1-SNAPSHOT.jar >> error_log &
sleep 5
curl -H "Accept: application/json" -H "Content-type: application/json"  --data-binary @inputLex.json -X POST  http://localhost:8080/lexAndLemon  &

