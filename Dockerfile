FROM openjdk:17-oracle
WORKDIR /app
COPY  /target/backend-0.0.1-SNAPSHOT.jar  app.jar 

# spell-checker: enable
COPY  Wallet_K0A996WRTNFO1GE4 /app/oracle_wallet
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]