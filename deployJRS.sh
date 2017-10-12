rsync -avz -e "ssh -p12322"  ./web/target/jrs.war haomo@haomo-studio.com:/opt/tomcat8/webapps/jrs.war
rsync -avz  web/target/etroinauction.war  ubuntu@www.e-troin.cn:/data/Tomcat8/webapps/eoa.war