package com.lwx.core.utils.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;

/**
 * @Author liuax01
 * @Date 2018/1/15 16:41
 */
public class HttpRequestUtils {

	private static Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);

	private static int CONNCTION_TIME_OUT_MS = 2000;
	private static int READ_TIME_OUT_MS = 8000;

	public static String doPost(String url,String data,int connectionTimeOutMs,int readTimeOutMs) throws Exception {
		ContentType contentType = ContentType.APPLICATION_JSON;
		StringEntity stringEntity = new StringEntity(data,contentType);
		HttpResponse httpResponse = post(url,stringEntity,connectionTimeOutMs,readTimeOutMs,null);
		return entityTostring(httpResponse,"utf-8");
	}

	private static String entityTostring(HttpResponse httpRespose, String charset) throws Exception{
		//如果服务器返回信息出错
		HttpEntity resEntity = httpRespose.getEntity();
		String msg = EntityUtils.toString(httpRespose.getEntity(),charset);
		if(httpRespose.getStatusLine().getStatusCode() != 200){
			logger.error("状成码[{}],错误信息[{}]",httpRespose.getStatusLine().getStatusCode(),msg);
			throw new Exception(String.format("状成码[%s],错误信息[%s]",httpRespose.getStatusLine().getStatusCode(),msg));
		}
		try {
			return EntityUtils.toString(httpRespose.getEntity(),charset);
		}catch (Exception e) {
			logger.error("解析http response异常");
			throw e;
		}
	}

	public static HttpResponse post(String url,HttpEntity httpEntity,int connectionTimeOutMs,int readTimeOutMs,CertConfig config) throws Exception{
		Exception exception = null;
		try {
			return postOnce(url,httpEntity,connectionTimeOutMs,readTimeOutMs,config);
		} catch (UnknownHostException e) {
			logger.error("UnknownHostException for url {}", url);
			exception  = e;
		} catch (ConnectTimeoutException e) {
			logger.error("connect timeout happened for url {}", url);
			exception  = e;
		} catch (SocketTimeoutException e) {
			logger.error("socket timeout happened for url {}", url);
			exception  = e;
		} catch (Exception e) {
			logger.error("unknow exception:" + e.getMessage());
			exception  = e;
		}
		throw exception;
	}

	private static HttpResponse postOnce(String url,HttpEntity httpEntity,int connectionTimeOutMs,int readTimeOutMs,CertConfig config) throws Exception {
		BasicHttpClientConnectionManager connManager;
		if (null != config) {
			// 证书
			char[] password = config.getPassword().toCharArray();
			InputStream certStream = config.getCertStream();
			KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(certStream, password);

			// 实例化密钥库 & 初始化密钥工厂
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, password);

			// 创建 SSLContext
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());

			SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
					sslContext,
					new String[]{"TLSv1"},
					null,
					new DefaultHostnameVerifier());

			connManager = new BasicHttpClientConnectionManager(
					RegistryBuilder.<ConnectionSocketFactory>create()
							.register("http", PlainConnectionSocketFactory.getSocketFactory())
							.register("https", sslConnectionSocketFactory)
							.build(),
					null,
					null,
					null
			);
		} else {
			connManager = new BasicHttpClientConnectionManager(
					RegistryBuilder.<ConnectionSocketFactory>create()
							.register("http", PlainConnectionSocketFactory.getSocketFactory())
							.register("https", SSLConnectionSocketFactory.getSocketFactory())
							.build(),
					null,
					null,
					null
			);
		}

		HttpClient httpClient = HttpClientBuilder.create()
				.setConnectionManager(connManager)
				.build();
		HttpPost httpPost = new HttpPost(url);

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeOutMs).setConnectTimeout(connectionTimeOutMs).build();
		httpPost.setConfig(requestConfig);
		//StringEntity postEntity = new StringEntity(data, "UTF-8");
		//httpPost.addHeader("Content-Type", "application/json");
		httpPost.setEntity(httpEntity);
		HttpResponse httpResponse = httpClient.execute(httpPost);
		//HttpEntity resEntity = httpResponse.getEntity();
		return httpResponse;
	}

}
