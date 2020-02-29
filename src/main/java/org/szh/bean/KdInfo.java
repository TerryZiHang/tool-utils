package org.szh.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class KdInfo implements Serializable{
	

	/** */
	private static final long serialVersionUID = -4120733876431392922L;

	private String context;  // 物流轨迹节点内容
    
    private String time;  // 时间，原始格式
    
    private String ftime;  // 格式化后时间
    
    private String status;  // 本数据元对应的签收状态。只有在开通签收状态服务（见上面"status"后的说明）且在订阅接口中提交resultv2标记后才会出现
    
    private String areaCode;  // 本数据元对应的行政区域的编码，只有在开通签收状态服务（见上面"status"后的说明）且在订阅接口中提交resultv2标记后才会出现
    
    private String areaName;  // 本数据元对应的行政区域的名称，开通签收状态服务（见上面"status"后的说明）且在订阅接口中提交resultv2标记后才会出现

}
