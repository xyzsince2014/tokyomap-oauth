FROM amazoncorretto:8

LABEL maintainer="xyzsince2014 <xyzsince2014@gmail.com>"

RUN amazon-linux-extras install tomcat9
RUN yum update -y
RUN yum install -y tomcat-webapps tomcat-admin-webapps vim fileutils

RUN echo 'alias ls="ls --color"' >> ~/.bashrc

# todo: use /usr/local instead of /usr/share
COPY conf/tomcat.conf /usr/share/tomcat/conf/tomcat.conf
COPY conf/context.xml /usr/share/tomcat/conf/context.xml
COPY conf/tomcat-users.xml /usr/share/tomcat/conf/tomcat-users.xml
COPY conf/server.xml /usr/share/tomcat/conf/server.xml
# COPY target/ROOT.war /usr/local/tomcat/webapps/ROOT.war // for production env
RUN systemctl enable tomcat.service

EXPOSE 8080

CMD ["/sbin/init"]
