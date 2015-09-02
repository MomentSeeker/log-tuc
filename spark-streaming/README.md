# Spark streaming engine
A spark streaming app that consumes data (web logs) from kafka and estimates metrics using count min sketch structure. 
Estimations are being sent directly to the Rest-API as soon as each batch has been calculated.