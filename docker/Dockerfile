FROM gradle:jdk18-alpine as builder

WORKDIR /builder

COPY . .

RUN gradle clean assemble --no-daemon

FROM openjdk:18-slim-buster

WORKDIR /bot

COPY --from=builder /builder/build/distributions/MahamatraBot*.tar MahamatraBot.tar

RUN tar -xvf MahamatraBot.tar --directory=. --strip-components=1

CMD ["sh", "-c", "./bin/MahamatraBot --token $BOT_TOKEN"]
