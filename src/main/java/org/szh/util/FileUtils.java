package org.szh.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件工具
 * 
 * @author Terry Zi
 *
 */
public class FileUtils {
	
	private static final String BASE_PATH= File.separator + "usr" + File.separator + "pictures";

    /**
     * 验证文件路径，得到文件正确路径
     * 
     * @date 2019年5月5日 上午11:48:45
     * @param fileNameWithPath
     * @return
     */
    public static String getFilePath(String fileNameWithPath) {
        String filePath = "";
        fileNameWithPath = fileNameWithPath.replace("/", File.separator);
        int idx = fileNameWithPath.lastIndexOf(File.separator);
        if (idx != -1) {
            filePath = fileNameWithPath.substring(0, idx);
        }
        return filePath;
    }

    /**
     * 将内容写入文件
     * 
     * @date 2019年5月5日 上午11:49:16
     * @param context  内容
     * @param fileName 目标文件
     * @throws Exception
     */
    public static void writeFile(String context, String fileName) throws Exception {
        byte[] bytes = context.getBytes();
        File file = new File(fileName);
        FileOutputStream fop = new FileOutputStream(file);
        BufferedOutputStream out = new BufferedOutputStream(fop);
        if (!file.exists()) {
            file.mkdirs();
        }
        out.write(bytes);
        out.flush();
        out.close();
    }

    /**
     * 复制文件
     * 
     * @date 2019年5月5日 上午11:49:52
     * @param srcFileName  源文件
     * @param destFileName 目标文件
     * @throws Exception
     */
    public static void copyFile(String srcFileName, String destFileName) throws Exception {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(new File(srcFileName)));
            String destPath = getFilePath(destFileName);
            File pathFile = new File(destPath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            out = new BufferedOutputStream(new FileOutputStream(new File(destFileName)));
            int len;
            byte[] b = new byte[1024];
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            System.out.println("复制文件失败：" + e.getMessage());
            throw e;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    /**
     * 图片格式判断
     * 
     * @param suffix
     * @return
     */
    public static boolean isImgSuffix(String suffix) {
        String suffixList = ".jpg,.png,.ico,.bmp,.jpeg";
        return suffixList.contains(suffix);
    }

    /**
     * 文件上传 文件修改后上传保存，如果源文件不是空，则覆盖源文件，如果源文件为空，则创建新文件
     * 
     * @date 2019年5月5日 下午5:24:13
     * @param file
     * @param savePath
     */
    public static void uploadImg(MultipartFile file, String savePath) {
        if (StringUtils.isEmpty(savePath)) {
            throw new RuntimeException("There is no save path for setting files on the server");
        }
        File superPath = new File(getFilePath(savePath));
        if (!superPath.exists()) {
            if (!superPath.mkdirs()) {
                throw new RuntimeException("Failed to create file upload path: " + savePath);
            }
        }
        try {
        	// transferTo上传需要绝对路径
            File savePathFile = new File(superPath.getAbsolutePath(),
                    savePath.substring(savePath.lastIndexOf(File.separator) + 1));  
            file.transferTo(savePathFile);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 上传文件
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static String upload(MultipartFile file) throws IOException {
    	if(file != null && !file.isEmpty()) {
    		String fileName = file.getOriginalFilename();
    		String fileType = fileName.substring(fileName.lastIndexOf("."));
    		byte[] bytes = file.getBytes();
    		return upload(bytes,fileType);
    	}
		return null;
    }
    
    /**
     * 上传文件的字节数组：bytes
     * 文件类型（包含.号）：fielType
     * 
     * @param bytes
     * @param fileType
     * @return
     */
    public static String upload(byte[] bytes,String fileType) {
    	// 自定义目录
    	String path = "images";
    	return upload(bytes,fileType,path);
    }

	/**
	 * @param bytes
	 * @param fileType
	 * @param Path：自定义目录名称
	 * @return
	 */
	private static String upload(byte[] bytes, String fileType, String path) {
		if(!isImgSuffix(fileType)) {
			 throw new RuntimeException("There is no suitable for type of file");
		}
		//定义文件路径
		StringBuffer savePath = new StringBuffer(BASE_PATH);
		savePath.append(getSavePath(path));
		String sourcePath = savePath.toString();
		File sourceFile = new File(sourcePath);
		if(!sourceFile.exists()) {
			//文件不存在，创建路径下的所有目录和文件
			sourceFile.mkdirs();
		}
		IdGenerator idGenerator = IdGenerator.getInstance();
		String fileName = idGenerator.nexId() + fileType;
		File destFile = new File(sourcePath,fileName);
		String imagePath = saveFile(destFile,bytes) ? getSavePath(path) + File.separator + fileName : null;
		return imagePath;
	}

	/**
	 * 输出到指定地方（写文件）
	 * 
	 * @param destFile
	 * @param bytes
	 * @return
	 */
	private static boolean saveFile(File destFile, byte[] bytes) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fos = new FileOutputStream(destFile);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
			bos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 自定义路径
	 * 
	 * @param path
	 * @return
	 */
	private static String getSavePath(String path) {
		return  File.separator + path;
	}
}
