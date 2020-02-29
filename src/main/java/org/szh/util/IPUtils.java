package org.szh.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 非nginx代理情况下，
 * 若代理还需要在location中加入：
 * proxy_set_header Host $host;
 * proxy_set_header X-Real-IP $remote_addr;
 * proxy_set_header X-Real-Port $remote_port;
 * proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
 * 
 * @author Terry Zi
 *
 */
public class IPUtils {
	
    /**
     * 获取用户真实ip
     * 
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        System.out.println("X-Forwarded-For:"+ip);
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {  
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if( ip.indexOf(",") != -1 ){
                ip = ip.split(",")[0];
            }
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
            System.out.println("HTTP_X_FORWARDED_FOR："+ip);
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("X-Real-IP");  
            System.out.println("X-Real-IP："+ip);
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
            System.out.println("getRemoteAddr："+ip);
        } 
        System.out.println("获取客户端真实ip: " + ip);
        return ip;  
    }

}
