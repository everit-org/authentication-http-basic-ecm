/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.biz)
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
package org.everit.authentication.http.basic.ecm;

/**
 * Constants of the HTTP Basic Authentication Filter component.
 */
public final class HttpBasicAuthFilterConstants {

  public static final String ATTR_AUTHENTICATION_PROPAGATOR = "authenticationPropagator.target";

  public static final String ATTR_AUTHENTICATOR = "authenticator.target";

  public static final String ATTR_REALM = "realm";

  public static final String ATTR_RESOURCE_ID_RESOLVER = "resourceIdResolver.target";

  public static final String DEFAULT_REALM = "default-realm";

  public static final String DEFAULT_SERVICE_DESCRIPTION =
      "Default HTTP Basic Authentication Filter";

  public static final String SERVICE_FACTORYPID_HTTP_BASIC_AUTH =
      "org.everit.authentication.http.basic.ecm.HttpBasicAuthenticationFilter";

  private HttpBasicAuthFilterConstants() {
  }

}
