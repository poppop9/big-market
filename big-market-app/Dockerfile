FROM openjdk:17-alpine

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD target/big-market-app-1.0.jar /big-market-app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "/big-market-app.jar"]