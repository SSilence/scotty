#!/bin/sh
PATTERN_GAE=".*/scotty-gateway-gae.*\.\(war\)"
PATTERN_PHP="scotty-php-gateway"

echo afterBuild Skript zip PHP and GAE gateway:
for i in `find /home/ci/ci/release -regex $PATTERN_GAE`;do mv $i `echo $i|sed -e ' s/\.war/\.zip/'`;done

VERSION=`cat $PROJECT/pom.xml |grep version|head -1|awk -F'<|>' '{print $3}'`
echo zipping PHP script $VERSION:
for i in `find $PROJECT -type d -name $PATTERN_PHP`;do zip -j "$RELEASES_FOLDER"scotty-php-gateway-$VERSION.zip $i/*;done
