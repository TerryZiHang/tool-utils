package org.szh.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.szh.bean.KdResult;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KdQuearyUtils {

	/** 实时查询请求地址 */
	private static final String SYNQUERY_URL = "http://poll.kuaidi100.com/poll/query.do";

	/** 授权key 自己填写*/
	private static final String key = "此处填写公司申请的授权key";

	/** 公司编号 自己填写*/
	private static final String customer = "此处填写给与的公司编码";

	/**
	 * 实时查询物流信息并解析数据返回
	 * 
	 * @param com
	 *            快递公司编码
	 * @param num
	 *            快递单号
	 * @return
	 */
	public static KdResult queryData(String com, String num) throws Exception {
		return queryData(com, num, "", "", "");

	}

	/**
	 * 实时查询物流信息并解析数据返回
	 * 
	 * @param com
	 *            快递公司编码
	 * @param num
	 *            快递单号
	 * @param phone
	 *            手机号码后四位
	 * @param from
	 *            出发地城市
	 * @param to
	 *            目的地城市
	 * @return
	 * @throws Exception
	 */
	public static KdResult queryData(String com, String num, String phone, String from, String to) throws Exception {
		String result = KdQueryData(com, num, phone, from, to, 0);
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		KdResult kdResult = mapper.readValue(result, KdResult.class);
		return kdResult;
	}

	/**
	 * 根据快递公司编码和快递单号实时查询物流
	 * 
	 * @param com
	 *            快递公司编码
	 * @param num
	 *            快递单号
	 * @return
	 */
	public static String KdQueryData(String com, String num) {
		// 做测试环境使用
		// String result =
		// "{\"message\":\"ok\",\"nu\":\"776000756349705\",\"ischeck\":\"0\",\"condition\":\"00\",\"com\":\"shentong\",\"status\":\"200\",\"state\":\"0\",\"data\":[{\"time\":\"2019-09-04
		// 09:57:06\",\"ftime\":\"2019-09-04
		// 09:57:06\",\"context\":\"广东深圳福田转运中心-已装袋发往-湖北武昌转运中心\"},{\"time\":\"2019-09-04
		// 09:57:06\",\"ftime\":\"2019-09-04
		// 09:57:06\",\"context\":\"广东深圳福田转运中心-已进行装车扫描\"},{\"time\":\"2019-09-04
		// 08:51:26\",\"ftime\":\"2019-09-04
		// 08:51:26\",\"context\":\"广东深圳福田转运中心-已发往-湖北武昌转运中心\"},{\"time\":\"2019-09-04
		// 08:51:26\",\"ftime\":\"2019-09-04
		// 08:51:26\",\"context\":\"广东深圳福田转运中心-已进行装袋扫描\"},{\"time\":\"2019-09-04
		// 08:33:46\",\"ftime\":\"2019-09-04
		// 08:33:46\",\"context\":\"已到达-广东深圳福田转运中心\"},{\"time\":\"2019-09-03
		// 19:21:17\",\"ftime\":\"2019-09-03
		// 19:21:17\",\"context\":\"深圳福田科技园分点-某某某(15012689530,0755-61410360)-已收件\"}]}";
		// 正式环境使用
		String result = KdQueryData(com, num, "", "", "", 0);
		return result;
	}

	/**
	 * 实时查询物流(构建参数)
	 * 
	 * @param com
	 * @param num
	 * @param phone
	 * @param from
	 * @param to
	 * @param resultv2
	 *            是否开通区域功能（0 关闭 1 开通）
	 * @return
	 */
	public static String KdQueryData(String com, String num, String phone, String from, String to, int resultv2) {
		StringBuffer param = new StringBuffer();
		param.append("{");
		param.append("\"com\":\"").append(com).append("\"");
		param.append(",\"num\":\"").append(num).append("\"");
		param.append(",\"phone\":\"").append(phone).append("\"");
		param.append(",\"from\":\"").append(from).append("\"");
		param.append(",\"to\":\"").append(to).append("\"");
		if (0 == resultv2) {
			param.append(",\"resultv2\":0");
		} else {
			param.append(",\"resultv2\":1");
		}
		param.append("}");
		Map<String, String> params = new HashedMap<>();
		params.put("customer", customer);
		// 生成签名
		String sign = MD5Utils.encode(param + key + customer);
		params.put("sign", sign);
		params.put("param", param.toString());
		return post(params);
	}

	/**
	 * 发送post请求
	 * 
	 * @param params
	 * @return
	 */
	@SuppressWarnings("null")
	private static String post(Map<String, String> params) {
		StringBuffer response = new StringBuffer();
		BufferedReader in = null;
		StringBuilder builder = new StringBuilder();
		try {
			for (Map.Entry<String, String> param : params.entrySet()) {
				if (builder.length() > 0) {
					builder.append('&');
				}
				builder.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				builder.append('=');
				builder.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			byte[] bytes = builder.toString().getBytes("UTF-8");
			URL url = new URL(SYNQUERY_URL);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 设置连接主机服务器的超时时间：3000毫秒
			conn.setConnectTimeout(3000);
			// 设置读取远程返回的数据时间
			conn.setReadTimeout(3000);
			// 请求方式
			conn.setRequestMethod("POST");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			// 设置传入参数的格式
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// 字节数组长度
			conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
			// 默认值为：false，当向远程服务器读数据/写数据时，需要设置为true
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.getOutputStream().write(bytes);
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = "";
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(null == in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response.toString();
	}

	public static void main(String[] args) throws Exception {
		String com = "shentong"; // 快递公司编码
		String num = "776000756349705"; // 快递单号
		KdResult result = queryData(com, num);
		System.out.println(result.getData());
		System.out.print(KdQueryData("shentong", "776000745337082"));
	}
}
