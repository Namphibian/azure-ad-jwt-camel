package com.vantia.homework;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.EnableRouteCoverage;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;



@RunWith(CamelSpringBootRunner.class)
@EnableRouteCoverage
@ActiveProfiles("test")
@UseAdviceWith
public class AdapterAzureAdApplicationTests {

	
	public static final String DIRECT_GET = "direct:get-expression-of-interest-route";
	public static final String ROUTE_GET = "get-token-verify-route-v1";
	
	public static final String MOCK_POINT = "mock:token";



	@Produce(uri = ROUTE_GET)
	private ProducerTemplate template;

	@Autowired
	private CamelContext camelContext;

	@EndpointInject(uri = MOCK_POINT)
	protected MockEndpoint mockEndpoint;

	@Ignore
	@Test
	@DirtiesContext
	public void testNoKidToken() throws Exception {
		camelContext.getRouteDefinition(ROUTE_GET).adviceWith(camelContext, new AdviceWithRouteBuilder() {

			@Override
			public void configure() throws Exception {
				// TODO Auto-generated method stub
				
			}});
			

		camelContext.setTracing(false);
		camelContext.start();
        template.sendBodyAndHeader(null,"jwttoken" , NoKidToken.TOKEN);

		

	}
	@Ignore
	@Test
	@DirtiesContext
	public void testNoKeyFound() throws Exception {
		camelContext.getRouteDefinition(ROUTE_GET).adviceWith(camelContext, new AdviceWithRouteBuilder() {

			@Override
			public void configure() throws Exception {
				// TODO Auto-generated method stub
				
			}});
			

		camelContext.setTracing(false);
		camelContext.start();
        template.sendBodyAndHeader(null,"jwttoken" , NoKidToken.TOKEN);

		

	}
	@Ignore
	@Test
	@DirtiesContext
	public void testNetworkError() throws Exception {
		camelContext.getRouteDefinition(ROUTE_GET).adviceWith(camelContext, new AdviceWithRouteBuilder() {

			@Override
			public void configure() throws Exception {
				// TODO Auto-generated method stub
				
			}});
			

		camelContext.setTracing(false);
		camelContext.start();
        template.sendBodyAndHeader(null,"jwttoken" , NoKidToken.TOKEN);

		

	}
	@Ignore
	@Test
	@DirtiesContext
	public void testKeyVerifySuccess() throws Exception {
		camelContext.getRouteDefinition(ROUTE_GET).adviceWith(camelContext, new AdviceWithRouteBuilder() {

			@Override
			public void configure() throws Exception {
				// TODO Auto-generated method stub
				
			}});
			

		camelContext.setTracing(false);
		camelContext.start();
        template.sendBodyAndHeader(null,"jwttoken" , NoKidToken.TOKEN);

		

	}

	

}
