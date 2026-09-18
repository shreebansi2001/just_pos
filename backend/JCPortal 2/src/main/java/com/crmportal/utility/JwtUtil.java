package com.crmportal.utility;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

	@Value("${jwt.secret:mySecretKey}")
	private String secret;

	@Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
	private Long expiration;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateToken(String email, Long userId, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userId);
		claims.put("role", role);
		return createToken(claims, email);
	}

	public String generateToken(com.crmportal.response.dto.UserMasterResponseDto user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", user.getId());
		claims.put("clientId", user.getClientId());
		claims.put("firstName", user.getFirstName());
		claims.put("lastName", user.getLastName());
		claims.put("isActive", user.getIsActive());
		claims.put("isApprove", user.getIsApprove());

		if (user.getUserBasicDetails() != null && user.getUserBasicDetails().getRole() != null) {
			claims.put("role", user.getUserBasicDetails().getRole().getName());
			claims.put("roleId", user.getUserBasicDetails().getRole().getId());
		}

		return createToken(claims, user.getEmail());
	}

	public String generateToken(com.crmportal.response.dto.PartyMasterResponseDto party) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", party.getId());
		claims.put("Name", party.getNameEnglish());

		return createToken(claims, party.getEmail());
	}
	
//	public String generateToken(VendorMasterEntity vendor) {
//		Map<String, Object> claims = new HashMap<>();
//		claims.put("userId", vendor.getId());
//		claims.put("name", vendor.getCmpName());
//
//		return createToken(claims, vendor.getContactNo());
//	}
	

	
	public Long getExpirationTime() {
		return expiration;
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Long extractUserId(String token) {
		return extractClaim(token, claims -> {
			Object userId = claims.get("userId");
			if (userId instanceof Integer) {
				return ((Integer) userId).longValue();
			}
			return (Long) userId;
		});
	}

	public String extractRole(String token) {
		return extractClaim(token, claims -> (String) claims.get("role"));
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Boolean validateToken(String token, String username) {
		final String extractedUsername = extractUsername(token);
		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}

	public Boolean isTokenValid(String token) {
		try {
			return !isTokenExpired(token);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isTokenSignatureValid(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException e) {
			return true; // signature OK
		} catch (Exception e) {
			return false;
		}
	}

	public String extractUsernameAllowExpired(String token) {
		return extractClaimAllowExpired(token, Claims::getSubject);
	}

	public <T> T extractClaimAllowExpired(String token, Function<Claims, T> resolver) {
		return resolver.apply(extractAllClaimsAllowExpired(token));
	}

	public Claims extractAllClaimsAllowExpired(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException ex) {
			return ex.getClaims();
		}
	}

	public Long extractUserIdAllowExpired(String token) {
		return extractClaimAllowExpired(token, claims -> {
			Object userId = claims.get("userId");
			if (userId instanceof Integer) {
				return ((Integer) userId).longValue();
			}
			return (Long) userId;
		});
	}
}