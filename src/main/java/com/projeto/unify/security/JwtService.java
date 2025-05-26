package com.projeto.unify.security;

import com.projeto.unify.models.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String extrairUsername(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extrairTodasClaims(token);
        return claimsResolver.apply(claims);
    }

    public String gerarToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        // Add authorities to claims
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        extraClaims.put("authorities", authorities);

        if (userDetails instanceof Usuario) {
            Usuario usuario = (Usuario) userDetails;
            if (!usuario.getPerfis().isEmpty()) {
                String perfil = usuario.getPerfis().iterator().next().getNome().name();
                extraClaims.put("tipo", perfil);
            }
            extraClaims.put("primeiroAcesso", usuario.isPrimeiroAcesso());
        }
        return gerarToken(extraClaims, userDetails);
    }

    public String gerarToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValido(String token, UserDetails userDetails) {
        final String username = extrairUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpirado(token);
    }

    private boolean isTokenExpirado(String token) {
        return extrairExpiracao(token).before(new Date());
    }

    private Date extrairExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    private Claims extrairTodasClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}