package org.everit.osgi.authentication.http.basic;

/**
 * Constants of the HTTP Basic Authentication Filter component.
 */
public final class HttpBasicAuthFilterConstants {

  public static final String DEFAULT_REALM = "default-realm";

  public static final String DEFAULT_SERVICE_DESCRIPTION =
      "Default HTTP Basic Authentication Filter";

  public static final String PROP_AUTHENTICATION_PROPAGATOR = "authenticationPropagator.target";

  public static final String PROP_AUTHENTICATOR = "authenticator.target";

  public static final String PROP_REALM = "realm";

  public static final String PROP_RESOURCE_ID_RESOLVER = "resourceIdResolver.target";

  public static final String SERVICE_FACTORYPID_HTTP_BASIC_AUTH =
      "org.everit.authentication.http.basic.ecm.HttpBasicAuthenticationFilter";

  private HttpBasicAuthFilterConstants() {
  }

}
