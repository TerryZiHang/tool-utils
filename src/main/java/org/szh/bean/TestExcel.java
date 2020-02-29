package org.szh.bean;

import java.io.Serializable;
import java.util.List;

import org.szh.annotation.Excel;

import lombok.Data;

@Data
public class TestExcel implements Serializable{
	
	private static final long serialVersionUID = -4510670413999756695L;

	@Excel(name="名称",sort = 1)
	private String name;
	
	@Excel(name="个人ID",select=true,sort = 2)
	private List<String> personIDs;
}
