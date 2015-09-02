# Logtuc
A complete (though simple) demo application that consumes data (web logs) from a kafka cluster and uses count min 
sketch to estimate various metrics. Estimations are being sent to the rest API and then visualized in realtime.   
For visuallizations elastic search and kibana could be used as well. Just change the code that performs http requests 
to the rest api.