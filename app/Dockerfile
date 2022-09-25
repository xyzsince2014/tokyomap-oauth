FROM amazoncorretto:8

LABEL maintainer="xyzsince2014 <xyzsince2014@gmail.com>"

RUN amazon-linux-extras install tomcat9
RUN yum update -y
RUN yum install -y tomcat-webapps tomcat-admin-webapps vim fileutils

ENV TZ='Asia/Tokyo'

RUN systemctl enable tomcat.service
RUN echo 'alias ls="ls --color"' >> ~/.bashrc

# todo: use /usr/local instead of /usr/share
COPY tomcat/conf/tomcat.conf /usr/share/tomcat/conf/tomcat.conf
COPY tomcat/conf/context.xml /usr/share/tomcat/conf/context.xml
COPY tomcat/conf/tomcat-users.xml /usr/share/tomcat/conf/tomcat-users.xml
COPY tomcat/conf/server.xml /usr/share/tomcat/conf/server.xml

EXPOSE 8080

CMD ["/sbin/init"]
