#!/bin/sh
#
# scotty     This shell script takes care of starting and stopping
#               the scotty gateway
#

# Source function library
. /etc/rc.d/init.d/functions


# Do preliminary checks here, if any
#### START of preliminary checks #########


##### END of preliminary checks #######


# Specify here the folder where soctty-gateway-jar-with-dependencies.jar is located.
SCOTTY_DIR=./scotty-gateway

case "$1" in
  start)
    # Start daemons.

    echo -n $"Starting scotty daemon: "
    echo
	$JAVA_HOME/bin/java -jar $SCOTTY_DIR/scotty-gateway-jar-with-dependencies.jar &
	export SCOTTY_PID=$!
    echo
    ;;

  stop)
    # Stop daemons.
    echo -n $"Shutting down scotty: "
    kill -9 $SCOTTY_PID
    echo
    ;;
  status)
    ps -p $SCOTTY_PID|tail -1

    ;;
  restart)
    $0 stop
    $0 start
    ;;

  *)
    echo $"Usage: $0 {start|stop|status|restart}"
    exit 1
esac

exit 0