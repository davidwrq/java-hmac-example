package com.example.demo;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.codec.binary.Hex;
public class DemoApplication {
	  public static void main(String[] args) {
		String key = "{secret-key}";
		String secret = "{secret-secret}";
		String requestMethod = "GET";
		Date currentDate = new java.util.Date();
		String date = "" + currentDate.getTime();
		String requestServer = "{URL_HERE}";
		String requestPath = "/payments/provider/check/{id_here}/"; 
		String encryptBase = key + "&" + date + "&" + requestMethod + "&" + requestPath;
		String HashMCA = null;
	    try {
			URLParamEncoder encoder= new URLParamEncoder();
	    	encryptBase = encoder.encode(encryptBase);
	    	System.out.println("PROVIDER-KEY: " + key);
			System.out.println("MESSAGE-DATE: " + date);
			System.out.println("ENCRYPT_BASE: " + encryptBase);
			HashMCA = encodeHmacSha256(secret, encryptBase);
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
			  .url(requestServer + requestPath)
			  .method("GET", null)
			  .addHeader("message-hash", HashMCA)
			  .addHeader("message-date", date)
			  .addHeader("provider-key", key)
			  .build();
			Response response = client.newCall(request).execute();
			System.out.println("HASH SENT: " + HashMCA);
			System.out.println("RESPONSE CODE: " + response.code());
			System.out.println("RESPONSE BODY: " + response.body().string());
	      } catch (Exception e) {
				e.printStackTrace();
	      }
	}
	private static String encodeHmacSha256(String secret, String data) throws Exception {
		 Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		 SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
		 sha256_HMAC.init(secret_key);
		 return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes()));
	}
	public static class URLParamEncoder {
	    public String encode(String input) {
	        StringBuilder resultStr = new StringBuilder();
	        for (char ch : input.toCharArray()) {
	            if (isUnsafe(ch)) {
	                resultStr.append('%');
	                resultStr.append(toHex(ch / 16));
	                resultStr.append(toHex(ch % 16));
	            } else {
	                resultStr.append(ch);
	            }
	        }
	        return resultStr.toString();
	    }
	    private char toHex(int ch) {
	        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
	    }
	    private boolean isUnsafe(char ch) {
	        if (ch > 128 || ch < 0)
	            return true;
	        return " %$+,/:;?@<>#%".indexOf(ch) >= 0;
	    }
	}
}