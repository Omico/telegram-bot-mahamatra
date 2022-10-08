Telegram Bot Mahamatra
=================

## Usage

```shell
cp ./docker/docker-compose.example.yml ./docker-compose.yml
```

Modify the `docker-compose.yml`.

```shell
docker-compose up -d
```

Note: In Windows, you should create a empty file named `mahamatra.json` before run `docker-compose up`.
Otherwise, the docker will create a directory named `mahamatra.json`, instead of a file.
