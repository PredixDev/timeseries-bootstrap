package com.ge.predix.solsvc.bootstrap.tsb.factories;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.kairosdb.client.deserializer.GroupByDeserializer;
import org.kairosdb.client.response.GetResponse;
import org.kairosdb.client.response.GroupResult;
import org.kairosdb.client.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ge.predix.solsvc.bootstrap.tbs.entity.InjectionMetricBuilder;
import com.ge.predix.solsvc.bootstrap.tbs.entity.TimeseriesQueryBuilder;
import com.ge.predix.solsvc.bootstrap.tbs.response.entity.TimeseriesQueryResponse;
import com.ge.predix.solsvc.bootstrap.tsb.client.TimeseriesRestConfig;
import com.ge.predix.solsvc.bootstrap.tsb.client.TimeseriesWSConfig;
import com.ge.predix.solsvc.bootstrap.tsb.client.TimeseriesWebsocketClient;
import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;
import com.ge.predix.solsvc.restclient.impl.CxfAwareRestClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * @author 212421693
 *
 */
@Component
public class TimeseriesFactoryImpl
        implements TimeseriesFactory
{

    private static Logger             log = LoggerFactory.getLogger(TimeseriesFactory.class);

    @Autowired
    private CxfAwareRestClient        restClient;

    @Autowired
    private TimeseriesRestConfig      timeseriesRestConfig;

    @Autowired
    private TimeseriesWSConfig        tsInjectionWSConfig;

    @Autowired
    private TimeseriesWebsocketClient timeseriesWebsocketClient;

    @Autowired
    private ApplicationContext        applicationContext;

    @Autowired
    private IOauthRestConfig                restConfig;

    @Override
    public Response create(InjectionMetricBuilder metricBuilder)
    {

        Response response = new Response();
        try
        {
            response = this.timeseriesWebsocketClient.pushMetrics(metricBuilder);
            log.debug("Response for metrics" + metricBuilder.getInjectionRequest().getMessageId() //$NON-NLS-1$
                    + " " + response.getStatusCode()); //$NON-NLS-1$
        }
        catch (URISyntaxException | IOException e)
        {
            log.debug("Exception in URI formation " + e);//$NON-NLS-1$
            throw new RuntimeException(e);
        }
        return response;

    }

    @Override
    public TimeseriesQueryResponse query(String uri, TimeseriesQueryBuilder query, List<Header> headers)
    {
        TimeseriesQueryResponse response = null;

        if ( query != null )
        {
            try
            {
                HttpResponse httpResponse = this.restClient.post(uri, query.build(), headers);
                org.apache.http.HttpEntity responseEntity = httpResponse.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(GroupResult.class, new GroupByDeserializer());
                Gson gson = builder.create();
                response = gson.fromJson(responseString, TimeseriesQueryResponse.class);
                return response;
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            finally
            {
//                if ( client != null ) client.shutdown();
            }
        }

        return response;
    }

    @SuppressWarnings("nls")
    @Override
    public List<String> getAttributeNames(List<Header> headers)
    {
        try
        {

            // client = getPredixTimeseriesClient(context);
            // GetResponse response = client.getAttributesNames();
            // return response.getResults();

            String uri = this.timeseriesRestConfig.getHostUri() + "/attributenames";
            headers.add(new BasicHeader(this.timeseriesRestConfig.getPredixZoneIdHeaderName(), this.timeseriesRestConfig.getZoneId()));
            HttpResponse httpResponse = this.restClient.get(uri, headers);
            org.apache.http.HttpEntity responseEntity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            GetResponse response = gson.fromJson(responseString, GetResponse.class);
            return response.getResults();

        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
//            if ( client != null ) client.shutdown();
        }
    }

    @SuppressWarnings("nls")
    @Override
    public List<String> getTagNames(List<Header> headers)
    {
        try
        {

            // client = getPredixTimeseriesClient(context);
            // GetResponse response = client.getTagNames();
            // return response.getResults();

            String uri = this.timeseriesRestConfig.getHostUri() + "/tagnames";
            headers.add(new BasicHeader(this.timeseriesRestConfig.getPredixZoneIdHeaderName(), this.timeseriesRestConfig.getZoneId()));
            HttpResponse httpResponse = this.restClient.get(uri, headers);
            org.apache.http.HttpEntity responseEntity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            GetResponse response = gson.fromJson(responseString, GetResponse.class);
            return response.getResults();

        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
//            if ( client != null ) client.shutdown();
        }
    }

    @SuppressWarnings("nls")
    @Override
    public Response deleteMetric(String tag, List<Header> headers)
    {
        try
        {

            // client = getPredixTimeseriesClient(context);
            // Response response = client.deleteTag(tag);
            // return response;

            String uri = this.timeseriesRestConfig.getHostUri() + "/metric/" + tag;
            headers.add(new BasicHeader(this.timeseriesRestConfig.getPredixZoneIdHeaderName(), this.timeseriesRestConfig.getZoneId()));
            HttpResponse httpResponse = this.restClient.get(uri, headers);
            org.apache.http.HttpEntity responseEntity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Response response = gson.fromJson(responseString, Response.class);
            return response;

        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
//            if ( client != null ) client.shutdown();
        }
    }

    @SuppressWarnings("nls")
    @Override
    public Response delete(TimeseriesQueryBuilder query, List<Header> headers)
    {
        try
        {

            // client = getPredixTimeseriesClient(context);
            // Response response = client.delete(query);

            String uri = this.timeseriesRestConfig.getHostUri() + "/delete";
            HttpResponse httpResponse = this.restClient.post(uri, query.build(), headers);
            org.apache.http.HttpEntity responseEntity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Response response = gson.fromJson(responseString, Response.class);
            return response;

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
//            if ( client != null ) client.shutdown();
        }
    }

//    @Override
//    public void registerCustomDataType(String groupType, @SuppressWarnings("rawtypes") Class dataPointClass,
//            List<Header> headers)
//    {
//        
//
////            checkArgument(!this.customGroupTypes.containsKey(groupType), "Type has already been registered"); //$NON-NLS-1$
////            this.customGroupTypes.put(groupType, dataPointClass);
//
//        
//
//    }

    @SuppressWarnings(
    {
            "rawtypes", "nls"
    })
    @Override
    public Class getDataPointValueClass(String groupType)
    {
        switch (groupType)
        {
            case "number":
                return Number.class;
            case "text":
                return String.class;

            default:
                throw new UnsupportedOperationException("unknown GroupType=" + groupType);
        }
    }

    @SuppressWarnings("nls")
    @Override
    public List<String> getVersion(List<Header> headers)
    {
        try
        {

            // client = getPredixTimeseriesClient(context);
            // GetResponse response = client.getVersion();
            // return response.getResults();

            String uri = this.timeseriesRestConfig.getHostUri() + "/version";
            headers.add(new BasicHeader(this.timeseriesRestConfig.getPredixZoneIdHeaderName(), this.timeseriesRestConfig.getZoneId()));
            HttpResponse httpResponse = this.restClient.get(uri, headers);
            org.apache.http.HttpEntity responseEntity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            GetResponse response = gson.fromJson(responseString, GetResponse.class);
            return response.getResults();

        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
//            if ( client != null ) client.shutdown();
        }
    }

    @SuppressWarnings("nls")
    @Override
    public TimeseriesQueryResponse currentDataPoint(List<String> tags, List<Header> headers)
    {
        TimeseriesQueryResponse response = new TimeseriesQueryResponse();

        if ( !CollectionUtils.isEmpty(tags) )
        {
            try
            {
                // client = getPredixTimeseriesClient(context);
                // response = client.getCurrentDataPoint(tags);

                StringBuffer queryString = new StringBuffer("?tags=");
                // construct the tags :
                if ( !CollectionUtils.isEmpty(tags) )
                {

                    for (String tag : tags)
                    {
                        queryString.append(tag);
                        queryString.append(","); //$NON-NLS-1$
                    }

                }
                // now remove the last occurrence of delimiter
                String currentQueryUrl = this.timeseriesRestConfig.getCurrentValueUri()
                        + StringUtils.removeEnd(queryString.toString(), ",");

                HttpResponse httpResponse = this.restClient.get(currentQueryUrl, headers);
                org.apache.http.HttpEntity responseEntity = httpResponse.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(GroupResult.class, new GroupByDeserializer());
                Gson gson = builder.create();
                response = gson.fromJson(responseString, TimeseriesQueryResponse.class);
                return response;
            }
            catch (ParseException e)
            {
                throw new RuntimeException(e);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            finally
            {
//                if ( client != null ) client.shutdown();
            }
        }

        return response;
    }

}
