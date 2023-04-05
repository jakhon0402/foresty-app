package uz.platform.forestyapp.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;


@Component
@Slf4j
public class JwtProvider {

    @Value("${foresty.app.jwtExpirationMs}")
    private long accessTokenExpirationMs;

    @Value("${foresty.app.jwtRefreshExpirationMs}")
    private long refreshTokenExpirationMs;

    @Value("${foresty.app.accessTokenKey}")
    private String accessTokenKey;

    @Value("${foresty.app.refreshTokenKey}")
    private String refreshTokenKey;

    public String generateAccessToken(String username){
        Date expireDate = new Date(System.currentTimeMillis() + accessTokenExpirationMs);

        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, accessTokenKey)
                .compact();
    }

    public String generateRefreshToken(String username, UUID refreshTokenId){
        Date expireDate = new Date(System.currentTimeMillis() + refreshTokenExpirationMs);
        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, refreshTokenKey)
                .claim("tokenId",refreshTokenId.toString())
                .compact();
    }

    public boolean validateAccessToken(String token){
        return validateToken(token,accessTokenKey);
    }

    public boolean validateRefreshToken(String token){
        return validateToken(token,refreshTokenKey);
    }


    private boolean validateToken(String token,String key){
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    public String getUsernameFromAccessToken(String token){
        return Jwts
                .parser().setSigningKey(accessTokenKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String getUsernameFromRefreshToken(String token){
        return Jwts
                .parser().setSigningKey(refreshTokenKey).parseClaimsJws(token).getBody().getSubject();
    }

    public UUID getRefreshTokenIdFromRefreshToken(String token){
        log.info(Jwts.parser().setSigningKey(refreshTokenKey).parseClaimsJws(token).getBody().get("tokenId").toString());
        return UUID.fromString(Jwts.parser().setSigningKey(refreshTokenKey).parseClaimsJws(token).getBody().get("tokenId").toString());
    }
}
