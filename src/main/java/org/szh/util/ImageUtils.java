package org.szh.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 *  图片工具类， 图片水印，文字水印，缩放，补白等
 * 
 * @author Terry
 *
 */
public final class ImageUtils {

	 public static final float DEFAULT_QUALITY = 0.2125f;

	    private ImageUtils() {}

	    /**
	     * 添加图片水印
	     * 
	     * @param targetImg
	     *            目标图片路径，如：C://myPictrue//1.jpg
	     * @param waterImg
	     *            水印图片路径，如：C://myPictrue//logo.png
	     * @param x
	     *            水印图片距离目标图片左侧的偏移量，如果x<0, 则在正中间
	     * @param y
	     *            水印图片距离目标图片上侧的偏移量，如果y<0, 则在正中间
	     * @param alpha
	     *            透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
	     */
	    public static BufferedImage pressImage(String targetImg, String waterImg, int x, int y,
	            float alpha) {
	        File file = new File(targetImg);
	        return pressImage(file, waterImg, x, y, alpha);
	    }

	    public static BufferedImage pressImage(File file, String waterImg, int x, int y, float alpha) {
	        try {
	            BufferedImage image = ImageIO.read(file);
	            return pressImage(image, waterImg, x, y, alpha);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    public static BufferedImage pressImage(BufferedImage image, String waterImg, int x, int y,
	            float alpha) {
	        try {
	            int width = image.getWidth(null);
	            int height = image.getHeight(null);
	            BufferedImage bufferedImage = new BufferedImage(width, height,
	                    BufferedImage.TYPE_INT_RGB);
	            Graphics2D g = bufferedImage.createGraphics();
	            g.drawImage(image, 0, 0, width, height, null);
	            // 水印文件
	            Image waterImage = ImageIO.read(new File(waterImg)); 
	            int waterWidth = waterImage.getWidth(null);
	            int waterHeight = waterImage.getHeight(null);
	            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
	            int widthDiff = width - waterWidth;
	            int heightDiff = height - waterHeight;
	            if (x < 0) {
	                x = widthDiff / 2;
	            } else if (x > widthDiff) {
	                x = widthDiff;
	            }
	            if (y < 0) {
	                y = heightDiff / 2;
	            } else if (y > heightDiff) {
	                y = heightDiff;
	            }
	            // 水印文件结束
	            g.drawImage(waterImage, x, y, waterWidth, waterHeight, null); 
	            g.dispose();
	            return bufferedImage;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    /**
	     * 添加文字水印
	     * 
	     * @param targetImg
	     *            目标图片路径，如：C://myPictrue//1.jpg
	     * @param pressText
	     *            水印文字， 如：中国证券网
	     * @param fontName
	     *            字体名称， 如：宋体
	     * @param fontStyle
	     *            字体样式，如：粗体和斜体(Font.BOLD|Font.ITALIC)
	     * @param fontSize
	     *            字体大小，单位为像素
	     * @param color
	     *            字体颜色
	     * @param x
	     *            水印文字距离目标图片左侧的偏移量，如果x<0, 则在正中间
	     * @param y
	     *            水印文字距离目标图片上侧的偏移量，如果y<0, 则在正中间
	     * @param alpha
	     *            透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
	     */
	    public static BufferedImage pressText(String targetImg, String pressText, String fontName,
	            int fontStyle, int fontSize, Color color, int x, int y, float alpha) {
	        File file = new File(targetImg);
	        return pressText(file, pressText, fontName, fontStyle, fontSize, color, x, y, alpha);
	    }

	    public static BufferedImage pressText(File file, String pressText, String fontName,
	            int fontStyle, int fontSize, Color color, int x, int y, float alpha) {
	        try {
	            BufferedImage image = ImageIO.read(file);
	            return pressText(image, pressText, fontName, fontStyle, fontSize, color, x, y, alpha);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;

	    }
	    
	    public static BufferedImage pressText(BufferedImage image, String pressText, String fontName,
	            int fontStyle, int fontSize, String color, int x, int y, float alpha) {
	        Color cc = new Color(Integer.parseInt(color, 16));
	        return pressText(image, pressText, fontName, fontStyle, fontSize, cc, x, y, alpha);
	    }

	    public static BufferedImage pressText(BufferedImage image, String pressText, String fontName,
	            int fontStyle, int fontSize, Color color, int x, int y, float alpha) {
	        try {
	            int width = image.getWidth(null);
	            int height = image.getHeight(null);
	            BufferedImage bufferedImage = new BufferedImage(width, height,
	                    BufferedImage.TYPE_INT_RGB);
	            Graphics2D g = bufferedImage.createGraphics();
	            g.drawImage(image, 0, 0, width, height, null);
	            g.setFont(new Font(fontName, fontStyle, fontSize));
	            g.setColor(color);
	            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
	            int textWidth = fontSize * getLength(pressText);
	            int textHeight = fontSize;
	            int widthDiff = width - textWidth;
	            int heightDiff = height - textHeight;
	            if (x < 0) {
	                x = widthDiff / 2;
	            } else if (x > widthDiff) {
	                x = widthDiff;
	            }
	            if (y < 0) {
	                y = heightDiff / 2;
	            } else if (y > heightDiff) {
	                y = heightDiff;
	            }

	            g.drawString(pressText, x, y + textHeight);
	            g.dispose();
	            return bufferedImage;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    /**
	     * 获取字符长度，一个汉字作为 1 个字符, 一个英文字母作为 0.5 个字符
	     * 
	     * @param text
	     * @return 字符长度，如：text="中国",返回 2；text="test",返回 2；text="中国ABC",返回 4.
	     */
	    public static int getLength(String text) {
	        int textLength = text.length();
	        int length = textLength;
	        for (int i = 0; i < textLength; i++) {
	            if (String.valueOf(text.charAt(i)).getBytes().length > 1) {
	                length++;
	            }
	        }
	        return (length % 2 == 0) ? length / 2 : length / 2 + 1;
	    }

	    /**
	     * 图片缩放
	     * 
	     * @param filePath
	     *            图片路径
	     * @param height
	     *            高度
	     * @param width
	     *            宽度
	     * @param bb
	     *            比例不对时是否需要补白
	     */
	    public static BufferedImage resize(String filePath, int width, int height, boolean bb) {
	        File filse = new File(filePath);
	        return resize(filse, width, height, bb);
	    }

	    public static BufferedImage resize(File file, int width, int height, boolean bb) {
	        try {
	            BufferedImage bi = ImageIO.read(file);
	            return resize(bi, width, height, bb);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    public static BufferedImage resize(BufferedImage bi, int width, int height, boolean bb) {
	    	// 缩放比例
	        double ratio = 0; 
	        Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
	        // 计算比例
	        if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
	            if (bi.getHeight() > bi.getWidth()) {
	                ratio = (new Integer(height)).doubleValue() / bi.getHeight();
	            } else {
	                ratio = (new Integer(width)).doubleValue() / bi.getWidth();
	            }
	            AffineTransformOp op = new AffineTransformOp(
	                    AffineTransform.getScaleInstance(ratio, ratio), null);
	            itemp = op.filter(bi, null);
	        } else {
	            AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(1, 1),
	                    null);
	            itemp = op.filter(bi, null);
	            return (BufferedImage) itemp;
	        }
	        if (bb) {
	            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	            Graphics2D graphics = image.createGraphics();
	            graphics.setColor(Color.white);
	            graphics.fillRect(0, 0, width, height);
	            if (width == itemp.getWidth(null)) {
	                graphics.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
	                        itemp.getWidth(null), itemp.getHeight(null), Color.white, null);
	            }
	            else {
	                graphics.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
	                        itemp.getWidth(null), itemp.getHeight(null), Color.white, null);
	            }
	            graphics.dispose();
	            return image;
	        } else {
	            return (BufferedImage) itemp;
	        }
	    }

	    /**
	     * 输出图片字节数组
	     * 
	     * @param bImage
	     * @param type
	     * @return
	     */
	    public static byte[] translateImage(BufferedImage bImage, String type) {
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        try {
	            ImageIO.write(bImage, type, out);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return out.toByteArray();
	    }

	    /**
	     * 合成带背景的二维码图
	     * 
	     * @param background
	     * @param qrcode
	     * @return
	     */
	    public static BufferedImage mergeImage(BufferedImage background, BufferedImage qrcode) {
	        int width = background.getWidth(null);
	        int height = background.getHeight(null);
	        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g = bufferedImage.createGraphics();
	        int[] pos = position(background, width, height);
	        g.drawImage(background, 0, 0, width, height, null);
	        // 水印文件结束
	        g.drawImage(qrcode, pos[0], pos[1], pos[2], pos[2], null); 
	        g.dispose();
	        return bufferedImage;
	    }

	    /**
	     * 定位置
	     * 
	     * @param bi
	     * @param w
	     * @param h
	     * @return
	     */
	    private static int[] position(BufferedImage bi, int w, int h) {
	        int[] position = { 0, 0, 0 };
	        // 是否是起始位置
	        boolean status = false;
	        for (int i = 0; i < w; i++) {
	            for (int j = 0; j < h; j++) {
	                int rgb = bi.getRGB(i, j);
	                if (rgb > 0) {
	                    if (!status) {
	                        position[0] = i;
	                        position[1] = j;
	                        status = true;
	                    }
	                } else {
	                    if (status) {
	                        position[2] = j - position[1];
	                        return position;
	                    }
	                }

	            }
	        }
	        return position;
	    }

	    /**
	     * 
	     * 
	     * 
	     * 压缩图片操作(文件物理存盘,使用默认格式)
	     * 
	     * @param imgPath
	     *            待处理图片
	     * @param quality
	     *            图片质量(0-1之間的float值)
	     * @param width
	     *            输出图片的宽度 输入负数参数表示用原来图片宽
	     * @param height
	     *            输出图片的高度 输入负数参数表示用原来图片高
	     * @param autoSize
	     *            是否等比缩放 true表示进行等比缩放 false表示不进行等比缩放
	     * @param format
	     *            压缩后存储的格式
	     * @param destPath
	     *            文件存放路径
	     * 
	     * @throws Exception
	     */
	    public static void compressImage(String imgPath, float quality, int width, int height,
	            boolean autoSize, String destPath) throws Exception {
	        try {
	            BufferedImage bufferedImage = compressImage(imgPath, quality, width, height, autoSize);
	            ImageIO.write(bufferedImage, imageFormat(imgPath), new File(destPath));
	        } catch (Exception e) {
	            throw new RuntimeException("图片压缩异常");
	        }

	    }

	    /**
	     * 
	     * 压缩图片操作(文件物理存盘,可自定义格式)
	     * 
	     * @param imgPath
	     *            待处理图片
	     * @param quality
	     *            图片质量(0-1之間的float值)
	     * @param width
	     *            输出图片的宽度 输入负数参数表示用原来图片宽
	     * @param height
	     *            输出图片的高度 输入负数参数表示用原来图片高
	     * @param autoSize
	     *            是否等比缩放 true表示进行等比缩放 false表示不进行等比缩放
	     * @param format
	     *            压缩后存储的格式
	     * @param destPath
	     *            文件存放路径
	     * 
	     * @throws Exception
	     */
	    public static void compressImage(String imgPath, float quality, int width, int height,
	            boolean autoSize, String format, String destPath) throws Exception {
	        try {
	            BufferedImage bufferedImage = compressImage(imgPath, quality, width, height, autoSize);
	            ImageIO.write(bufferedImage, format, new File(destPath));
	        } catch (Exception e) {
	            throw new RuntimeException("图片压缩异常");
	        }
	    }

	    /**
	     * 
	     * 压缩图片操作,返回BufferedImage对象
	     * 
	     * @param imgPath
	     *            待处理图片
	     * @param quality
	     *            图片质量(0-1之間的float值)
	     * @param width
	     *            输出图片的宽度 输入负数参数表示用原来图片宽
	     * @param height
	     *            输出图片的高度 输入负数参数表示用原来图片高
	     * @param autoSize
	     *            是否等比缩放 true表示进行等比缩放 false表示不进行等比缩放
	     * @return 处理后的图片对象
	     * @throws Exception
	     */
	    public static BufferedImage compressImage(String imgPath, float quality, int width, int height,
	            boolean autoSize) throws Exception {
	        BufferedImage targetImage = null;
	        float min = 0F,max = 1F;
	        if (quality < min || quality > max) {
	            quality = DEFAULT_QUALITY;
	        }
	        try {
	            Image img = ImageIO.read(new File(imgPath));
	            // 如果用户输入的图片参数合法则按用户定义的复制,负值参数表示执行默认值
	            int newwidth = (width > 0) ? width : img.getWidth(null);
	            // 如果用户输入的图片参数合法则按用户定义的复制,负值参数表示执行默认值
	            int newheight = (height > 0) ? height : img.getHeight(null);
	            // 如果是自适应大小则进行比例缩放
	            if (autoSize) {
	                // 为等比缩放计算输出的图片宽度及高度
	                double widthRate = ((double) img.getWidth(null)) / (double) width + 0.1;
	                double heightRate = ((double) img.getHeight(null)) / (double) height + 0.1;
	                double rate = widthRate > heightRate ? widthRate : heightRate;
	                newwidth = (int) (((double) img.getWidth(null)) / rate);
	                newheight = (int) (((double) img.getHeight(null)) / rate);
	            }
	            // 创建目标图像文件
	            targetImage = new BufferedImage(newwidth, newheight, BufferedImage.TYPE_INT_RGB);
	            Graphics2D g = targetImage.createGraphics();
	            g.drawImage(img, 0, 0, newwidth, newheight, null);
	            // 如果添加水印或者文字则继续下面操作,不添加的话直接返回目标文件----------------------
	            g.dispose();

	        } catch (Exception e) {
	            throw new RuntimeException("图片压缩操作异常");
	        }
	        return targetImage;
	    }

	    /**
	     * 获取图片文件的真实格式信息
	     * 
	     * @param imgPath
	     *            图片原文件存放地址
	     * @return 图片格式
	     * @throws Exception
	     */
	    public static String imageFormat(String imgPath) throws Exception {
	        String[] files = imgPath.split("\\\\");
	        String[] formats = files[files.length - 1].split("\\.");
	        return formats[formats.length - 1];
	    }
	    
	/*public static BufferedImage createOrderImage(OrderInfo order) {
		try {
			InputStream imagein = new FileInputStream(
					File.separator + "usr" + File.separator + "javaApplication" + File.separator + "back.png");
			BufferedImage background = ImageIO.read(imagein);
			Graphics2D g = background.createGraphics();
			Font f = new Font("宋体", Font.BOLD, 35);
			g.setFont(f);
			g.setColor(Color.BLACK);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int start = 70, width = 640, left = 25;
			start = drawBaseInfo(order, g, f, left, start, width);
			start = drawOrderInfo(order.getOrders(), g, left, start);
			return background;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 定义图片主体内容位置
	 *
	 * @param o
	 *            数据
	 * @param g
	 *            画笔
	 * @param f
	 *            字体
	 * @param left
	 *            水平左间距
	 * @param start
	 *            垂直行间距
	 * @param width
	 *            行宽
	 * @return
	 * @throws Exception 
	 * @date 2019年9月5日
	 *//*
	private static int drawBaseInfo(OrderInfo o, Graphics2D g, Font f, int left, int start, int width) {
		g.drawString("地址：" + o.getUserArea(), left, start);
		List<String> rows = getStringRows(g, f, o.getUserAddress(), width);
		for (int i = 0, len = rows.size(); i < len; i++)
			g.drawString(rows.get(i), left, start += 36);
		g.drawString("联系人：" + o.getUserName(), left, start += 42);
		g.drawString("联系电话：" + o.getUserPhone(), left, start += 42);
		g.drawString("订单详情：", left, start += 42);
		return start;
	}

	private static int drawOrderInfo(List<OrderItem> list, Graphics g, int left, int start) {
		String t;
		for (OrderItem o : list) {
			t = o.getGoods_name();
			t = t.length() <= 15 ? t : t.substring(0, 15);
			g.drawString(t + "*" + o.getQuantity(), left, start += 36);
		}
		return start;
	}

	private static List<String> getStringRows(Graphics g, Font font, String text, int width) {
		List<String> textRows = new LinkedList<String>();
		String temp;
		FontMetrics fm = g.getFontMetrics();
		int text_length = (int) fm.getStringBounds(text, g).getWidth();
		if (text_length <= width) {
			textRows.add(text);
			return textRows;
		}
		while (!text.equals("")) {
			temp = getStringRow(g, fm, text, width);
			textRows.add(temp);
			text = text.replaceFirst(temp, "");
		}
		return textRows;
	}

	private static String getStringRow(Graphics g, FontMetrics fm, String text, int width) {
		int text_length = (int) fm.getStringBounds(text, g).getWidth();
		if (text_length <= width)
			return text;
		int start = text.length() / (text_length / width + 1);
		text_length = (int) fm.getStringBounds(text, 0, start, g).getWidth();
		if (text_length == width)
			return text.substring(0, start);
		else if (text_length > width) {
			while (text_length > width) {
				start--;
				text_length = (int) fm.getStringBounds(text, 0, start, g).getWidth();
			}
			return text.substring(0, start);
		} else {
			while (text_length < width) {
				start++;
				text_length = (int) fm.getStringBounds(text, 0, start, g).getWidth();
			}
			return text.substring(0, start - 1);
		}
	}*/

	public static void main(String[] args) throws Exception {
		String text = "石子abc";
		System.out.println(getLength(text));
		String imagePath="D:\\backend\\pictures\\html\\2019-07\\e41a5e06-a111-47c6-82fb-25bc812a3985.jpg";
        compressImage(imagePath,0.15f,2000,2000,true,"jpg","D:\\backend\\pictures\\test.jpg");
	}

}