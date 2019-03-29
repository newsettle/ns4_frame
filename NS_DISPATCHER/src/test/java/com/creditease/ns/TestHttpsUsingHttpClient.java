package com.creditease.ns;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

public class TestHttpsUsingHttpClient {
	public static void main(String[] args) throws URISyntaxException, ClientProtocolException, IOException 
	{
		ConnectionConfig connConfig = ConnectionConfig.custom().setCharset(Charset.forName("utf-8")).build();  
		SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(100000).build();  
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();  
		ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();  
		registryBuilder.register("http", plainSF);  
		//指定信任密钥存储对象和连接套接字工厂  
		try {  
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());  
			SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, new AnyTrustStrategy()).build();  
			LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
			registryBuilder.register("https", sslSF);  
		} catch (KeyStoreException e) {  
			throw new RuntimeException(e);  
		} catch (KeyManagementException e) {  
			throw new RuntimeException(e);  
		} catch (NoSuchAlgorithmException e) {  
			throw new RuntimeException(e);  
		}  
		Registry<ConnectionSocketFactory> registry = registryBuilder.build();  
		//设置连接管理器  
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);  
		connManager.setDefaultConnectionConfig(connConfig);  
		connManager.setDefaultSocketConfig(socketConfig);  
		//构建客户端  
		HttpClient client= HttpClientBuilder.create().setConnectionManager(connManager).build(); 
		
		HttpPost pm = new HttpPost();
		URIBuilder builder = new URIBuilder("https://liuyang.dispatcher.com:8043/b");
		
		Map<String,String> queryParams = new HashMap<String, String>();
		Map<String,String> formParams = new HashMap<String, String>();
		
		queryParams.put("aaa", "bbb");
		formParams.put("ppp", "hehehe");
		
		//填入查询参数
		if (queryParams!=null && !queryParams.isEmpty()){
			builder.setParameters(paramsConverter(queryParams));
		}
		pm.setURI(builder.build());
		//填入表单参数
		if (formParams!=null && !formParams.isEmpty()){
			pm.setEntity(new UrlEncodedFormEntity(paramsConverter(formParams)));
		}
		client.execute(pm);
	}

	private static List<NameValuePair> paramsConverter(Map<String, String> params){
		List<NameValuePair> nvps = new LinkedList<NameValuePair>();
		Set<Entry<String, String>> paramsSet= params.entrySet();
		for (Entry<String, String> paramEntry : paramsSet) {
			nvps.add(new BasicNameValuePair(paramEntry.getKey(), paramEntry.getValue()));
		}
		return nvps;
	}
	

	static class AnyTrustStrategy implements TrustStrategy{  

		@Override  
		public boolean isTrusted(X509Certificate[] chain, String authType) {  
			return true;  
		}  

	}  
}
