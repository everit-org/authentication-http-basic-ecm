package org.everit.osgi.authentication.http.basic.internal;

/**
 * Constants of HttpBasicAuthFilter attribute priority.
 */
public final class HttpBasicAuthFilterAttributePriority {

  public static final int P1_SERVICE_DESCRIPTION = 1;

  public static final int P2_REALM = 2;

  public static final int P3_AUTHENTICATOR = 3;

  public static final int P4_RESOURCE_ID_RESOLVER = 4;

  public static final int P5_AUTHENTICATION_PROPAGATOR = 5;

  private HttpBasicAuthFilterAttributePriority() {
  }
}
