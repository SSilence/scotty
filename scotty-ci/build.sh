#!/bin/sh
#                _   _         _     
#  ___  ___ ___ | |_| |_ _   _( )___ 
# / __|/ __/ _ \| __| __| | | |// __|
# \__ \ (_| (_) | |_| |_| |_| | \__ \
# |___/\___\___/ \__|\__|\__, | |___/
#                        |___/       
#  \ \ \ \ \ Continous Integration Bash Script
# 
# This script pulls, compiles, and puts the files on the configured ftp server.

PROJECT=/home/ci/ci/projects/scotty
RELEASES_FOLDER=/home/ci/ci/release/
RELEASES_FOLDER_ARCHIVE=/home/ci/ci/release-archive/`date +%s`
FILE_PATTERN=".*\.\(war\|jar\)"
PATTERN=".*/target/scotty$FILE_PATTERN"
MVN_SETTINGS=/home/ci/ci/settings.xml

REMOTE_USER=$REMOTE_USER
PASSWORD=$PASSWORD
HOSTNAME=$HOSTNAME
REMOTE_DIR=$REMOTE_DIR
LOGFILE=$SCOTTY_LOGFILE

# Profles to run:
PROFILES[0]=
PROFILES[1]="-P gae"
LOCKFILE=.scotty_ci_lockfile

echo ====\> `date`
stat $LOCKFILE &>/dev/null
LOCK=`echo $?`

# pull git repo
cd $PROJECT
GIT_OUTPUT=`git pull`
if [ "$LOCK" == "0" ] || [ "$GIT_OUTPUT" == "Already up-to-date."  ]; then
	echo $GIT_OUTPUT
	if [ "$LOCK" == "0" ]; then
		echo Lockile $LOCKFILE exists..build is currently running
	fi
else
	touch $LOCKFILE
	# make dirs, if not existing
	mkdir -p $RELEASES_FOLDER
	mkdir -p $RELEASES_FOLDER_ARCHIVE

	# Mave old Release to archive
	stat $RELEASES_FOLDER* &> /dev/null
	if [ "$?" == "0" ]; then
        	mv -f $RELEASES_FOLDER* $RELEASES_FOLDER_ARCHIVE/
	fi

	#run each profile and copy to RELEASES_FOLDER
	for p in "${PROFILES[@]}" 
	do
		echo ------------------------------ Profile $p ----------------------------------
		mvn -l $SCOTTY_LOGFILE -s $MVN_SETTINGS $p -U clean install
		for i in `find ./ -maxdepth 4 -regex $PATTERN`;do echo cp -f $i $RELEASES_FOLDER; cp $i $RELEASES_FOLDER;done
	done

	echo Uploading to FTP:
	for i in `find $RELEASES_FOLDER -regex $FILE_PATTERN`
	do
		ncftpput -T PART -u $REMOTE_USER -p $PASSWORD $HOSTNAME $REMOTE_DIR $i
	done
	rm $LOCKFILE
fi
