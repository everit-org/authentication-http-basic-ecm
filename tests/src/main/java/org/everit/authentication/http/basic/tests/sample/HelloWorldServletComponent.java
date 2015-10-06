package org.everit.authentication.http.basic.tests.sample;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.context.AuthenticationContext;

@Component(name = "HelloWorldServletComponent", metatype = true,
    configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
    @Property(name = "authenticationContext.target")
})
@Service(value = Servlet.class)
public class HelloWorldServletComponent extends HttpServlet {

  private static final long serialVersionUID = -5545883781165913751L;

  @Reference(bind = "setAuthenticationContext")
  private AuthenticationContext authenticationContext;

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException,
      IOException {
    long currentResourceId = authenticationContext.getCurrentResourceId();
    resp.setContentType("text/plain");
    PrintWriter out = resp.getWriter();
    out.print(currentResourceId);
  }

  public void setAuthenticationContext(final AuthenticationContext authenticationContext) {
    this.authenticationContext = authenticationContext;
  }

}
