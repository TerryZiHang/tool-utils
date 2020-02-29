package org.szh.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import com.google.zxing.Result;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * @author Terry
 * @date 2019年12月27日 @description：描述：生成二维码(文本或者图片)
 */
public class QrCodeUtils {

	/**
	 * 编码格式
	 */
	private static final String CHARSET = "UTF-8";

	/**
	 * 图片格式
	 */
	private static final String FORMAT = "PNG";

	/**
	 * 二维码颜色
	 */
	private static final int BLACK = 0xFF000000;
	/**
	 * 二维码颜色
	 */
	private static final int WHITE = 0xFFFFFFFF;

	/**
	 * 保存路径
	 */
	private static final String DEFAULT_PATH = "E:" + File.separator + "images" + File.separator + "qrcode_02";
	/**
	 * 二维码尺寸
	 */
	private static final int QRCODE_SIZE = 300;
	/**
	 * LOGO压缩宽度
	 */
	private static final int LOGO_WIDTH = 60;
	/**
	 * LOGO压缩高度
	 */
	private static final int LOGO_HEIGHT = 60;

	/**
	 * 生成二维码(内嵌LOGO) 二维码文件名随机，文件名可能会有重复
	 *
	 * @param content
	 *            内容
	 * @param logoPath
	 *            LOGO地址
	 * @param destPath
	 *            存放目录
	 * @param needCompress
	 *            是否压缩LOGO
	 * @throws Exception
	 */
	public static String generateQrCode(String content, String logoPath, String destPath, boolean needCompress)
			throws Exception {
		BufferedImage image = createImage(content, logoPath, needCompress);
		mkdirs(destPath);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = sdf.format(new Date()) + "." + FORMAT.toLowerCase();
		ImageIO.write(image, FORMAT, new File(destPath + "/" + fileName));
		return fileName;
	}

	/**
	 * 生成二维码(内嵌LOGO) 调用者指定二维码文件名
	 *
	 * @param content
	 *            内容
	 * @param logoPath
	 *            LOGO地址
	 * @param destPath
	 *            存放目录
	 * @param fileName
	 *            二维码文件名
	 * @param needCompress
	 *            是否压缩LOGO
	 * @throws Exception
	 */
	public static String generateQrCode(String content, String logoPath, String destPath, String fileName, boolean needCompress)
			throws Exception {
		BufferedImage image = createImage(content, logoPath, needCompress);
		mkdirs(destPath);
		fileName = fileName.substring(0, fileName.indexOf(".") > 0 ? fileName.indexOf(".") : fileName.length()) + "."
				+ FORMAT.toLowerCase();
		ImageIO.write(image, FORMAT, new File(destPath + "/" + fileName));
		return fileName;
	}

	/**
	 * 生成二维码(内嵌LOGO)
	 *
	 * @param content
	 *            内容
	 * @param logoPath
	 *            LOGO地址
	 * @param destPath
	 *            存储地址
	 * @throws Exception
	 */
	public static String generateQrCode(String content, String logoPath, String destPath) throws Exception {
		return QrCodeUtils.generateQrCode(content, logoPath, destPath, false);
	}

	/**
	 * 生成二维码
	 *
	 * @param content
	 *            内容
	 * @param destPath
	 *            存储地址
	 * @param needCompress
	 *            是否压缩LOGO
	 * @throws Exception
	 */
	public static String generateQrCode(String content, String destPath, boolean needCompress) throws Exception {
		return QrCodeUtils.generateQrCode(content, null, destPath, needCompress);
	}

	/**
	 * 生成二维码
	 *
	 * @param content
	 *            内容
	 * @param destPath
	 *            存储地址
	 * @throws Exception
	 */
	public static String generateQrCode(String content, String destPath) throws Exception {
		return generateQrCode(content, null, destPath, false);
	}

	/**
	 * 生成二维码
	 *
	 * @param content
	 *            内容
	 * @param output
	 *            输出流
	 * @throws Exception
	 */
	public static void generateQrCode(String content, OutputStream output) throws Exception {
		generateQrCode(content, null, output, false);
	}

	/**
	 * 生成二维码(内嵌LOGO)
	 *
	 * @param content
	 *            内容
	 * @param logoPath
	 *            LOGO地址
	 * @param output
	 *            输出流
	 * @param needCompress
	 *            是否压缩LOGO
	 * @throws Exception
	 */
	public static void generateQrCode(String content, String logoPath, OutputStream output, boolean needCompress)
			throws Exception {
		BufferedImage image = createImage(content, logoPath, needCompress);
		ImageIO.write(image, FORMAT, output);
	}

	/**
	 * 解析二维码
	 *
	 * @param path
	 *            二维码图片地址
	 * @return
	 * @throws Exception
	 */
	public static String parseQrCode(String path) throws Exception {
		return parseQrCode(new File(path));
	}

	/**
	 * 解析二维码
	 *
	 * @param file
	 *            二维码图片
	 * @return
	 * @throws Exception
	 */
	public static String parseQrCode(File file) throws Exception {
		BufferedImage image;
		image = ImageIO.read(file);
		if (image == null) {
			return null;
		}
		BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Result result;
		Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
		result = new MultiFormatReader().decode(bitmap, hints);
		String resultStr = result.getText();
		return resultStr;
	}

	private static BufferedImage createImage(String content, String logoPath, boolean needCompress) throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,
				hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
			}
		}
		if (logoPath == null || "".equals(logoPath)) {
			return image;
		}
		// 插入图片
		QrCodeUtils.insertImage(image, logoPath, needCompress);
		return image;
	}

	/**
	 * 插入LOGO
	 *
	 * @param source
	 *            二维码图片
	 * @param logoPath
	 *            LOGO图片地址
	 * @param needCompress
	 *            是否压缩
	 * @throws Exception
	 */
	private static void insertImage(BufferedImage source, String logoPath, boolean needCompress) throws Exception {
		File file = new File(logoPath);
		if (!file.exists()) {
			throw new Exception("logo file not found.");
		}
		Image src = ImageIO.read(new File(logoPath));
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		// 是否压缩LOGO
		if (needCompress) {
			if (width > LOGO_WIDTH) {
				width = LOGO_WIDTH;
			}
			if (height > LOGO_HEIGHT) {
				height = LOGO_HEIGHT;
			}
			Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = tag.getGraphics();
			// 绘制缩小后的图
			g.drawImage(image, 0, 0, null);
			g.dispose();
			src = image;
		}
		// 插入LOGO
		Graphics2D graph = source.createGraphics();
		int x = (QRCODE_SIZE - width) / 2;
		int y = (QRCODE_SIZE - height) / 2;
		graph.drawImage(src, x, y, width, height, null);
		Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
		graph.setStroke(new BasicStroke(3f));
		graph.draw(shape);
		graph.dispose();
	}

	/**
	 * 当文件夹不存在时，mkdirs会自动创建多层目录，
	 * 区别于mkdir(mkdir如果父目录不存在则会抛出异常)
	 * 
	 * @param destPath
	 *            存放目录
	 */
	private static void mkdirs(String destPath) {
		File file = new File(destPath);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
	}

	public static void main(String[] args) throws Exception {
		String text = "https://shop.szhengzhu.com";
		// 普通二维码
		QrCodeUtils.generateQrCode(text, null, DEFAULT_PATH, true);
		// 嵌入logo,不指定二维码图片名称
		QrCodeUtils.generateQrCode(text, "E:/images/logo.jpg", DEFAULT_PATH, true);
		// 嵌入logo，指定二维码图片名
		QrCodeUtils.generateQrCode(text, "E:/images/logo.jpg", DEFAULT_PATH, "qrcode", true);
		// 嵌入logo,输出页面
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		QrCodeUtils.generateQrCode(text,out);
		//解析二维码
		System.out.println(parseQrCode("E:/images/qrcode_02/qrcode.png"));
	}
}
