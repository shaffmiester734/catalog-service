# Git Data Fetcher

## Overview
This application can be used to retrieve a subset of a git users data from github.

You can run this application from the command line using the following command:

## Running

```sh
./gradlew bootRun
```


The Applicaiton is written using [Spring Boot](https://docs.spring.io/spring-boot/index.html) and [Spring Wev MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html). It takes advantage of Spring Boot's built in [Caching](https://docs.spring.io/spring-boot/reference/io/caching.html) support.

```sh
curl localhost:8080/octocat | jq
```

## Architecture
It uses a service to abstract away the implementation details of fetching data from github.  This might come in
handy if we ever wanted to expand to other Git hosting platforms

