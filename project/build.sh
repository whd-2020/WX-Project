#!/bin/sh
if [ "$1" = "build" ];then
    mkdir /home/changsheng/project/project31885/project
    cp -a /home/changsheng/project/project31885/server/. /home/changsheng/project/project31885/project/
    cd /home/changsheng/project/project31885/project
    rm -rf /home/changsheng/project/project31885/server
    echo "执行成功"
fi
