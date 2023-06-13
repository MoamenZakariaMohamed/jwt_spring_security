package com.santechture.api.configuration;
import com.santechture.api.entity.Admin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.net.BindException;
import java.security.Key;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;


import static java.time.temporal.ChronoUnit.HOURS;

@AllArgsConstructor
@Service
public class JwtService {

    private static final String SECRET_KEY = "3778214125442A472D4B6150645267556B58703273357638792F423F4528482B";
    private final Set<String> blacklistedTokens = new HashSet<>();

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimType) {
        final Claims claims = extractAllClaims(token);
        return claimType.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(Map<String, Objects> extraClaims, Admin userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(
                        Date.from(
                                Instant.now().plus(1, HOURS)
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }



    public String generateToken(Admin userDetails) {
        return generateToken(Collections.emptyMap(),userDetails);
    }


    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public Boolean isTokenValid(String token, Admin userDetails) throws BindException {
        if (isTokenBlacklisted(token)) {
            throw new BindException("Your Token has been Blocked You need to Login Again");
        }

        String tokenUsername = extractUserName(token);
        return tokenUsername.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public void logout(String token) {
        blacklistToken(token);
    }

}
