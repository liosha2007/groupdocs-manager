groupdocs-manager
=================

Android application for GroupDocs

For build project use Maven v3.0.5 (or less) with JDK 6.

[Configuration for AndroidAnnotation](http://gdogaru.blogspot.com/2013/02/update-intellij-12-android-annotations.html) (mvn idea:idea not work currently)

Sign application
clean install -Prelease

Generate .keystore file for sign application (in cmd)
call %JAVA_HOME%/bin/keytool -genkey -validity 12345 -dname "CN=<ApplicationName>, O=Android, C=US" -keystore ./<KeystoreName>.keystore -alias <AliasString> -keyalg RSA -v -keysize 2048

Replace to value:
<ApplicationName>
<KeystoreName>
<AliasString>


Sign application (in cmd)
call %JAVA_HOME%/bin/jarsigner -sigalg SHA1withRSA -digestalg SHA1 -keystore ./<KeystoreName>.keystore -signedjar ./target/<SignedApkName> ./target/<UnsignedApkName_SourceApk> <AliasString>

Replace to value
<KeystoreName>
<SignedApkName>
<UnsignedApkName_SourceApk>
<AliasString>
