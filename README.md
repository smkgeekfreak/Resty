New Play application
=================================
Getting play environment up and running a new system with a simple sample RESTful API application. Uses Redis/Jedis as a backend data store.
Redis is installed and run via Docker/Docker Compose.

>##Installation

1. Java 1.8 jdk
2. [Play Framework via Typesafe Activator](http://typesafe.com/get-started) v2.3.8, uses Scala 2.11.1
3. [Docker](https://docs.docker.com/installation/#installation) ([boot2docker](https://docs.docker.com/installation/mac/#install-boot2docker) for OSX)
4. [Docker Compose](https://docs.docker.com/compose/) 

>##Redis

1. If **boot2docker** is not already running, you'll need to start it with ***"boot2docker up"***
1. Navigate to the **/docker** directory of the project. 
2. The very first time you will need to run ***"docker-compose up"***. This will build the docker images and containers as well as start them.
To stop Redis use ***"docker-compose stop redis"***. Subsequent usages can use the command ***"docker-compose start redis"*** instead

>##Run Tests (assumes **Redis** is already running)

1. From the main project directory, run the command ***"activator test"***.

>##Run API Server

1. From the main project directory, run the command ***"activator run"***
2. Navigate in a browser to the index page at [http://localhost:9000](http://localhost:9000), which redirects you to 
the [API documentation](http://localhost:9000/assets/swagger-ui/index.html) via [Swagger](http://swagger.io)) to view review and/or 
interactive utilize the API endpoints 
  . or use e.g. ***curl http://localhost:9000/customers*** to hit the API endpoints
  



