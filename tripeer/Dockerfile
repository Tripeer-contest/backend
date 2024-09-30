FROM openjdk:21

WORKDIR /app

ARG JAR_FILE=build/libs/tripeer-0.0.1-SNAPSHOT.jar
# ARG : 변수지정, 변수에 적혀진 의미는 빌드 결과물 위치

COPY ${JAR_FILE} test.jar
# Auto Exctraction으로 압축된 파일을 해제하여 test.jar에 그 내용을 복사, 결국 스프링부트를 실행하는 컨테이너 내에는 test.jar가 있게 됨

# 타임존 한국 시간으로 설정
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone

ENTRYPOINT ["java", "-jar","test.jar", "-DJASYPT_KEY=${JASYPT_KEY}"]
# ENTRYPOINT : 해당 컨테이너 시작할때 무조건 실행할 명령어 지정
