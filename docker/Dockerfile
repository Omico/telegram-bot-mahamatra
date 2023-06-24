FROM gradle:jdk17-alpine as builder

WORKDIR /builder

COPY . .

RUN gradle clean assemble --no-daemon

FROM openjdk:17-slim-buster

WORKDIR /bot

COPY --from=builder /builder/build/distributions/MahamatraBot*.tar MahamatraBot.tar

RUN tar -xvf MahamatraBot.tar --directory=. --strip-components=1

CMD ["sh", "-c", "./bin/MahamatraBot --token $BOT_TOKEN"]
