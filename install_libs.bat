@echo off

echo Installing libraries...

call mvn install:install-file -Dpackaging=jar -Dversion=1.7.0-google-v5 -DgroupId=com.google -DartifactId=google-analytics-services -Dfile=lib/libGoogleAnalyticsServices.jar
call mvn install:install-file -Dpackaging=jar -Dversion=6.4.1 -DgroupId=com.google -DartifactId=google-admob-ads-sdk -Dfile=lib/GoogleAdMobAdsSdk-6.4.1.jar

PAUSE
