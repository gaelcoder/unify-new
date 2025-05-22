package com.projeto.unify.services;

import com.projeto.unify.models.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Chave secreta para assinatura do token - deve ser armazenada no application.properties
    @Value("${jwt.secret:fallbacksecretkeywhichisusedonlyifnopropertyisset}")
    private String secretKey;

    // Tempo de expiração do token - padrão 24 horas (86400000 ms)
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * Extrai o email (username) do token JWT
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai uma informação específica do token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Gera um token para o usuário (sem claims extras)
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Gera um token para o usuário com claims extras
     * Claims são informações adicionais que queremos armazenar no token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        // Se o UserDetails for um Usuario (nosso modelo), adicionar o tipo de perfil
        if (userDetails instanceof Usuario) {
            Usuario usuario = (Usuario) userDetails;
            if (!usuario.getPerfis().isEmpty()) {
                String perfil = usuario.getPerfis().iterator().next().getNome().name();
                extraClaims.put("tipo", perfil);
            }
            extraClaims.put("primeiroAcesso", usuario.isPrimeiroAcesso());
        }

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Verifica se o token é válido para o usuário
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Verifica se o token está expirado
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrai a data de expiração do token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrai todas as informações do token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtém a chave de assinatura
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}