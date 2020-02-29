package org.szh.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Random;

/**
 * 图片验证码
 * 
 * @author Terry Zi
 *
 */
public class ValidateCode {
	/** 图片的宽度 */
	private int width = 160;
	/** 图片的高度。 */
	private int height = 40;
	/** 验证码字符个数 */
	private int codeCount = 5;
	/** 验证码干扰线数 */
	private int lineCount = 150;
	/** 验证码 */
	private String code = null;
	/** 验证码图片Buffer */
	private BufferedImage buffImg = null;

	private static final int DEFAULT_RANGE = 255;

	private static final String IMAGE_TYPE = "png";

	/** 验证码范围,去掉0(数字)和O(拼音)容易混淆的(小写的1和L也可以去掉,大写不用了) */
	private static final char[] CODE_SEQUENCE = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z','a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
			'm', 'n','p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'1', '2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * 默认构造函数,设置默认参数
	 */
	public ValidateCode() {
		this.createCode();
	}

	/**
	 * @param width:
	 *            图片宽
	 * @param height:图片高
	 */
	public ValidateCode(int width, int height) {
		this.width = width;
		this.height = height;
		this.createCode();
	}

	/**
	 * @param width:
	 *            图片宽
	 * @param height:图片高
	 * @param codeCount:字符个数
	 * @param lineCount:干扰线条数
	 */
	public ValidateCode(int width, int height, int codeCount, int lineCount) {
		this.width = width;
		this.height = height;
		this.codeCount = codeCount;
		this.lineCount = lineCount;
		this.createCode();
	}

	public void createCode() {
		int x = 0, fontHeight = 0, codeHeight = 0;
		int red = 0, green = 0, blue = 0;
		// 每个字符的宽度(左右各空出一个字符)
		x = width / (codeCount + 2);
		// 字体的高度
		fontHeight = height - 2;
		codeHeight = height - 4;

		// 图像buffer
		buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		// 生成随机数
		Random random = new Random();
		// 将图像填充为白色
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		// 创建字体,可以修改为其它的
		Font font = new Font("宋体", Font.BOLD, fontHeight);
//		 Font font = new Font("Times New Roman", Font.ROMAN_BASELINE, fontHeight);
		g.setFont(font);

		for (int i = 0; i < lineCount; i++) {
			// 设置随机开始和结束坐标
			// x坐标开始
			int xs = random.nextInt(width);
			// y坐标开始
			int ys = random.nextInt(height);
			// x坐标结束
			int xe = xs + random.nextInt(width / 8);
			// y坐标结束
			int ye = ys + random.nextInt(height / 8);

			// 产生随机的颜色值，让输出的每个干扰线的颜色值都将不同。
			red = random.nextInt(DEFAULT_RANGE);
			green = random.nextInt(DEFAULT_RANGE);
			blue = random.nextInt(DEFAULT_RANGE);
			g.setColor(new Color(red, green, blue));
			g.drawLine(xs, ys, xe, ye);
		}

		// randomCode记录随机产生的验证码
		StringBuffer randomCode = new StringBuffer();
		// 随机产生codeCount个字符的验证码。
		for (int i = 0; i < codeCount; i++) {
			String strRand = String.valueOf(CODE_SEQUENCE[random.nextInt(CODE_SEQUENCE.length)]);
			// 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
			red = random.nextInt(DEFAULT_RANGE);
			green = random.nextInt(DEFAULT_RANGE);
			blue = random.nextInt(DEFAULT_RANGE);
			g.setColor(new Color(red, green, blue));
			g.drawString(strRand, (i + 1) * x, codeHeight);
			// 将产生的四个随机数组合在一起。
			randomCode.append(strRand);
		}
		// 将四位数字的验证码保存到Session中。
		code = randomCode.toString();
	}

	public void write(String path) throws IOException {
		OutputStream sos = new FileOutputStream(path);
		this.write(sos);
	}

	public void write(OutputStream sos) throws IOException {
		ImageIO.write(buffImg, IMAGE_TYPE, sos);
		sos.close();
	}

	public BufferedImage getBuffImg() {
		return buffImg;
	}

	public String getCode() {
		return code;
	}

	public byte[] tansformImg() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(buffImg, IMAGE_TYPE, bos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}

	/**
	 * 测试函数,默认生成到d盘
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ValidateCode vCode = new ValidateCode(160, 40, 5, 150);
		String path = "D:/" + new Date().getTime() + ".png";
		OutputStream sos = new FileOutputStream(path);
		byte[] imageByte = vCode.tansformImg();
		sos.write(imageByte);
		sos.flush();
		sos.close();
		System.out.println(vCode.getCode() + " >" + path);
		System.out.println("v57r".equalsIgnoreCase("V57r"));
	}
}
