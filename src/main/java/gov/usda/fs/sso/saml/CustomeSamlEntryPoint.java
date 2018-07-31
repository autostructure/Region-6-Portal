package main.java.gov.usda.fs.sso.saml;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.websso.WebSSOProfileOptions;

/**
 * THe entry point for all SAML communcations.
 */
public class CustomeSamlEntryPoint  extends SAMLEntryPoint {
   /**
    * Do the local relay state dance.
    */
  private String localRelayState;

  /**
   * Commence the commencing.
   *
   * @param request Standard Servlet request.
   * @param response Standard Servlet response.
   * @param authenticationException Standard Servlet authenticationException.
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public final void commence(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final AuthenticationException authenticationException)
    throws IOException, ServletException {

    //read your request parameter
    System.out.println("Setting RelayState======"
     + request.getParameter("relayState"));

    setRelayState(request.getParameter("relayState"));

    super.commence(request, response, authenticationException);
  }

  /**
   * Gets the options.
   *
   * @param samlMessageContext Standard Servlet request.
   * @param authenticationException Standard Servlet response.
   * @throws MetadataProviderException
   */
  @Override
  protected final WebSSOProfileOptions getProfileOptions(
    final SAMLMessageContext samlMessageContext,
    final AuthenticationException authenticationException)
    throws MetadataProviderException {
    //set the relayState to your SAML message context
    samlMessageContext.setRelayState(getRelayState());

    return super.getProfileOptions(samlMessageContext, authenticationException);
  }

  /**
   * @param relayState The state of the relay.
   */
  private void setRelayState(final String relayState) {
    localRelayState = relayState;
  }

  /**
   * @return the state of the relay.
   */
  private String getRelayState() {
    return localRelayState;
  }
}
