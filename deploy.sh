#!/bin/bash
rsync -avz web/target/jrs.war root@39.106.104.195:/var/lib/tomcat8/webapps/jrs.war
