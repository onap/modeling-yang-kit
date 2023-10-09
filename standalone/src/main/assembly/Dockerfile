FROM nexus3.onap.org:10001/onap/integration-java11:9.0.0

USER root

RUN mkdir /home/yangkit

ADD modeling-yang-kit-*-linux64.tar.gz /home/yangkit

WORKDIR /home/yangkit

ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar modeling-yang-compiler.jar"]
