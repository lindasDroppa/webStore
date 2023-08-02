package Control;

import io.jsonwebtoken.*;



public class SecurityFilter
{



    public SecurityFilter() {
    }

    public final String key="123456";

    public String createJWTToken(String email,String password){

        String jws = Jwts.builder().setSubject(password)
                .setIssuer(email)
                .signWith(SignatureAlgorithm.HS256,key).compact();


        return jws;
    }

    public String verifyJWTToken(String token){


        try {
            Jwt jjwts=   Jwts.parser().setSigningKey(key).parse(token);

            return jjwts.getBody().toString();

        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException expiredJwtException) {
        }

        return "Failed";


    }

    public String getSubject(String token){
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    }


    public String getIssuer(String token){
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getIssuer();
    }

    /**
     * @param token 
     * @return
     */
    public boolean isValid(String token){

        try {
            Jwt jjwts=   Jwts.parser().setSigningKey(key).parse(token);



            return jjwts!=null;

        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException expiredJwtException) {
            return false;
        }


    }
    public String encryptPassword(String password){


        return null;
    }

    public String decryptPassword(String password){
        return null;
    }




}
