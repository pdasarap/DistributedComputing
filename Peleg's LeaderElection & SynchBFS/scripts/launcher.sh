#!/bin/bash

# Change this to your netid
netid=pxd210008

# Root directory of your project
PROJECT_DIR=/home/012/p/px/pxd210008/DC

# Directory where the config file is located on your local system
CONFIG_LOCAL=/home/012/p/px/pxd210008/DC/configGHS.txt

# Directory your java classes are in
BINARY_DIR=$PROJECT_DIR/bin

# Your main project class
PROGRAM=MyMain

n=0

cat $CONFIG_LOCAL | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i
    echo $i
    while [[ $n < $i ]]
    do
    	read line
    	p=$( echo $line | awk '{ print $1 }' )
        host=$( echo $line | awk '{ print $2 }' )
	
	ssh -o UserKnownHostsFile=/home/012/p/px/pxd210008/.ssh/known_hosts -o StrictHostKeyChecking=no $netid@$host java -cp $BINARY_DIR $PROGRAM $p; exec bash &

        n=$(( n + 1 ))
    done
)
