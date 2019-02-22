package com.ventia.homework.model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.util.Base64;

import org.apache.camel.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ventia.homework.exception.InvalidTokenException;
import com.ventia.homework.exception.InvalidTokenPublicKeyException;
import com.ventia.homework.exception.NullTokenException;
import com.ventia.homework.stubs.AzureAdKeyListStub;
import com.ventia.homework.stubs.AzureAdKeyLocationStub;

/**
 * 
 * @author Cornelius Franken This class is used to store and work with a JWT
 *         token from Azure.
 */
public class AzureAdJwtToken {

	Logger LOGGER = LoggerFactory.getLogger(AzureAdJwtToken.class);

	public String token;

	private DecodedJWT decodedToken;
	// Header
	public String x5t;
	public String kid;

	// Payload
	public String uniqueName;
	public String issuer;
	public PublicKey publicKey;
	public String openidConfigURL;// "https://login.microsoftonline.com/common/.well-known/openid-configuration"
	public boolean useStubs;

	public AzureAdJwtToken() {
		LOGGER.debug("Constructed AD JWT Token");
	}

	/***
	 * This method will decode and store some information we need for a Base64
	 * encoded Azure JWT Token.
	 * 
	 * @param token
	 * @throws InvalidTokenException
	 * @throws NullTokenException
	 * 
	 */
	public void decodeToken(@Header("jwttoken") String fromToken) throws InvalidTokenException, NullTokenException {
		if (null != fromToken || fromToken.length() > 0) {
			// eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Ik5HVEZ2ZEstZnl0aEV1THdqcHdBSk9NOW4tQSJ9.eyJhdWQiOiJodHRwczovL3NlcnZpY2UuY29udG9zby5jb20vIiwiaXNzIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvN2ZlODE0NDctZGE1Ny00Mzg1LWJlY2ItNmRlNTdmMjE0NzdlLyIsImlhdCI6MTM4ODQ0MDg2MywibmJmIjoxMzg4NDQwODYzLCJleHAiOjEzODg0NDQ3NjMsInZlciI6IjEuMCIsInRpZCI6IjdmZTgxNDQ3LWRhNTctNDM4NS1iZWNiLTZkZTU3ZjIxNDc3ZSIsIm9pZCI6IjY4Mzg5YWUyLTYyZmEtNGIxOC05MWZlLTUzZGQxMDlkNzRmNSIsInVwbiI6ImZyYW5rbUBjb250b3NvLmNvbSIsInVuaXF1ZV9uYW1lIjoiZnJhbmttQGNvbnRvc28uY29tIiwic3ViIjoiZGVOcUlqOUlPRTlQV0pXYkhzZnRYdDJFYWJQVmwwQ2o4UUFtZWZSTFY5OCIsImZhbWlseV9uYW1lIjoiTWlsbGVyIiwiZ2l2ZW5fbmFtZSI6IkZyYW5rIiwiYXBwaWQiOiIyZDRkMTFhMi1mODE0LTQ2YTctODkwYS0yNzRhNzJhNzMwOWUiLCJhcHBpZGFjciI6IjAiLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJhY3IiOiIxIn0.JZw8jC0gptZxVC-7l5sFkdnJgP3_tRjeQEPgUn28XctVe3QqmheLZw7QVZDPCyGycDWBaqy7FLpSekET_BftDkewRhyHk9FW_KeEz0ch2c3i08NGNDbr6XYGVayNuSesYk5Aw_p3ICRlUV1bqEwk-Jkzs9EEkQg4hbefqJS6yS1HoV_2EsEhpd_wCQpxK89WPs3hLYZETRJtG5kvCCEOvSHXmDE6eTHGTnEgsIk--UlPe275Dvou4gEAwLofhLDQbMSjnlV5VLsjimNBVcSRFShoxmQwBJR_b2011Y5IuD6St5zPnzruBbZYkGNurQK63TJPWmRd3mbJsGM0mf3CUQ
			this.token = fromToken;
			try {
				LOGGER.debug("Attempting to decode token:" + token);
				this.decodedToken = JWT.decode(token);

			} catch (JWTDecodeException jwt) {
				LOGGER.error("error thrown in decoding of token:{}", jwt);
				throw new InvalidTokenException("Could not decode Base 64 Token");
			}
			LOGGER.debug("Decoded token details follows:");
			LOGGER.debug("---------TOKEN HEADER START---------");
			LOGGER.debug("Type of token:{} ", decodedToken.getType());
			LOGGER.debug("Signature/Encryption Algorithm:{} ", decodedToken.getAlgorithm());
			LOGGER.debug("X506 Fingerprint:{} ", decodedToken.getHeaderClaim("x5t").asString());
			LOGGER.debug("Key Id:{} ", decodedToken.getKeyId());
			LOGGER.debug("------------------------------------");
			x5t = decodedToken.getHeaderClaim("x5t").asString();
			if (null == decodedToken.getKeyId()) {
				throw new InvalidTokenException("Cannot find the Key ID it cannot be null for Azure AD Token.");
			} else {

				kid = decodedToken.getKeyId();
			}
			// Payload
			// reserved, public, and private claims.
			LOGGER.debug("---------TOKEN PAYLOAD START---------");
			decodedToken.getAudience().forEach(aud -> {
				LOGGER.debug("Audience = ", aud);
			});
			LOGGER.debug("Issuer = " + decodedToken.getIssuer());
			LOGGER.debug("Unique Name = " + decodedToken.getClaim("unique_name"));
			LOGGER.debug("--------------------------------------");
			uniqueName = decodedToken.getClaim("unique_name").asString();
			issuer = decodedToken.getIssuer();
		} else {
			throw new NullTokenException();
		}
	}

	/**
	 * This is how to verify an key in AzureAD 1. go to here:
	 * https://login.microsoftonline.com/common/.well-known/openid-configuration 2.
	 * check the value of "jwks_uri", which is
	 * "https://login.microsoftonline.com/common/discovery/keys" 3. go to
	 * https://login.microsoftonline.com/common/discovery/keys 4. get "kid" value
	 * from header 5. search kid in key file to get the key.
	 * 
	 * @throws IOException
	 * @throws CertificateException
	 */
	protected PublicKey loadPublicKey() throws IOException, CertificateException {

		LOGGER.debug("Attempting to connect to: {}", this.openidConfigURL);

		String openidConfigStr = "";
		if (this.useStubs) {
			LOGGER.debug("Using Stub Methods.");
			openidConfigStr = AzureAdKeyLocationStub.STUB_RESPONSE;
		} else {
			
			openidConfigStr = readUrl(this.openidConfigURL);
		}
		LOGGER.debug("Retrieved Information: {}", openidConfigStr);
		JSONObject openidConfig = new JSONObject(openidConfigStr);
		String jwksUri = openidConfig.getString("jwks_uri");
		LOGGER.debug("Attempting to get public key list from:{}", jwksUri);
		String jwkConfigStr = "";
		if (this.useStubs) {
			LOGGER.debug("Using Stub Methods.");
			 jwkConfigStr = AzureAdKeyListStub.STUB_RESPONSE;
		} else {
			 jwkConfigStr = readUrl(jwksUri);
		}

		LOGGER.debug("Public Key List Found:{} ", jwkConfigStr);
		JSONObject jwkConfig = new JSONObject(jwkConfigStr);

		JSONArray keys = jwkConfig.getJSONArray("keys");
		for (int i = 0; i < keys.length(); i++) {
			JSONObject key = keys.getJSONObject(i);

			String currentKid = key.getString("kid");
			String x5c = key.getJSONArray("x5c").getString(0);

			String keyStr = "-----BEGIN CERTIFICATE-----\r\n";
			String tmp = x5c;
			while (tmp.length() > 0) {
				if (tmp.length() > 64) {
					String x = tmp.substring(0, 64);
					keyStr += x + "\r\n";
					tmp = tmp.substring(64);
				} else {
					keyStr += tmp + "\r\n";
					tmp = "";
				}
			}
			keyStr += "-----END CERTIFICATE-----\r\n";
			LOGGER.debug("Listing Certificate For:{}", kid);
			LOGGER.debug(keyStr);

			if (this.kid.equals(currentKid)) {
				LOGGER.info("Found Key ID {}: in the json list.");
				LOGGER.info("Using The Following Public Key To verify Token: {}", keyStr);
				PublicKey publicKey = extractMatchedCertificate(keyStr);

				return publicKey;
			}
		}
		return null;
	}

	private PublicKey extractMatchedCertificate(String keyStr) throws CertificateException {
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		InputStream stream = new ByteArrayInputStream(keyStr.getBytes(StandardCharsets.US_ASCII));
		X509Certificate cer = (X509Certificate) fact.generateCertificate(stream);
		PublicKey publicKey = cer.getPublicKey();
		return publicKey;
	}

	/***
	 * This is a helper function used to read all JSON data from a URL
	 * 
	 * @param url
	 * @return A String containing JSON data,
	 * @throws IOException
	 */
	private String readUrl(String url) throws IOException {
		// TODO: cache content of json key content to speed up access i.e. dont need to
		// access internet the whole time.

		System.setProperty("http.proxyHost", "bh-app-proxy.corp.dmz");
		System.setProperty("http.proxyPort", "8080");

		URL addr = new URL(url);
		StringBuilder sb = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(addr.openStream()))) {
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
		}
		return sb.toString();
	}

	/***
	 * 
	 * @throws InvalidTokenPublicKeyException
	 * @throws InvalidTokenException
	 */
	public void verify() throws InvalidTokenPublicKeyException, InvalidTokenException {

		this.publicKey = null;
		try {
			publicKey = loadPublicKey();
			if (null == publicKey) {
				LOGGER.warn("No Key Found  For KeyId: {} Throwing My Toys Out Of The Cot!", this.kid);
				throw new InvalidTokenPublicKeyException(
						"No public key found with the specified key id of:" + this.kid);
			}
		} catch (CertificateException certEx) {
			// TODO Auto-generated catch block
			LOGGER.error("Issue With Certificate Generation:{}", certEx);
			throw new InvalidTokenPublicKeyException(
					"Cannot generate a public key certiticate for key id of:" + this.kid);
		} catch (IOException ioEx) {

			LOGGER.error("An IO Exception was thrown something went wrong with connectivity most probably details are:",
					ioEx);
			throw new InvalidTokenPublicKeyException(
					"Cannot connecto Azure Key Servers To Find Specified Key Id Of:" + this.kid);
		}

		try {
			JWTVerifier verifier = JWT.require(Algorithm.RSA256((RSAKey) publicKey)).withIssuer(issuer).build();
			DecodedJWT jwt = verifier.verify(token);
		} catch (JWTVerificationException exception) {
			LOGGER.error("Verification exception was{}:", exception);
			throw new InvalidTokenException(exception.getMessage());
		}
		LOGGER.info("Token was validated and can be used.");

	}

	public String getUniqueName() {
		return uniqueName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public DecodedJWT getDecodedToken() {
		return decodedToken;
	}

	public void setDecodedToken(DecodedJWT decodedToken) {
		this.decodedToken = decodedToken;
	}

	public String getX5t() {
		return x5t;
	}

	public void setX5t(String x5t) {
		this.x5t = x5t;
	}

	public String getKid() {
		return kid;
	}

	public void setKid(String kid) {
		this.kid = kid;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public String getOpenidConfigURL() {
		return openidConfigURL;
	}

	public void setOpenidConfigURL(String openidConfigURL) {
		this.openidConfigURL = openidConfigURL;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	@Override
	public String toString() {
		return "AzureAdJwtToken [issuer=" + issuer + ", uniqueName=" + uniqueName + "]";
	}

	public boolean isUseStubs() {
		return useStubs;
	}

	public void setUseStubs(boolean useStubs) {
		this.useStubs = useStubs;
	}
}
