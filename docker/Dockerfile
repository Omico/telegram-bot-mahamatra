FROM gradle:jdk18-alpine as builder

WORKDIR /mahamatra

COPY . .

RUN gradle clean assemble --no-daemon

FROM openjdk:18-slim-buster

WORKDIR /mahamatra

COPY --from=builder /mahamatra/build/distributions/MahamatraBot*.tar MahamatraBot.tar

RUN tar -xvf MahamatraBot.tar

CMD ["sh", "-c", "./MahamatraBot*/bin/MahamatraBot --token $BOT_TOKEN"]
