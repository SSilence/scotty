#!/bin/sh

GIT_OUTPUT=`git pull`
GIT_EXIT=$?
if [ "$?" != "0"  ]; then
        echo Something is wrong, git pull exit code is $?
        exit 3
else
        if [ "$LOCK" == "0" ] || [ "$GIT_OUTPUT" == "Already up-to-date."  ]; then
                echo $GIT_OUTPUT
                exit 0
        else
                echo $GIT_OUTPUT
                exit 2
        fi
fi

