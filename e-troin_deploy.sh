#!/bin/bash
rsync -av  "-e ssh -i ~/.ssh/prod"  web/target/jrs.war     ubuntu@www.e-troin.cn:/data/Tomcat8/webapps/jrs.war