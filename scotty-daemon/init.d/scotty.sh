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


# Handle manual control parameters like start, stop, status, restart, etc.
SCOTTY_DIR=../bin

case "$1" in
  start)
    # Start daemons.

    echo -n $"Starting scotty daemon: "
    echo
	$JAVA_HOME/bin/java -cp $SCOTTY_DIR/libs scotty.StandaloneGatewayMain &
    echo
    ;;

  stop)
    # Stop daemons.
    echo -n $"Shutting down scotty: "
    
    echo

    # Do clean-up works here like removing pid files from /var/run, etc.
    ;;
  status)
    

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