package org.szh.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 快递返回结果
 * 
 * @author Terry Zi
 *
 */
@Data
public class KdResult implements Serializable{
	
	private static final long serialVersionUID = -299030357949193385L;

	private String message;  // 消息体
    
    private Integer state; // 快递单当前状态，包括0在途，1揽收，2疑难，3签收，4退签，5派件，6退回，7转投 等7个状态
    
    private Integer status; // 通讯状态，请忽略
    
    private String condition; // 快递单明细状态标记，暂未实现，请忽略
    
    private Integer ischeck; // 是否签收标记
    
    private String com; // 快递公司编码,一律用小写字母
    
    private String nu; // 快递单号
    
    private List<KdInfo> data; // 数组，包含多个对象，每个对象字段如展开所示

}
