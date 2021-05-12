## Content
* Feature files, java code are contained in src/test/java/weatherAPI
* weather_data.xlsx contain all the data sets, i.e. parameters, it is in src/test/resources
* result file from performance test is in src/test/resources/apache-jmeter-5.4.1/bin/output

## How to run
* right click on project -> maven -> update project
* install cucumber plugin
* right click on project -> configure -> convert to cucumber project
* go to feature file, right click -> run as -> run configuration -> set cucumber feature runner
  -> select project and set feature path -> Run

## Result
* scenario 1-7 check result from console or at the end of the console, there is a cucumber report link
* scenario 8, open Jmeter gui, by running src/test/resources/apache-jmeter-5.4.1/bin/jmeter.bat 
  / src/test/resources/apache-jmeter-5.4.1/bin/jmeter.sh, go to listener tab, and import reslut file from src/test/resources/apache-jmeter-5.4.1/bin/output
