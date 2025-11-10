package next.gen.consulting.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import next.gen.consulting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserRepository userRepository;

    @Value("${app.jwt.secret:superSecretKeyThatIsDefinitelyLongEnoughAndSecureForHS512_1234567890!}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:3600000}") // 1 hour
    private int jwtExpirationInMs;

    @Value("${app.jwt.refreshExpiration:604800000}") // 7 days
    private int jwtRefreshExpirationInMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return buildAccessToken(userPrincipal.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return buildAccessToken(username);
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, jwtRefreshExpirationInMs);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        if (userId instanceof Long) {
            return (Long) userId;
        }
        return null;
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    private String buildAccessToken(String username) {
        return buildToken(username, jwtExpirationInMs);
    }

    private String buildToken(String username, long expirationInMs) {
        Date expiryDate = new Date(System.currentTimeMillis() + expirationInMs);
        Date issuedAt = new Date();

        UUID userId = userRepository.findByPhone(username)
                .map(next.gen.consulting.model.User::getId)
                .orElseThrow(() -> new IllegalStateException("User not found for token generation"));

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(issuedAt)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
