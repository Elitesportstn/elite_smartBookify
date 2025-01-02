package com.elite.app.builder.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String  SECRET_KEY = "4ozzGA8YKN5x9ORl30U0fZY1IVn0WoGNVpvMgh9S3wNGIEcvpycjPA9BuEcPfxi4yAODsmISAWpQaqRM7uw";
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    public String extractUsername(String token) {
        return  extractClaim(token , Claims::getSubject);
    }

    public boolean isTokenValid(String token , UserDetails userDetails) {
        final String username = extractUsername(token);
        return  username.equals(userDetails.getUsername()) && !isTokenExpired(token)  ;
    }

    private boolean isTokenExpired(String token) {
        return  extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return  extractClaim(token ,  Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role" , userDetails.getAuthorities().toArray()[0]);
        claims.put("user" , userDetails);
        return  generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> claims , UserDetails userDetails) {
        var token =  Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+30000*60*24))
                .signWith(getSignKey() , SignatureAlgorithm.HS256)
                .compact();
        log.info("generating token " +token);
        return  token;
    }


    public <T> T extractClaim(String token , Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);

    }
}