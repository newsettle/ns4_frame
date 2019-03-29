#!/bin/bash  -vx
#author hengzhou

#jvm启动参数
JVM_OPTS="-Dconfigfile=nscontroller.xml  -Dfile.encoding=utf-8  -Duser.timezone=GMT+8 -server -Xms1024m -Xmx1024m  -XX:MaxPermSize=128m  -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=5 -XX:+PrintGC -XX:+PrintGCTimeStamps  -XX:+PrintGCDetails  -XX:+PrintGCApplicationStoppedTime -XX:+HeapDumpOnOutOfMemoryError "
CLASS_PATH=""
OPTIONS=" -Dfile.encoding=utf-8"
cd ..
echo Starting .........

export LANG="en_US.UTF-8" 
for i in $PWD/lib/*;
    do CLASS_PATH=$i:"$CLASS_PATH";
done
CLASS_PATH=$PWD/config/:$CLASS_PATH
export CLASS_PATH=.:$CLASS_PATH
echo CLASS_PATH=.:$CLASS_PATH


nohup java $JVM_OPTS -classpath $CLASS_PATH  com.creditease.ns.dispatcher.core.Bootstrap >$PWD/log/stdout.log 2>&1 &