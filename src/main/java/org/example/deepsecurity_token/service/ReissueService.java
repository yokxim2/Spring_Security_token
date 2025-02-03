package org.example.deepsecurity_token.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import org.example.deepsecurity_token.exception.CustomJwtException;
import org.example.deepsecurity_token.jwt.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ReissueService {

    private final JWTUtil jwtUtil;

    public ReissueService(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String[] reissue(Cookie[] cookies) {

        //get refresh token
        String refresh = null;
        String[] tokens = new String[2];

        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            //response status code
            throw new CustomJwtException("Refresh token is missing", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            throw new CustomJwtException("Refresh token is expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            throw new CustomJwtException("Refresh token is invalid", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        tokens[0] = newAccess;
        tokens[1] = newRefresh;

        return tokens;
    }
}
