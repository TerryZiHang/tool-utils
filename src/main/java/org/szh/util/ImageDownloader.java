package org.szh.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

/**
 * 根据微信图片链接获取图片到指定路径
 * 
 * @author Terry Zi
 */
public class ImageDownloader {

	private static final String USER_AGENT = "Mozilla/5.0 Firefox/26.0";
	private static Logger logger = Logger.getLogger(ImageDownloader.class);
	private static final int TIMEOUT_SECONDS = 120;
	private static final int POOL_SIZE = 120;
	private static CloseableHttpClient httpclient;

	public void initApacheHttpClient() {
		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT_SECONDS * 1000)
				.setConnectTimeout(TIMEOUT_SECONDS * 1000).build();
		httpclient = HttpClients.custom().setUserAgent(USER_AGENT).setMaxConnTotal(POOL_SIZE)
				.setMaxConnPerRoute(POOL_SIZE).setDefaultRequestConfig(defaultRequestConfig).build();
	}

	public void destroyApacheHttpClient() {
		try {
			httpclient.close();
		} catch (IOException e) {
			logger.error("httpclient close fail", e);
		}
	}

	public void fetchContent(String imageUrl, String filePath) throws ClientProtocolException, IOException {

		HttpGet httpget = new HttpGet(imageUrl);
		httpget.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
		httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		CloseableHttpResponse response = httpclient.execute(httpget);
		try {
			HttpEntity entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() >= 400) {
				throw new IOException("Got bad response, error code = " + response.getStatusLine().getStatusCode()
						+ " imageUrl: " + imageUrl);
			}
			if (entity != null) {
				InputStream input = entity.getContent();
				OutputStream output = new FileOutputStream(new File(filePath));
				IOUtils.copy(input, output);
				output.flush();
			}
		} finally {
			response.close();
		}
	}

	public static void main(String[] args) throws ClientProtocolException, IOException {
		ImageDownloader imageDownloader = new ImageDownloader();
		imageDownloader.initApacheHttpClient();

		String imageUrl = "http://thirdwx.qlogo.cn/mmopen/t2UP5IpFGVz40rIRqRT92yXY2J6LFVJa1ZKTBJLtG3UicOa6iaIjnKhybhDds1luauaP6GrsDwVy72pic0Ecaa49GPYTiakicJBTL/132";
		imageDownloader.fetchContent(imageUrl, "/backend/pictures/test.png");

		imageDownloader.destroyApacheHttpClient();
	}
}
