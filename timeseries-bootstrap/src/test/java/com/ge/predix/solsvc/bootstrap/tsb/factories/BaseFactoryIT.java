package com.ge.predix.solsvc.bootstrap.tsb.factories;

import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import com.ge.predix.solsvc.bootstrap.tsb.client.TimeseriesRestConfig;
import com.ge.predix.solsvc.restclient.config.OauthRestConfig;
import com.ge.predix.solsvc.restclient.impl.CxfAwareRestClient;

/**
 * 
 * @author 212421693
 *
 */
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml", 
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml", 
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml"
})
@PropertySource("classpath:timeseries-config-test.properties")
public abstract class BaseFactoryIT
{

    @Autowired
    protected CxfAwareRestClient restClient;

    @Autowired
    TimeseriesRestConfig         timeseriesRestConfig;

    @Autowired
    protected OauthRestConfig         restConfig;

    protected String getToken(MessageContext context)
    {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> headers = ((Map<String, List<String>>) context.get(Message.PROTOCOL_HEADERS));
        List<String> authorization = headers.get("Authorization"); //$NON-NLS-1$
        return authorization.get(0).replace("BEARER", "Bearer"); //$NON-NLS-1$ //$NON-NLS-2$

    }

}
