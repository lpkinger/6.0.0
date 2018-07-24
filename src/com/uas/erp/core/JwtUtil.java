package com.uas.erp.core;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
	private static String base64Security = "435aMe9L5itTrckY35kfcOQvPkBGZtGo";
	public static String createJWT(String username, String password) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// 生成签名密钥
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Security);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// 添加构成JWT的参数
		JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
				.claim("username", username)
				.claim("password", password)
				.signWith(signatureAlgorithm, signingKey);
		// 添加Token过期时间
		long expMillis = nowMillis + Long.parseLong("86400000");
		Date exp = new Date(expMillis);
		builder.setExpiration(exp).setNotBefore(now);
		// 生成JWT
		return builder.compact();
	}

	public static Claims parseJWT(String jsonWebToken) {
		try {
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
					.parseClaimsJws(jsonWebToken).getBody();
			return claims;
		} catch (Exception ex) {
			return null;
		}
	}
}
