package com.ge.predix.solsvc.timeseries.bootstrap.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.timeseries.datapoints.ingestionresponse.AcknowledgementMessage;
import com.ge.predix.solsvc.ext.util.IJsonMapper;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.timeseries.bootstrap.config.ITimeseriesConfig;
import com.ge.predix.solsvc.websocket.client.WebSocketClient;
import com.neovisionaries.ws.client.WebSocketAdapter;

@RunWith(MockitoJUnitRunner.class)
public class TimeseriesClientImplTest {
	
	@InjectMocks
	protected TimeseriesClientImpl timeseriesClient;
	
	@Mock
	protected RestClient restClient;
	
	@Mock
	protected WebSocketClient webSocketClient;
	
	@Mock
	protected ITimeseriesConfig timeseriesConfig;
	
	@Mock
	protected IJsonMapper jsonMapper;
	
	@Captor
	protected ArgumentCaptor<WebSocketAdapter> listenerCaptor;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	private JsonMapper realJsonMapper = new JsonMapper();
	
	private DatapointsIngestion data;
	
	@SuppressWarnings("rawtypes")
	@Before
	public void init() {
		mockData();
		MockitoAnnotations.initMocks(this);

		when(jsonMapper.toJson(any())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return realJsonMapper.toJson(invocation.getArguments()[0]);
			}});
		when(jsonMapper.fromJson(anyString(), any())).thenAnswer(new Answer() {
			@SuppressWarnings("unchecked")
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return realJsonMapper.fromJson((String)invocation.getArguments()[0], (Class)invocation.getArguments()[1]);
			}});
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void verifyPostDataReturnsSuccessfullyWhenStatusIsSuccess() throws Exception {
		timeseriesClient.createTimeseriesWebsocketConnectionPool();
		
		verify(webSocketClient).init(any(), any(), listenerCaptor.capture());
		
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				String message = (String)invocation.getArguments()[0];
				DatapointsIngestion data = realJsonMapper.fromJson(message, DatapointsIngestion.class);
				AcknowledgementMessage ack = new AcknowledgementMessage();
				ack.setMessageId(data.getMessageId());
				ack.setStatusCode(202);
				listenerCaptor.getValue().onTextMessage(null, realJsonMapper.toJson(ack));
				return null;
			}
		}).when(webSocketClient).postTextWSData(anyString());

		timeseriesClient.postDataToTimeseriesWebsocket(data);
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void verifyPostDataThrowsErrorWhenStatusIsFail() throws Exception {
		timeseriesClient.createTimeseriesWebsocketConnectionPool();
		
		verify(webSocketClient).init(any(), any(), listenerCaptor.capture());
		
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				String message = (String)invocation.getArguments()[0];
				DatapointsIngestion data = realJsonMapper.fromJson(message, DatapointsIngestion.class);
				AcknowledgementMessage ack = new AcknowledgementMessage();
				ack.setMessageId(data.getMessageId());
				ack.setStatusCode(500);
				listenerCaptor.getValue().onTextMessage(null, realJsonMapper.toJson(ack));
				return null;
			}
		}).when(webSocketClient).postTextWSData(anyString());

		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage(new BaseMatcher<String>() {
			@Override
			public boolean matches(Object item) {
				return item.toString().contains("java.io.IOException");
			}

			@Override
			public void describeTo(Description description) {
			}
		});
		timeseriesClient.postDataToTimeseriesWebsocket(data);
	}
	
	@Test
	public void verifyPostDataThrowsErrorWhenThereIsNoResponse() throws Exception {
		timeseriesClient.createTimeseriesWebsocketConnectionPool();

		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage(new BaseMatcher<String>() {
			@Override
			public boolean matches(Object item) {
				return item.toString().contains("java.util.concurrent.TimeoutException");
			}

			@Override
			public void describeTo(Description description) {
			}
		});
		timeseriesClient.postDataToTimeseriesWebsocket(data);
	}
	
	private void mockData() {
		data = new DatapointsIngestion();
		data.setMessageId(String.valueOf(System.currentTimeMillis()));

		Body body = new Body();
		body.setName("SampleTag"); //$NON-NLS-1$
		List<Object> datapoint1 = new ArrayList<Object>();
		datapoint1.add(System.currentTimeMillis() - 30000);
		datapoint1.add(10);
		datapoint1.add(3);

		List<Object> datapoint2 = new ArrayList<Object>();
		datapoint1.add(System.currentTimeMillis() - 20000);
		datapoint2.add(9);
		datapoint2.add(1);

		List<Object> datapoint3 = new ArrayList<Object>();
		datapoint1.add(System.currentTimeMillis() - 10000);
		datapoint3.add(27);
		datapoint3.add(0);

		List<Object> datapoints = new ArrayList<Object>();
		datapoints.add(datapoint1);
		datapoints.add(datapoint2);
		datapoints.add(datapoint3);

		body.setDatapoints(datapoints);

		List<Body> bodies = new ArrayList<Body>();
		bodies.add(body);

		data.setBody(bodies);
	}
}
