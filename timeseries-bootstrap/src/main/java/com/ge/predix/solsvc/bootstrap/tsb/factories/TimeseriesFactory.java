package com.ge.predix.solsvc.bootstrap.tsb.factories;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.kairosdb.client.response.Response;

import com.ge.predix.solsvc.bootstrap.tbs.entity.InjectionMetricBuilder;
import com.ge.predix.solsvc.bootstrap.tbs.entity.TimeseriesQueryBuilder;
import com.ge.predix.solsvc.bootstrap.tbs.response.entity.TimeseriesQueryResponse;

/**
 * 
 * @author 212421693
 *
 */
public interface TimeseriesFactory
{

    /**
     * 
     * @param metrics -
     * @return -
     */
    public Response create(InjectionMetricBuilder metrics);


    /**
     * @param uri -
     * @param query -
     * @param headers -
     * @return -
     */
    TimeseriesQueryResponse query(String uri, TimeseriesQueryBuilder query, List<Header> headers);

    /**
     * 
     * @param headers - 
     * @return List<String>
     * @throws IOException e
     */
    public List<String> getAttributeNames(List<Header> headers);

    /**
     * 
     * @param headers -
     * @return List<String>
     */
    public List<String> getTagNames(List<Header> headers);

    /**
     * 
     * @param token String
     * @return List<String>
     * @throws IOException e
     */
    // public List<String> getAttributeValues(MessageContext context, String token) throws IOException;
    /**
     * 
     * @param name String
     * @param headers -
     * @return Response
     * @throws IOException e
     */
    public Response deleteMetric(String name, List<Header> headers)
            throws IOException;

    /**
     * 
     * @param uri -
     * @param builder TimeseriesQueryBuilder
     * @param headers -
     * @param token String
     * @return token
     */
    public Response delete(TimeseriesQueryBuilder builder, List<Header> headers);

    /**
     * 
     * @param groupType String
     * @param dataPointValueClass Class
     * @param headers -
     * @param token String
     */
//    public void registerCustomDataType(String groupType, @SuppressWarnings("rawtypes") Class dataPointValueClass,
//            List<Header> headers);

    /**
     * 
     * @param groupType String
     * @param headers -
     * @return Class
     */
    @SuppressWarnings("rawtypes")
    public Class getDataPointValueClass(String groupType);

    /**
     * @param headers -
     * @return -
     */
    public List<String> getVersion(List<Header> headers);

    /**
     * @param tags -
     * @param headers -
     * @return -
     */
    public TimeseriesQueryResponse currentDataPoint(List<String> tags, List<Header> headers);


}
