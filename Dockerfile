FROM maven:3.9-eclipse-temurin-21 AS build

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

WORKDIR /app

RUN mkdir -p /root/.m2

RUN printf '<settings>\
<servers>\
<server>\
<id>github-common</id>\
<username>%s</username>\
<password>%s</password>\
</server>\
<server>\
<id>github-security</id>\
<username>%s</username>\
<password>%s</password>\
</server>\
</servers>\
</settings>' \
"$GITHUB_USERNAME" "$GITHUB_TOKEN" \
"$GITHUB_USERNAME" "$GITHUB_TOKEN" \
> /root/.m2/settings.xml

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]