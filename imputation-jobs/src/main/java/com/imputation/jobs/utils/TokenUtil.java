package com.imputation.jobs.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TokenUtil {
    //设置过期时间半小时
    private static final long EXPIRE_DATE=120*60*1000;
    //token秘钥
    private static final String TOKEN_SECRET = "ASDGFadsfg43245asdfF2353VFDG";

    /**
     * 生成Token
     * @param reqMap
     * @return
     */
    public static String token (Map<String,String> reqMap){

        String token = "";
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis()+EXPIRE_DATE);

            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //设置头部信息
            Map<String,Object> header = new HashMap<>();
            header.put("typ","JWT");
            header.put("alg","HS256");
            //携带reqMap信息，生成签名
            JWTCreator.Builder jwtB = JWT.create().withHeader(header);
            for (String key:reqMap.keySet()) {
                jwtB.withClaim(key,reqMap.get(key));
            }
            token = jwtB.withExpiresAt(date).sign(algorithm);
        }catch (Exception e){
             log.error("生成Token异常",e);
            return  null;
        }
        log.info("生成Token="+token);
        return token;
    }

    /**
     * 获取token中信息
     * @param token
     * @param keyList
     * @return
     */

    public static Map<String,String> getMessage(String token, List<String> keyList){
        Map<String,String> resMap  = new HashMap<>();
        try {
            DecodedJWT jwt = JWT.decode(token);
            for (String key:keyList){
                resMap.put(key,jwt.getClaim(key).asString());
            }
            return resMap;

        }catch (JWTDecodeException e){
             log.error("获取token中信息异常",e);
        }
        return null;
    }

    /**
     * @desc   验证token是否正确，过期，通过返回true
     * @params [token]需要校验的串
     **/
    public static boolean verify(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        }catch (Exception e){
            log.error("验证token是否正确，过期异常",e);
            return  false;
        }
    }
    public static void main(String[] args) {
//        Map<String,String> resultMap  = new HashMap<>();
//        resultMap.put("username","zhangsan");
//        resultMap.put("password","123");
//        String token = token(resultMap);
//        System.out.println(token);
        boolean b = verify("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwYXNzd29yZCI6IjEyMyIsImV4cCI6MTY0NzQ5NTAwNSwidXNlcm5hbWUiOiJ6aGFuZ3NhbiJ9.INv6NEEqYD-dQ9bSFCx6YELy7JduH0WJuAlclaLhUn4");
        System.out.println(b);
    }
}
