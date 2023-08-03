package Control;

import io.jsonwebtoken.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


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

    public boolean isVerified(String token){

        try {
            Jwt jjwts=   Jwts.parser().setSigningKey(key).parse(token);

            return jjwts!=null;

        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException expiredJwtException) {
            return false;
        }


    }
    private static final String ALGORITHM = "AES";
    private static final String KEY_VALUE = "1Hbfh667adfDEJ78";


    public  String encrypt(String value) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(KEY_VALUE.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedValue = cipher.doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(encryptedValue);
    }

    public  String decrypt(String value) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(KEY_VALUE.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedValue = Base64.getDecoder().decode(value.getBytes());
        byte[] decryptedValue = cipher.doFinal(decodedValue);
        return new String(decryptedValue);
    }




}
