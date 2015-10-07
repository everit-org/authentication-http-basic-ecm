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
package org.everit.authentication.http.basic.ecm.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Filter;

import org.everit.authentication.context.AuthenticationPropagator;
import org.everit.authentication.http.basic.HttpBasicAuthFilter;
import org.everit.authentication.http.basic.ecm.HttpBasicAuthFilterConstants;
import org.everit.authenticator.Authenticator;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.ManualService;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.component.ComponentContext;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.resource.resolver.ResourceIdResolver;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * ECM component for {@link Filter} interface based on {@link HttpBasicAuthFilter}.
 */
@Component(componentId = HttpBasicAuthFilterConstants.SERVICE_FACTORYPID_HTTP_BASIC_AUTH,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    label = "Everit HTTP Basic Authentication Filter",
    description = "The component that implements HTTP Basic Authentication "
        + "mechanism as a Servlet Filter.")
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@StringAttributes({
    @StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION,
        defaultValue = HttpBasicAuthFilterConstants.DEFAULT_SERVICE_DESCRIPTION,
        priority = HttpBasicAuthFilterAttributePriority.P1_SERVICE_DESCRIPTION,
        label = "Service Description",
        description = "The description of this component configuration. It is used to easily "
            + "identify the service registered by this component.") })
@ManualService(value = Filter.class)
public class HttpBasicAuthFilterComponent {

  private AuthenticationPropagator authenticationPropagator;

  private Authenticator authenticator;

  /**
   * The realm attribute (case-insensitive) is required for all authentication schemes which issue a
   * challenge. The realm value (case-sensitive), in combination with the canonical root URL of the
   * server being accessed, defines the protection space. These realms allow the protected resources
   * on a server to be partitioned into a set of protection spaces, each with its own authentication
   * scheme and/or authorization database. The realm value is a string, generally assigned by the
   * origin server, which may have additional semantics specific to the authentication scheme.
   */
  private String realm;

  private ResourceIdResolver resourceIdResolver;

  private ServiceRegistration<?> serviceRegistration;

  /**
   * Component activator method.
   */
  @Activate
  public void activate(final ComponentContext<HttpBasicAuthFilterComponent> componentContext) {
    HttpBasicAuthFilter httpBasicAuthFilter =
        new HttpBasicAuthFilter(authenticationPropagator, authenticator, resourceIdResolver, realm);

    Dictionary<String, Object> serviceProperties =
        new Hashtable<>(componentContext.getProperties());
    serviceRegistration =
        componentContext.registerService(Filter.class, httpBasicAuthFilter, serviceProperties);
  }

  /**
   * Component deactivate method.
   */
  @Deactivate
  public void deactivate() {
    if (serviceRegistration != null) {
      serviceRegistration.unregister();
    }
  }

  @ServiceRef(attributeId = HttpBasicAuthFilterConstants.ATTR_AUTHENTICATION_PROPAGATOR,
      defaultValue = "",
      attributePriority = HttpBasicAuthFilterAttributePriority.P5_AUTHENTICATION_PROPAGATOR,
      label = "Authentication Propagator OSGi filter",
      description = "OSGi Service filter expression for AuthenticationPropagator instance.")
  public void setAuthenticationPropagator(final AuthenticationPropagator authenticationPropagator) {
    this.authenticationPropagator = authenticationPropagator;
  }

  @ServiceRef(attributeId = HttpBasicAuthFilterConstants.ATTR_AUTHENTICATOR, defaultValue = "",
      attributePriority = HttpBasicAuthFilterAttributePriority.P3_AUTHENTICATOR,
      label = "Authenticator OSGi filter",
      description = "OSGi Service filter expression for Authenticator instance.")
  public void setAuthenticator(final Authenticator authenticator) {
    this.authenticator = authenticator;
  }

  @StringAttribute(attributeId = HttpBasicAuthFilterConstants.ATTR_REALM,
      defaultValue = HttpBasicAuthFilterConstants.DEFAULT_REALM,
      priority = HttpBasicAuthFilterAttributePriority.P2_REALM, label = "Realm",
      description = "Pages in the same realm should share credentials. For more information see "
          + "RFC 1945 (HTTP/1.0) and RFC 2617 (HTTP Authentication referenced by HTTP/1.1).")
  public void setRealm(final String realm) {
    this.realm = realm;
  }

  @ServiceRef(attributeId = HttpBasicAuthFilterConstants.ATTR_RESOURCE_ID_RESOLVER,
      defaultValue = "",
      attributePriority = HttpBasicAuthFilterAttributePriority.P4_RESOURCE_ID_RESOLVER,
      label = "Resource ID Resolver OSGi filter",
      description = "OSGi Service filter expression for ResourceIdResolver instance.")
  public void setResourceIdResolver(final ResourceIdResolver resourceIdResolver) {
    this.resourceIdResolver = resourceIdResolver;
  }
}
