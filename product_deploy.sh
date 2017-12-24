#!/bin/bash
rsync -avz  web/target/jrs.war  e-troin@192.168.0.160:/var/lib/tomcat8/webapps/jrs.war