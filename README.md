authentication-http-basic-ecm
=============================

ECM based OSGi components of the [authentication-http-basic][7] implementation.

[HTTP Basic Authentication][2] Servlet Filter based on blog post 
[Everit Authentication][1].

#Component
The component can be  instantiated multiple times via Configuration Admin. The component registers 
a *javax.servlet.Filter* OSGi service that implements 
[HTTP Basic Authentication][2]. To do that, the component is built on the 
following modules:
 - [authenticator-api][3]: to authentication the "Authorization" header value
 - [resource-resolver-api][4]: to map the authenticated username sent in the
 "Authorization" header to a Resource ID
 - [authentication-context-api][5]: to execute an authenticated process in the 
 name of the authenticated and mapped Resource ID

#Concept
Full authentication concept is available on blog post [Everit Authentication][1].
Implemented components based on this concept are listed [here][6].

[1]: http://everitorg.wordpress.com/2014/07/31/everit-authentication/
[2]: http://en.wikipedia.org/wiki/Basic_access_authentication
[3]: https://github.com/everit-org/authenticator-api
[4]: https://github.com/everit-org/resource-resolver-api
[5]: https://github.com/everit-org/authentication-context-api
[6]: http://everitorg.wordpress.com/2014/07/31/everit-authentication-implemented-and-released-2/
[7]: https://github.com/everit-org/authentication-http-basic