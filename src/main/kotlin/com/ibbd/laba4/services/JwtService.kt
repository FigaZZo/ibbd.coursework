package com.ibbd.laba4.services

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.core.env.Environment
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

private val logger = KotlinLogging.logger {}

@Service
class JwtService(val env: Environment) {
    private  val secretKeyString: String = env.getProperty("jwt_token") ?: throw NullPointerException("JWT token is missing")

    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray())
    private val jwtExpirationInMs: Long = 1000 * 60 * 30// 1 hour

    init {
        logger.info { "Created JWT token: $secretKey" }
    }

    fun generateToken(userDetails: UserDetails, plateId: String? = null): String {
        val claims: Map<String, Any> = hashMapOf<String, Any>("plateId" to (plateId ?: "null"))
        return createToken(claims, userDetails.username)
    }

    private fun createToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtExpirationInMs))
            .signWith(secretKey)
            .compact()
    }

    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    fun extractPlateId(token: String): String {
        return extractClaim(token) {
            it.get("plateId", String::class.java)
        }
    }

    private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims: Claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }
}