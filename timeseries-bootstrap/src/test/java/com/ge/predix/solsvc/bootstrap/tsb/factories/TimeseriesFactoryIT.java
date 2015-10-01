package com.ge.predix.solsvc.bootstrap.tsb.factories;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kairosdb.client.builder.AggregatorFactory;
import org.kairosdb.client.builder.TimeUnit;
import org.kairosdb.client.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.solsvc.bootstrap.tbs.entity.InjectionBody;
import com.ge.predix.solsvc.bootstrap.tbs.entity.InjectionMetric;
import com.ge.predix.solsvc.bootstrap.tbs.entity.InjectionMetricBuilder;
import com.ge.predix.solsvc.bootstrap.tbs.entity.TimeseriesQueryBuilder;
import com.ge.predix.solsvc.bootstrap.tbs.response.entity.TimeseriesQueryResponse;

/**
 * 
 * 
 * @author 212421693
 */

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest(
{
    "server.port=0"
})
@ComponentScan("com.ge.predix.solsvc.restclient")
@ActiveProfiles("local")
public class TimeseriesFactoryIT extends BaseFactoryIT
{

    /**
     * 
     */
    @Autowired
    protected TimeseriesFactory timeseriesFactory;

    /**
     * -
     */
    @Test
    public void runAllTest()
    {
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        this.restClient.addZoneToHeaders(headers, this.timeseriesRestConfig.getZoneId());
        createMetrics();
        try {
            Thread.sleep(1000); /// due to delay in Injection pipeline and query                 
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        queryMetrics(headers);
        getCurrent(headers);
        // deleteMetric();
        // getAttributesNames();
        // getTagNames();
        // getVersion();

    }

    /**
     * @return -
     * 
     */
    /**
     * @return -
     */
    public List<String> getVersion()
    {
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        List<String> version = this.timeseriesFactory.getVersion(headers);
        return version;
    }

    /**
     * @param headers 
	 * 
	 */
    private void getCurrent(List<Header> headers)
    {
        List<String> tags = new ArrayList<String>();
        tags.add("RMD_metric1"); //$NON-NLS-1$
        TimeseriesQueryResponse response = this.timeseriesFactory.currentDataPoint(tags, headers);

        List<Double> dataPoints = response.getQueries().get(0).getResults().get(0).getValues().get(0);

        assertTrue(dataPoints.size() > 0);

    }

    /**
     * -
     */
    public void createMetrics()
    {
        for (int i = 0; i < 10; i++)
        {
            InjectionMetricBuilder builder = InjectionMetricBuilder.getInstance();
            InjectionMetric metric = new InjectionMetric(new Long(System.currentTimeMillis()));
            InjectionBody body = new InjectionBody("RMD_metric1"); //$NON-NLS-1$
            body.addAttributes("host", "server1");  //$NON-NLS-1$//$NON-NLS-2$
            body.addAttributes("customer", "Acme");  //$NON-NLS-1$//$NON-NLS-2$
            body.addDataPoint(System.currentTimeMillis(), 10).addDataPoint(System.currentTimeMillis(), 30L);
            metric.getBody().add(body);
            builder.addMetrics(metric);
            Response response = this.timeseriesFactory.create(builder);
            assertTrue(response != null);

        }

    }

    /**
     * -
     * @param headers 
     */
    public void queryMetrics(List<Header> headers)
    {
        // {"end_absolute":1440530126366,"start_relative":{"value":2,"unit":"DAYS"},"cacheTime":0,"metrics":[{"name":"crank-frame-velocity","tags":{"sourceTagId":["crank-frame-velocity-2015"]},"group_by":[],"aggregators":[]}]}

        TimeseriesQueryBuilder builder = TimeseriesQueryBuilder.getInstance();
        builder.setStart(1, TimeUnit.HOURS);
        builder.addTags("RMD_metric1").addAggregator(//$NON-NLS-1$
                AggregatorFactory.createAverageAggregator(2, TimeUnit.DAYS));

        TimeseriesQueryResponse response = this.timeseriesFactory.query(this.timeseriesRestConfig.getQueryUri(),
                builder, headers);

        List<Double> dataPoints = response.getQueries().get(0).getResults().get(0).getValues().get(0);
        assertTrue(dataPoints.size() > 0);
    }

    /**
     * -
     */
    @SuppressWarnings({

    })
    public void getAttributesNames()
    {
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        List<String> attributesNames = this.timeseriesFactory.getAttributeNames(headers);
        assertTrue(attributesNames.size() > 0);
    }

    /**
     * @throws IOException -
     */
    public void getTagNames()
            throws IOException
    {
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        List<String> tagNames = this.timeseriesFactory.getTagNames(headers);
        assertTrue(tagNames.size() > 0);
    }

    /**
     * @throws IOException -
     */
    public void deleteMetric()
            throws IOException
    {
        InjectionMetricBuilder metricsBuilder = setUpMetric("testDelete");//$NON-NLS-1$
        this.timeseriesFactory.create(metricsBuilder);
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        Response response = this.timeseriesFactory.deleteMetric("testDelete", headers);//$NON-NLS-1$
        assertTrue(response.getStatusCode() == 204);
    }

    private InjectionMetricBuilder setUpMetric(String metricsName)
    {
        InjectionMetricBuilder builder = InjectionMetricBuilder.getInstance();
        InjectionMetric metric = new InjectionMetric(new Double(Math.random()).longValue());
        InjectionBody body = new InjectionBody(metricsName);
        body.addAttributes("host", "server1");  //$NON-NLS-1$//$NON-NLS-2$
        body.addAttributes("customer", "Acme"); //$NON-NLS-1$ //$NON-NLS-2$
        body.addDataPoint(System.currentTimeMillis(), 10).addDataPoint(System.currentTimeMillis(), 30L);
        metric.getBody().add(body);
        builder.addMetrics(metric);

        return builder;

    }

}
