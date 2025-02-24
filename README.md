# Git Data Fetcher

## Overview
This application can be used to retrieve a subset of a users data from GitHub.

## Running
As long as you have java installed, you switch to the directory where you have checked out the code can run the application from the command line at the root directory using the following command:

```shell
./gradlew bootRun
```

The application itself will run on port `8080`. You can call it with a GitHub username to retrieve a subset of that user's data.

For example, if you have `curl` and `jq` installed, you can get the details for the _octocat_ user with the following command:

```shell
curl localhost:8080/octocat | jq
```

The actuator is available at port `8090`: http://localhost:8090/actuator.  

If you are unfamiliar with the spring boot actuator it provides endpoints you can use to administer and monitor your running application.
For instance You can check the health of the application by checking the health endpoint:

```shell
curl localhost:8090/actuator/health
```

If it's healthy you will see the following

```json
{
  "status": "UP"
}
```

If you prefer to use docker you can build an image locally with the following command:

**Note:** This requires docker to be running

```shell
./gradlew bootBuildImage
```

This will create an image named `docker.io/library/git-data-retriever:0.0.1-SNAPSHOT`. You can then run a container from this 
image (exposing the same ports mentioned above) with the following command:

```shell
docker run -p 8080:8080 -p 8090:8090 docker.io/library/git-data-retriever:0.0.1-SNAPSHOT
```

## Architecture
The application is a simple [Spring Boot](https://docs.spring.io/spring-boot/index.html) with [Spring Wev MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html). It consists of a Rest Controller to deliver the API and a Service
Interface with a single method that takes in a username and returns the data in the format specified by the client. 
I used an Interface abstract away the implementation details used to retrieve the data and convert it to the format requested by the Client..

The Service itself is an Interface. This allows us hide details service to abstract away the implementation details of fetching data from github. 
The implementation of the service uses Spring's `RestClient`library to make calls to the GitHub API. It relies on Spring 
Web's use of Jackson for Serialization/Deserialization. It uses 2 sets of Domain objects, one set for the data coming in from 
GitHub and one for the data going out to consumers. The domain objects themselves are Java Records since they are just 
immutable data carriers. I made all of fields Strings, this will make them less likely to cause errors if something changes. 
I did contemplate [Instant](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Instant.html) and a [LocalDateTime](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/LocalDateTime.html) to represent the 
created date based on the Client's requested api and the format coming from GitHub. In the end I just left them as Strings.
I do attempt to use [DateTimeFormatter](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/format/DateTimeFormatter.html) I was worried this might lead to errors thoughThis helps remove some of the boilerplate code required when working with for traditional Java Classes.

I did check to see if there were any existing java libraries I could use to interact with GitHub API, but did not find any suitable options. 
GitHub itself linked to [two](https://docs.github.com/en/rest/using-the-rest-api/libraries-for-the-rest-api?apiVersion=2022-11-28#java), but
The first one was a broken link and the 2nd was someone's personal website. I found another one from [Spotify](https://docs.github.com/en/rest/using-the-rest-api/libraries-for-the-rest-api?apiVersion=2022-11-28#java),
but that was more focused on DevOps type interactions with GitHub (like making commits and creating repos), the version has not hit 1.0, and it abstracted the Http Communication mechanisms.

To help limit rate limiting I have enabled caching with Spring Boot's built-in Caching support and configured it via an
annotation on the service method keyed off of the username. I wanted to be able to pick up changes fairly quickly The default in memory cache provided by spring does not come 
with built in support for configuring a TTL for the cache. I wanted to evict objects after a short amount of time to be 
able to pick up able to pick up changes.
make the caching a little more robust I included the boot caching starter and the caffeine caching library.

I have included the spring boot actuator dependency and enabled the following endpoints: `health,info,metrics,caches,loggers,logfile`.
These can be used to help monitor the application in a production environment.

## Testing
I have included standard Unit tests along with Integration tests. The Integration tests utilize Spring Boot's testing support in combination with [WireMock](https://wiremock.org/). 
I Choose WireMock because I have the most familiarity with it and it has excellent [integration with Spring Boot](https://wiremock.org/docs/spring-boot/).
WireMock allows me to stand up an HTTP server and have it respond as if my Service was interacting with the GitHub API. To make the tests more realistic
I copied out hte actual responses for _octocat_, I am also able to test the Caching because I determine exactly how many times a given endpoint was  hit.

## Security Considerations
I made a couple decisions to help make the application more secure that it othwerise would have been. First off I have configured
the actuator server to use a different port (`8090`) than the one used by the application (`8080`) This will help ensure that these actuator endpoints are not accidentally exposed to the public.
I also have configured a Spring Web ExceptionHandler to return a custom message in the event of an unanticaped Exception which should
help the application from accidentally providing a java Stack Traces to end users.