#!/bin/bash

##### FUNCTIONS ######

function quit {
    echo -e "Possible args:"
    echo -e "\tpull - pull mysql:latest image"
    echo -e "\tbuild - build project and platform container"
    echo -e "\tstart - pull images, build platform and run all containers"
    echo -e "\trun - run platform container"
    echo -e "\trun mysql - run mysql container"
    echo -e "\trerun - stop, build and run platform"
    echo -e "\tstop - stop and rm platform container"
    echo -e "\tstop all - stop and rm all containers"
    echo -e "Recommended to use start the first time and rerun when code is changed"
    exit
}

function pull {
    docker pull mysql:latest
}

function run_mysql {
    docker run --name mysql-socatel -e MYSQL_ROOT_PASSWORD={your_root_password} -e MYSQL_DATABASE={your_database} -e MYSQL_USER={your_user} -e MYSQL_PASSWORD={your_password} -d mysql:latest
}

function build {
    mvn clean package && docker build . -t socatel-platform
}

function run_platform {
    docker run -p 8080:8080 --name socatel-platform --link mysql-socatel:mysql -d socatel-platform
}

function rerun {
    stop_platform
    build && run_platform
}

function start {
    pull
    run_mysql
    build
    run_platform
}

function stop_platform {
    docker stop socatel-platform
    docker rm socatel-platform
}

function stop_mysql {
    docker stop mysql-socatel
    docker rm mysql-socatel
}

function stop_all {
    stop_platform
    stop_mysql
}

##### MAIN SCRIPT #####

[[ $# -eq 0 ]] && quit

if [[ $# -eq 1 ]]; then
    ([[ "$1" = "pull" ]] && pull) || ([[ "$1" = "start" ]] && start) || ([[ "$1" = "build" ]] && build) || ([[ "$1" = "run" ]] && run_platform) || ([[ "$1" = "stop" ]] && stop_platform) || ([[ "$1" = "rerun" ]] && rerun) || quit
else
    ([[ "$1" = "run" ]] && [[ "$2" = "mysql" ]] && run_mysql) || ([[ "$1" = "stop" ]] && [[ "$2" = "all" ]] && stop_all) || quit
fi
