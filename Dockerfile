FROM library/openjdk:17.0.2-jdk AS builder
COPY .gradle-cache /root/.gradle
COPY . /root/src
RUN cd /root/src && ./gradlew installDist

FROM library/openjdk:17.0.2-jdk AS rates-service
EXPOSE 5000
COPY --from=builder /root/src/rates.json /opt/app/rates.json
COPY --from=builder /root/src/api/build/install/api /opt/app
CMD ["sh", "-c", "/opt/app/bin/api /opt/app/rates.json"]
