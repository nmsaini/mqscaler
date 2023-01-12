FROM docker.io/adoptopenjdk/openjdk8 AS build
RUN echo $(id)
COPY --chown=1001:0 . /app
USER 1001
WORKDIR /app/src
ENV CLASSPATH /app/lib/com.ibm.mq.allclient.jar:/app/lib/json-20220924.jar:.
RUN javac com/ibm/utils/mq/autoscaler/*.java
RUN jar cvf mqscaler-1.0.jar com/*

FROM docker.io/adoptopenjdk/openjdk8
USER 1001
COPY --from=build /app/src/mqscaler-1.0.jar /app/
COPY --from=build /app/lib/*.jar /app/
COPY --from=build /app/mqscaler.properties /app/
WORKDIR /app
RUN ls -ltr /app/
ENV MQSCALER_PROPSFILE mqscaler.properties
ENV CLASSPATH /app/mqscaler-1.0.jar:/app/com.ibm.mq.allclient.jar:/app/json-20220924.jar:.
EXPOSE 8888
CMD java com.ibm.utils.mq.autoscaler.MetricsServer
