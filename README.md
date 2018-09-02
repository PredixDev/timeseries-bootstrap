<a href="http://predixdev.github.io/timeseries-bootstrap/javadocs/index.html" target="_blank" >
	<img height="50px" width="100px" src="images/javadoc.png" alt="view javadoc"></a>
&nbsp;
<a href="http://predixdev.github.io/timeseries-bootstrap" target="_blank">
	<img height="50px" width="100px" src="images/pages.jpg" alt="view github pages">
</a>

Time Series Bootstrap SDK
================================

Welcome to Time Series Bootstrap SDK. This is a SDK in Java for calling the Predix Time Series API to post and query data. It has built in mechanism to perform authenticated calls to Time Series API. It can be used as a dependency to other java back-end applications for creating and querying time series data seamlessly. 
For further inforation please view the [Time Series Tutorial](https://www.predix.io/resources/tutorials/journey.html#1612)

## Download Time Series Bootstrap SDK

```
git clone https://github.com/PredixDev/timeseries-bootstrap.git
```

## Set your encrypted password

In your maven settings.xml file ensure you have [added your encrypted username and password](https://www.predix.io/resources/tutorials/tutorial-details.html?tutorial_id=1560&tag=1608&journey=Development%20tools%20and%20tips&resources=1565,1560).

## Build it

1. From the command line, go to the project directory.
2. Run as

```
  mvn clean package
```

## Run integration tests

1. Edit config/application.properties as follows. For further information on configuring Predix Time Series service, please refer to the tutorials called [Exploring Time Series](https://www.predix.io/resources/tutorials/journey.html#Journey.Exploring Time Series).

```
 predix.oauth.issuerId=put.your.uaa.issuerId.here
 #you may put client:secret as unencoded cleartext by setting predix.oauth.clientIdEncode=true
 predix.oauth.clientIdEncode=false
 predix.oauth.clientId=you.should.base64encode(put.your.clientId:put.your.clientSecret separated by a colon)  

 predix.timeseries.websocket.uri=wss://put.your.timeseries.ingest.uri.here/v1/stream/messages

 predix.timeseries.queryUrl=https://put.your.timeseries.service.instance.here/v1/datapoints
 predix.timeseries.zoneid=put.your.timeseries.zoneid.aka.instanceid.here
```

2. From the command line, go to the project directory.
3. Run as 

``` 
 mvn clean install.
```

## How to include as a dependency
1. Modify the pom.xml as follows in the SDK for which you would like to have timeseries-bootstrap as a dependency.
2. Add the following under the ```<dependencies>``` section with the latest version:
```
 <dependency>
	<groupId>com.ge.predix.solsvc</groupId>
	<artifactId>timeseries-bootstrap</artifactId>
	<version>${predix-timeseries-client.version}</version>
 </dependency>
 ```
 
 ## Dependencies
 
  - [Predix Websocket Client](https://github.com/PredixDev/predix-websocket-client)
  	- [Predix Rest Client](https://github.com/PredixDev/predix-rest-client)
	
The Time Series API model is defined here
  - [Ext Model](https://github.com/PredixDev/ext-interface)

Accessing the model is done here using a class JsonMapper.
  - [Ext Util](https://github.com/PredixDev/ext-interface)
 
 ## Properties
 
 Standard Spring [property hierarchy](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) but not using Spring Boot jar though.
 
 - **config/application.properties** for local tests
 - **src/main/resources/application.properties** are added to Jar files, be careful
 - **src/main/resources/application-default.properties** in each jar, use at microservice level not SDK level, useful for microservice wide defaults
 - **src/main/resources/application-cloud.properties** in each jar - used when in "cloud" profile
 - **src/main/resources/application.properties** in each jar, use at microservice level not SDK level
 - **manifest.yml** - environment variables - uses underscores not periods - sample microservice transforms these underscore based variables to period based.
 - **environment variables** - SDKs read from Cloud (e.g. VCAP) environment variables to get properties at runtime in the cloud for things like UAA or Time Series Zone Id.
 
 ## Usage
 
 Usually you'll place this dependency inside a Spring Boot microservice.  It's not required but that means you'll need to manage all the Class creations that dependency injection handles automatically.  
 
 ### Spring Context 
 
 Spring resolves the beans by doing a component scan of package directories.   See the test cases for examples.  See [Wind Data](https://github.com/PredixDev/winddata-timeseries-service) microservice for an example in a running microservice.
 
 In a test case place this annotation above the Class definition
 
     @ContextConfiguration(locations = { 
		"classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
		"classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml" })    
		
The first file is located inside the timeseries jar file, it tells Spring to scan the com.ge.predix.solsvc.timeseries.bootstrap package for Spring beans.
 
 The second file is used just for JUnit Tests, it sets up the properties file lookups in the exact same way tha a Spring Boot microservice does since this project isn't a Spring Boot project.
		
In a production file place this annotation above the Class definition

    @ImportResource(
     {
     "classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml"
     })
 
 

### Profile

JUnit tests are run in the "local" profile.  In the cloud, when in a Spring Boot microservice, they auto-magically set a "cloud" profile.

### Autowire

Simply place this @Autowired variable in your class.

    @Autowired
    private TimeseriesClient       timeseriesClient;

### Initialize the connection pool

Placing this in your microservice class will initialize the Websocket and REST connection pools.

    @PostConstruct
    public void init()
    {
        try
        {
            this.timeseriesClient.createTimeseriesWebsocketConnectionPool();
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "unable to set up timeseries Websocket Pool timeseriesConfig=" + this.timeseriesClient.getTimeseriesConfig(), e);
        }
    }
 
 ### Call the Timeseries SDK methods
 

This will post data to the Time Series ingest web socket endpoint, assuming you create a DataPointsIngestion Java object first. 

    this.timeseriesClient.postDataToTimeseriesWebsocket(datapointsIngestion);
    
This for example will make a REST request to get the list of Tags.  Take a look at the TimeseriesClient interface for more API methods.
 
    List<Header> headers = this.timeseriesClient.getTimeseriesHeaders();
    TagsList tagsList = this.timeseriesClient.listTags(headers);

## Tech Stack

 - Spring
 - SpringTest
 - Maven
 
## More Details
 
 [Exploring Time Series](https://www.predix.io/resources/tutorials/journey.html#Journey.Exploring Time Series)

[![Analytics](https://ga-beacon.appspot.com/UA-82773213-1/timeseries-bootstrap/readme?pixel)](https://github.com/PredixDev)
