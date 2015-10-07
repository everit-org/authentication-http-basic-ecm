/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.authentication.http.basic.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.everit.authentication.context.AuthenticationContext;
import org.everit.authentication.simple.SimpleSubject;
import org.everit.authentication.simple.SimpleSubjectManager;
import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.resource.ResourceService;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * Test for HttpBasicAuthFilter.
 */
@Component(componentId = "HttpBasicAuthFilterTest",
    configurationPolicy = ConfigurationPolicy.FACTORY)
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@StringAttributes({
    @StringAttribute(attributeId = TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE,
        defaultValue = "junit4"),
    @StringAttribute(attributeId = TestRunnerConstants.SERVICE_PROPERTY_TEST_ID,
        defaultValue = "HttpBasicAuthFilterTest")
})
@Service(value = HttpBasicAuthFilterTestComponent.class)
public class HttpBasicAuthFilterTestComponent {

  private static final String HTTP_BASIC_AUTH_FILTER_PATTERN = "/hello/secure";

  private static final String PASSWORD = "open sesame";

  private static final String PUBLIC_HELLO_WORLD_ALIAS = "/hello";

  private static final String SECURE_HELLO_WORLD_ALIAS = "/hello/secure";

  private static final String USERNAME = "Aladdin";

  private long authenticatedResourceId;

  private AuthenticationContext authenticationContext;

  private long defaultResourceId;

  private Servlet helloWorldServlet;

  private Filter httpBasicAuthenticationFilter;

  private String publicUrl;

  private ResourceService resourceService;

  private String secureUrl;

  private SimpleSubjectManager simpleSubjectManager;

  private Server testServer;

  /**
   * Component activator method.
   */
  @Activate
  public void activate(final BundleContext context, final Map<String, Object> componentProperties)
      throws Exception {
    testServer = new Server(0);
    ServletContextHandler servletContextHandler = new ServletContextHandler();
    testServer.setHandler(servletContextHandler);

    servletContextHandler.addServlet(
        new ServletHolder("publichelloWorld", helloWorldServlet), PUBLIC_HELLO_WORLD_ALIAS);
    servletContextHandler.addServlet(
        new ServletHolder("securehelloWorld", helloWorldServlet), SECURE_HELLO_WORLD_ALIAS);
    servletContextHandler.addFilter(
        new FilterHolder(httpBasicAuthenticationFilter), HTTP_BASIC_AUTH_FILTER_PATTERN, null);

    testServer.start();

    String testServerURI = testServer.getURI().toString();
    String testServerURL = testServerURI.substring(0, testServerURI.length() - 1);

    publicUrl = testServerURL + PUBLIC_HELLO_WORLD_ALIAS;
    secureUrl = testServerURL + SECURE_HELLO_WORLD_ALIAS;

    long resourceId = resourceService.createResource();
    simpleSubjectManager.delete(USERNAME);
    SimpleSubject simpleSubject = simpleSubjectManager.create(resourceId, USERNAME, PASSWORD);
    authenticatedResourceId = simpleSubject.getResourceId();
    defaultResourceId = authenticationContext.getDefaultResourceId();
  }

  private void assertGet(final String url, final Header header, final int expectedStatusCode,
      final Long expectedResourceId) throws IOException {
    HttpClient httpClient = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(url);
    if (header != null) {
      httpGet.addHeader(header);
    }
    HttpResponse httpResponse = httpClient.execute(httpGet);
    Assert.assertEquals("Wrong status code on URL [" + url + "] with header [" + header + "]",
        expectedStatusCode,
        httpResponse.getStatusLine().getStatusCode());
    if (expectedStatusCode == HttpStatus.SC_OK) {
      HttpEntity httpEntity = httpResponse.getEntity();
      InputStream inputStream = httpEntity.getContent();
      StringWriter writer = new StringWriter();
      IOUtils.copy(inputStream, writer);
      String responseBodyAsString = writer.toString();
      Assert.assertEquals(expectedResourceId, Long.valueOf(responseBodyAsString));
    }
  }

  /**
   * Component deactivate method.
   */
  @Deactivate
  public void deactivate() throws Exception {
    if (testServer != null) {
      testServer.stop();
      testServer.destroy();
    }
  }

  private String encode(final String plain) {
    Encoder encoder = Base64.getEncoder();
    String encoded = encoder.encodeToString(plain.getBytes(StandardCharsets.UTF_8));
    return encoded;
  }

  @ServiceRef(defaultValue = "")
  public void setAuthenticationContext(final AuthenticationContext authenticationContext) {
    this.authenticationContext = authenticationContext;
  }

  @ServiceRef(defaultValue = "")
  public void setHelloWorldServlet(final Servlet helloWorldServlet) {
    this.helloWorldServlet = helloWorldServlet;
  }

  @ServiceRef(defaultValue = "")
  public void setHttpBasicAuthenticationFilter(final Filter httpBasicAuthenticationFilter) {
    this.httpBasicAuthenticationFilter = httpBasicAuthenticationFilter;
  }

  @ServiceRef(defaultValue = "")
  public void setResourceService(final ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @ServiceRef(defaultValue = "")
  public void setSimpleSubjectManager(final SimpleSubjectManager simpleSubjectManager) {
    this.simpleSubjectManager = simpleSubjectManager;
  }

  @Test
  public void testAccessPublicUrl() throws IOException {
    assertGet(publicUrl, null,
        HttpServletResponse.SC_OK, defaultResourceId);
  }

  @Test
  public void testAccessSecureUrl() throws IOException {
    assertGet(secureUrl,
        new BasicHeader("Authorization", "Basic " + encode(USERNAME + ":" + PASSWORD)),
        HttpServletResponse.SC_OK, authenticatedResourceId);
    assertGet(secureUrl, null,
        HttpServletResponse.SC_UNAUTHORIZED, null);
    assertGet(secureUrl,
        new BasicHeader("Authorization", "BasiC " + encode(USERNAME + ":" + PASSWORD)),
        HttpServletResponse.SC_UNAUTHORIZED, null);
    assertGet(secureUrl, new BasicHeader("Authorization", "Basic " + "1234567890ABCDEFGHI"),
        HttpServletResponse.SC_UNAUTHORIZED, authenticatedResourceId);
    assertGet(secureUrl, new BasicHeader("Authorization", "Basic " + encode(USERNAME + PASSWORD)),
        HttpServletResponse.SC_UNAUTHORIZED, authenticatedResourceId);
    assertGet(secureUrl,
        new BasicHeader("Authorization", "Basic " + encode(USERNAME + ":" + PASSWORD + "1")),
        HttpServletResponse.SC_UNAUTHORIZED, authenticatedResourceId);
  }
}
