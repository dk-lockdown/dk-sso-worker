package com.dk.sso.worker.oauth2;

import com.dk.sso.worker.util.OAuth2Utils;

import java.io.Serializable;
import java.util.*;

public class OAuth2Request implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Resolved client ID. This may be present in the original request
     * parameters, or in some cases may be inferred by a processing class and
     * inserted here.
     */
    private String clientId;

    /**
     * Resolved scope set, initialized (by the OAuth2RequestFactory) with the
     * scopes originally requested. Further processing and user interaction may
     * alter the set of scopes that is finally granted and stored when the
     * request processing is complete.
     */
    private Set<String> scope = new HashSet<String>();

    /**
     * Map of parameters passed in to the Authorization Endpoint or Token
     * Endpoint, preserved unchanged from the original request. This map should
     * not be modified after initialization. In general, classes should not
     * retrieve values from this map directly, and should instead use the
     * individual members on this class.
     *
     * The OAuth2RequestFactory is responsible for initializing all members of
     * this class, usually by parsing the values inside the requestParmaeters
     * map.
     *
     */
    private Map<String, String> requestParameters = Collections
            .unmodifiableMap(new HashMap<String, String>());

    /**
     * Resolved resource IDs. This set may change during request processing.
     */
    private Set<String> resourceIds = new HashSet<String>();


    /**
     * The resolved redirect URI of this request. A URI may be present in the original request, in the
     * authorizationParameters, or it may not be provided, in which case it will be defaulted (by processing classes) to
     * the Client's default registered value.
     */
    private String redirectUri;

    /**
     * Resolved requested response types initialized (by the OAuth2RequestFactory) with the response types originally
     * requested.
     */
    private Set<String> responseTypes = new HashSet<String>();

    /**
     * Extension point for custom processing classes which may wish to store additional information about the OAuth2
     * request. Since this class is serializable, all members of this map must also be serializable.
     */
    private Map<String, Serializable> extensions = new HashMap<String, Serializable>();

    /**
     * Whether the request has been approved by the end user (or other process). This will be altered by the User
     * Approval Endpoint and/or the UserApprovalHandler as appropriate.
     */
    private boolean approved = false;

    public OAuth2Request(Map<String, String> requestParameters, String clientId,  boolean approved,  Set<String> scope,
                         Set<String> resourceIds, String redirectUri, Set<String> responseTypes,
                         Map<String, Serializable> extensionProperties) {
        setClientId(clientId);
        setRequestParameters(requestParameters);
        setScope(scope);
        this.approved = approved;
        if (resourceIds != null) {
            this.resourceIds = new HashSet<String>(resourceIds);
        }
        if (responseTypes != null) {
            this.responseTypes = new HashSet<String>(responseTypes);
        }
        this.redirectUri = redirectUri;
        if (extensionProperties != null) {
            this.extensions = extensionProperties;
        }
    }

    protected OAuth2Request(OAuth2Request other) {
        this(other.getRequestParameters(), other.getClientId(), other.approved, other
                .getScope(), other.getResourceIds(), other.getRedirectUri(), other.getResponseTypes(), other
                .getExtensions());
    }

    protected OAuth2Request(String clientId) {
        setClientId(clientId);
    }

    protected OAuth2Request() {
        super();
    }

    public String getClientId() {
        return clientId;
    }

    public Set<String> getScope() {
        return scope;
    }

    /**
     * Warning: most clients should use the individual properties of this class,
     * such as {{@link #getScope()} or { {@link #getClientId()}, rather than
     * retrieving values from this map.
     *
     * @return the original, unchanged set of request parameters
     */
    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public Set<String> getResponseTypes() {
        return responseTypes;
    }

    public Set<String> getResourceIds() {
        return resourceIds;
    }

    public Map<String, Serializable> getExtensions() {
        return extensions;
    }

    public boolean isApproved() {
        return approved;
    }

    /**
     * Update the request parameters and return a new object with the same properties except the parameters.
     * @param parameters new parameters replacing the existing ones
     * @return a new OAuth2Request
     */
    public OAuth2Request createOAuth2Request(Map<String, String> parameters) {
        return new OAuth2Request(parameters, getClientId(), approved, getScope(), resourceIds,
                redirectUri, responseTypes, extensions);
    }

    /**
     * Update the scope and create a new request. All the other properties are the same (including the request
     * parameters).
     *
     * @param scope the new scope
     * @return a new request with the narrowed scope
     */
    public OAuth2Request narrowScope(Set<String> scope) {
        OAuth2Request request = new OAuth2Request(getRequestParameters(), getClientId(), approved, scope,
                resourceIds, redirectUri, responseTypes, extensions);
        return request;
    }

    /**
     * Tries to discover the grant type requested for the token associated with this request.
     *
     * @return the grant type if known, or null otherwise
     */
    public String getGrantType() {
        if (getRequestParameters().containsKey(OAuth2Utils.GRANT_TYPE)) {
            return getRequestParameters().get(OAuth2Utils.GRANT_TYPE);
        }
        if (getRequestParameters().containsKey(OAuth2Utils.RESPONSE_TYPE)) {
            String response = getRequestParameters().get(OAuth2Utils.RESPONSE_TYPE);
            if (response.contains("token")) {
                return "implicit";
            }
        }
        return null;
    }


    protected void setScope(Collection<String> scope) {
        if (scope != null && scope.size() == 1) {
            String value = scope.iterator().next();
            /*
             * This is really an error, but it can catch out unsuspecting users
             * and it's easy to fix. It happens when an AuthorizationRequest
             * gets bound accidentally from request parameters using
             * @ModelAttribute.
             */
            if (value.contains(" ") || value.contains(",")) {
                scope = OAuth2Utils.parseParameterList(value);
            }
        }
        this.scope = Collections
                .unmodifiableSet(scope == null ? new LinkedHashSet<String>()
                        : new LinkedHashSet<String>(scope));
    }

    protected void setRequestParameters(Map<String, String> requestParameters) {
        if (requestParameters != null) {
            this.requestParameters = Collections
                    .unmodifiableMap(new HashMap<String, String>(requestParameters));
        }
    }

    protected void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
