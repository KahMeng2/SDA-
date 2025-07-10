# build stage
   FROM maven:3.9-amazoncorretto-17 AS build
   WORKDIR /app
   COPY . .
   RUN mvn clean package -DwarName=brogrammerbrigade-backend
   # RUN mv /app/target/api-1.0-SNAPSHOT.war /app/target/react-example-api.war

   # run stage
   FROM tomcat:10.1.26-jre17
   COPY --from=build /app/target/brogrammerbrigade-backend.war $CATALINA_HOME/webapps/
