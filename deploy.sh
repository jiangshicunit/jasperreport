#!/bin/bash
rsync -avz web/target/jrs.war root@39.106.104.195:/root/data/apache-tomcat-8.0.47/webapps/jrs.war
