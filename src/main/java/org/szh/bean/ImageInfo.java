package org.szh.bean;

import java.io.Serializable;

import lombok.Data;

/**
 * 图片信息
 * 
 * @author Administrator
 * @date 2019年3月5日
 */
@Data
public class ImageInfo implements Serializable {

    private static final long serialVersionUID = 8421486906021961502L;

    private String markId;

    private String imagePath;

    private String fileType;
    
    public ImageInfo() {}
    
    public ImageInfo(String markId, String imagePath, String fileType) {
        this.markId = markId;
        this.imagePath = imagePath;
        this.fileType = fileType.substring(1, fileType.length());//移除文件类型前面的.号
    }
}