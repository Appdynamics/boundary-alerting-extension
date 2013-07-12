@echo off
rem ------------------------------------------------------------
rem Batch file to send policy violation  notifications to Splunk
rem ------------------------------------------------------------
java -Dlog4j.configuration=file:..\..\conf/log4j.xml -DBOUNDARY_CLIENT_HOME=../.. -jar ..\..\lib\boundaryClient.jar PolicyViolation %*
