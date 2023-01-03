package my.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import my.entity.ResultEnum;
import my.exception.MyException;

public class SomeMethod {
	
	private static final Logger log = LoggerFactory.getLogger(SomeMethod.class);
	
	public static String getUsername() throws Exception {
		String username = null;
		Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        	throw new MyException(ResultEnum.ACCESS_FAILURE);
        try {
        	username = subject.getPrincipal().toString();
        }
        catch (Exception e) {
			throw new MyException(ResultEnum.ACCESS_FAILURE);
		}
        return username;
	}
	
	public static String restMultipartRequest(String url, MultiValueMap<String, Object> map, String token) throws Exception {
    	RestTemplate restTemplate = new RestTemplate();
    	ResponseEntity<String> responseEntity = null;
    	String res = null;
    	JSONObject resJson = null;
    	try {
    		HttpHeaders headers = new HttpHeaders();
    		headers.add("Content-Type", "multipart/form-data");
    		if(token != null)
    			headers.add("Authorization", token);
    		
    		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
    		responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
    		map.add("url", url);
            res =  responseEntity.getBody();
    		log.debug("Service call sueecss");
    		log.debug("Service call sueecss: errorCode={}, params={}, resutl={}", "0", JSON.toJSONString(map), responseEntity);
        } catch(Exception e){
    		map.add("url", url);
    		log.error("Service call failed: errorCode={}, params={}", ResultEnum.HTTPREQUEST_FAILURE.getCode(), JSON.toJSONString(map));
        	throw new MyException(ResultEnum.HTTPREQUEST_FAILURE);
        }
    	try {
    		resJson = JSON.parseObject(res);
    	} catch(Exception e){
    		log.error("Service call failed: errorCode={}, params={}", ResultEnum.HTTPRES_FAILURE.getCode(), JSON.toJSONString(res));
        	throw new MyException(ResultEnum.HTTPRES_FAILURE);
        }
    	
		if(!resJson.containsKey("data") || !resJson.containsKey("code")  || !resJson.getString("code").toString().contentEquals("0")) {
			log.error("Service call failed: errorCode={}, params={}", ResultEnum.HTTPRES_FAILURE.getCode(), JSON.toJSONString(resJson));
			throw new MyException(ResultEnum.HTTPRES_FAILURE);
		}
		return (String) resJson.get("data");
    }
	
    public static String restRequest(String url, Map<String, Object> map, String token, String username, String password) throws Exception {
    	SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(555000);
        requestFactory.setReadTimeout(555000);
        
    	RestTemplate restTemplate = new RestTemplate();
    	restTemplate.setRequestFactory(requestFactory);
    	
    	JSONObject jsonObject = null;
    	ResponseEntity<String> responseEntity = null;
    	HttpEntity<String> httpEntity = null;
    	try {
    		HttpHeaders headers = new HttpHeaders();
    		headers.add("Content-Type", "application/json");
    		if(token != null)
    			headers.add("Authorization", token);
    		if(username != null)
    			headers.add("Username", username);
    		if(password != null)
    			headers.add("Password", password);
    		if(map != null) {
    			jsonObject = new JSONObject(map);
        		httpEntity = new HttpEntity<String>(jsonObject.toString(), headers);
    		}
    		else {
    			httpEntity = new HttpEntity<String>(null, headers);
    		}
    		responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
            return responseEntity.getBody();
        }catch(Exception e){
        	throw new MyException(ResultEnum.HTTPREQUEST_FAILURE);
        }
    }
    
    public static String restRequestJson(String url, String JsonStr, String token, String username, String password) throws Exception {
    	RestTemplate restTemplate = new RestTemplate();
    	ResponseEntity<String> responseEntity = null;
    	HttpEntity<String> httpEntity = null;
    	try {
    		HttpHeaders headers = new HttpHeaders();
    		headers.add("Content-Type", "application/json");
    		if(token != null)
    			headers.add("Authorization", token);
    		if(username != null)
    			headers.add("Username", username);
    		if(password != null)
    			headers.add("Password", password);

        	httpEntity = new HttpEntity<String>(JsonStr, headers);
    		responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
            return responseEntity.getBody();
        }catch(Exception e){
        	throw new MyException(ResultEnum.HTTPREQUEST_FAILURE);
        }
    }
    
    public static String restMultiRequest(String url, MultiValueMap<String, Object> map, Hashtable<String, String> headermap, String token) throws Exception {
    	SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(555000);
        requestFactory.setReadTimeout(555000);
        
    	RestTemplate restTemplate = new RestTemplate();
    	restTemplate.setRequestFactory(requestFactory);
    	
    	ResponseEntity<String> responseEntity = null;
    	HttpEntity<MultiValueMap<String, Object>> httpEntity = null;
    	try {
    		HttpHeaders headers = new HttpHeaders();
    		for(String key : headermap.keySet())
    			headers.add(key, headermap.get(key));//"Content-Type", "multipart/form-data"
    		if(token != null)
    			headers.add("Authorization", token);
        	httpEntity = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
    		responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
            return responseEntity.getBody();
        }catch(Exception e){
        	throw new MyException(ResultEnum.HTTPREQUEST_FAILURE);
        }
    }
    
    public static String restGetRequest(String url, MultiValueMap<String, Object> map, String token){
    	String  responseEntity = null;
        RestTemplate restTemplate = new RestTemplate();
        //设置Http Header
        HttpHeaders headers = new HttpHeaders();
        if(token != null)
			headers.add("Authorization", token);
        
        //封装请求头
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<MultiValueMap<String, Object>>(headers);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>( headers);

        if(!CollectionUtils.isEmpty(map)) {
        	url = url + "?";
        	
        	Set<String> keySet = map.keySet();
        	for (String key : keySet) {
        		String value = map.get(key).get(0).toString();	 
        		url = url + key;
        		url = url + "=";
        		url = url + value;
        	}
        }
        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        
       String res1 =  restTemplate.getForObject(url, String.class, httpEntity);
        
        return res.getBody();
    }
        
    public static String getHash(String fileName,String hashType) throws Exception {
    	
    	File f = new File(fileName);  	
    	InputStream ins = new FileInputStream(f);
    	byte[] buffer = new byte[8192];
    	
    	MessageDigest md5 = MessageDigest.getInstance(hashType);
    	
    	int len;
    	
    	while((len = ins.read(buffer)) != -1){
             md5.update(buffer, 0, len);
        }
    	
    	ins.close();
        return DigestUtils.md5Hex(md5.digest());
    }
}
