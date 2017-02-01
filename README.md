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

##Download Time Series Bootstrap SDK

```
git clone https://github.com/PredixDev/timeseries-bootstrap.git
```

##Build it

1. From the command line, go the the project directory.
2. Run as

```
  mvn clean package
```

##Run integration tests

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

2. From the command line, go the the project directory.
3. Run as 

``` 
 mvn clean install.
```

##How to include as a dependency
1. Modify the pom.xml as follows in the SDK for which you would like to have timeseries-bootstrap as a dependency.
2. Add the following under the ```<dependencies>``` section with the latest version:
```
 <dependency>
	<groupId>com.ge.predix.solsvc</groupId>
	<artifactId>timeseries-bootstrap</artifactId>
	<version>${predix-timeseries-client.version}</version>
 </dependency>
 ```
 
##Tech Stack

 - Spring
 - SpringTest
 - Maven
 
##More Details
 
 [Exploring Time Series](https://www.predix.io/resources/tutorials/journey.html#Journey.Exploring Time Series)

[![Analytics](https://ga-beacon.appspot.com/UA-82773213-1/timeseries-bootstrap/readme?pixel)](https://github.com/PredixDev)
