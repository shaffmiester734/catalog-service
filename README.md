# Git Data Fetcher

## Overview
This application can be used to retrieve a subset of a user's data from GitHub.

## Running
As long as you have java installed, switch to the directory where you checked out the code and run the following command:

```shell
./gradlew bootRun
```

The application itself will run on port `8080`. It takes a GitHub username as a path parameter.

For example, if you have `curl` and `jq` installed, you can get the details for the _octocat_ user with the following command:

```shell
curl localhost:8080/octocat | jq
```

The application is also running the Spring Boot actuator which is available on port `8090`, i.e. http://localhost:8090/actuator.  

If you are unfamiliar with the spring boot actuator it provides endpoints you can use to monitor and administer your running application.
For instance, you can check the health of the application of the application at the health endpoint `/actuator/health`:

```shell
curl localhost:8090/actuator/health
```

If it's healthy you will see the following:

```json
{
  "status": "UP"
}
```

If you prefer to run the application as a container, you can build an image locally with the following command:

**Note:** This requires docker to be running

```shell
./gradlew bootBuildImage
```

This will create an image named `docker.io/library/git-data-retriever:0.0.1-SNAPSHOT`. You can then run a container from this 
image, exposing the same ports discussed above, with the following command:

```shell
docker run -p 8080:8080 -p 8090:8090 docker.io/library/git-data-retriever:0.0.1-SNAPSHOT
```

## Architecture
The application is a simple [Spring Boot](https://docs.spring.io/spring-boot/index.html) App with [Spring Wev MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html). It consists of a Rest Controller, Service, Domain models, and a utility class.
The Controller is used to deliver the API. The Service consists of an Interface and one provided implementation. The use of an interface allows 
us to abstract away the details of the data fetching and manipulation. The Interface itself consists a single method 
that takes a username as a parameter and returns the data in the format specified by the client. The provided implementation 
is specific to GitHub. Under the hood it uses Spring's `RestClient` library to make the actual calls to the GitHub API. The app 
also relies on Spring Web's use of Jackson for Serialization/Deserialization. The utility class provides some static methods 
for data conversions and allows for easier testing.

The app also consists of 2 sets of Domain objects, one for the data coming in from GitHub and one for the data going out 
to the client. The domain objects themselves are Java Records. I chose to use Records because they are used for immutable data carriers and are less verbose than traditional Classes. 
I made all of fields Strings. This makes them less likely to cause errors if Changes are made to the GitHub API. I did contemplate [Instant](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Instant.html) and a [LocalDateTime](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/LocalDateTime.html) to represent the 
users _created_ date based on the Client's requested api and the format coming from GitHub. In the end I just left them as Strings.
I do attempt to use 2 [DateTimeFormatters](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/format/DateTimeFormatter.html) to handle the format conversions
To make it more it less prone to error, if an issue occurs during the conversion it just returns the string as it came from GitHub.

I did check to see if there were any existing Java libraries I could use to interact with GitHub API, but did not find any suitable options. 
GitHub itself linked to [two](https://docs.github.com/en/rest/using-the-rest-api/libraries-for-the-rest-api?apiVersion=2022-11-28#java), but
The first one was a broken link and the 2nd was someone's personal website. I found another one from [Spotify](https://docs.github.com/en/rest/using-the-rest-api/libraries-for-the-rest-api?apiVersion=2022-11-28#java), but that was more focused on DevOps 
type interactions with GitHub (like making commits and creating repos), the version has not hit 1.0, and it abstracted the Http 
Communication mechanisms.

To help alleviate GitHub's rate limiting I have enabled caching. I use Spring Boot's built-in Caching support and configured it via an
annotation on the service method which is keyed off of the username. I wanted to be able to pick up changes fairly quickly, but the default 
cache implementation provided by Spring does not come with built-in support for configuring a TTL. Fortunately Spring has integrations
with several caching libraries. I chose caffeine because it runs in memory and doesn't require a separate running application.

To help monitor the application I have included the spring boot actuator dependency and enabled the following endpoints: `health,info,metrics,caches,loggers,logfile`.
I have also used Springs Logging support to provide some helpful messages.

## Testing
I have included standard Unit tests along with Integration tests. The Integration tests utilize Spring Boot's testing support in 
combination with [WireMock](https://wiremock.org/). I Choose WireMock because It has excellent [integration with Spring Boot](https://wiremock.org/docs/spring-boot/).
WireMock allows me to stand up an HTTP server and have it respond as if my Service was interacting with the GitHub API. To make the tests more realistic
I copied out the actual responses for _octocat_ user. Using Wiremock also allows me to test the Caching since I can determine exactly 
how many times a given endpoint was  hit.

## Security Considerations
I made a couple decisions to make the application more secure. I have configured the actuator to use a different port (`8090`) than 
the one used by the application (`8080`). This will help ensure that these endpoints are not accidentally exposed to the public. 
I have also configured a Spring Web [ExceptionHandler](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html) to return a custom message 
in the event of an uncaught Exception. This helps ensure that a caller does not inadvertently receive a java Stack Traces.