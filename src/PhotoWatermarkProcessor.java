import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class PhotoWatermarkProcessor {
    private static final Logger logger = Logger.getLogger(PhotoWatermarkProcessor.class.getName());
    private final CommandLineParser parser;
    
    public PhotoWatermarkProcessor(CommandLineParser parser) {
        this.parser = parser;
    }
    
    public void processPhotos() throws IOException, ImageProcessingException, ParseException {
        Path imagePath = parser.getImagePath();
        File file = imagePath.toFile();
        
        // 检查文件是否为目录
        if (file.isDirectory()) {
            processDirectory(file);
        } else {
            // 单个文件处理
            processSingleFile(file);
        }
    }
    
    private void processDirectory(File directory) throws IOException, ImageProcessingException, ParseException {
        logger.log(Level.INFO, "处理目录: {0}", directory.getAbsolutePath());
        
        // 获取所有图片文件
        File[] imageFiles = directory.listFiles(this::isImageFile);
        
        if (imageFiles == null || imageFiles.length == 0) {
            logger.log(Level.INFO, "目录中没有找到图片文件: {0}", directory.getAbsolutePath());
            return;
        }
        
        // 创建输出目录
        Path outputDir = createOutputDirectory(directory);
        
        // 处理每个图片文件
        for (File imageFile : imageFiles) {
            try {
                addWatermarkToImage(imageFile, outputDir);
            } catch (Exception e) {
                logger.log(Level.WARNING, "处理文件失败: {0}, 错误: {1}", new Object[]{imageFile.getAbsolutePath(), e.getMessage()});
            }
        }
        
        logger.log(Level.INFO, "目录处理完成，共处理 {0} 个文件，输出目录: {1}", 
                new Object[]{imageFiles.length, outputDir.toAbsolutePath()});
    }
    
    private void processSingleFile(File imageFile) throws IOException, ImageProcessingException, ParseException {
        if (!isImageFile(imageFile)) {
            throw new IllegalArgumentException("指定的文件不是图片: " + imageFile.getAbsolutePath());
        }
        
        logger.log(Level.INFO, "处理单个文件: {0}", imageFile.getAbsolutePath());
        
        // 获取文件所在目录
        File parentDir = imageFile.getParentFile();
        if (parentDir == null) {
            parentDir = new File(System.getProperty("user.dir"));
        }
        
        // 创建输出目录
        Path outputDir = createOutputDirectory(parentDir);
        
        // 添加水印并保存
        addWatermarkToImage(imageFile, outputDir);
        
        logger.log(Level.INFO, "文件处理完成，输出目录: {0}", outputDir.toAbsolutePath());
    }
    
    private boolean isImageFile(File file) {
        if (!file.isFile()) {
            return false;
        }
        
        String extension = getFileExtension(file).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif") || extension.equals("bmp");
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return name.substring(lastDotIndex + 1);
        }
        return "";
    }
    
    private Path createOutputDirectory(File parentDir) throws IOException {
        String parentDirName = parentDir.getName();
        Path outputDir;
        
        // 如果parentDir.getParent()为null，使用当前工作目录
        if (parentDir.getParent() == null) {
            outputDir = Paths.get(System.getProperty("user.dir"), parentDirName + "_watermark");
        } else {
            outputDir = Paths.get(parentDir.getParent(), parentDirName + "_watermark");
        }
        
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
            logger.log(Level.INFO, "创建输出目录: {0}", outputDir.toAbsolutePath());
        }
        
        return outputDir;
    }
    
    private void addWatermarkToImage(File imageFile, Path outputDir) throws IOException, ImageProcessingException, ParseException {
        // 读取图片
        BufferedImage image = ImageIO.read(imageFile);
        
        // 获取EXIF信息中的拍摄时间
        String watermarkText = getWatermarkText(imageFile);
        
        if (watermarkText == null || watermarkText.isEmpty()) {
            logger.log(Level.WARNING, "无法获取文件的拍摄时间: {0}", imageFile.getAbsolutePath());
            return;
        }
        
        // 创建Graphics2D对象以绘制水印
        Graphics2D g2d = image.createGraphics();
        
        // 设置抗锯齿以获得更好的文字质量
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 设置字体和颜色
        Font font = new Font("Arial", Font.BOLD, parser.getFontSize());
        g2d.setFont(font);
        
        // 解析颜色
        Color color = parseColor(parser.getFontColor());
        g2d.setColor(color);
        
        // 计算水印位置
        int[] position = calculateWatermarkPosition(image, watermarkText, font);
        int x = position[0];
        int y = position[1];
        
        // 绘制水印
        g2d.drawString(watermarkText, x, y);
        
        // 释放资源
        g2d.dispose();
        
        // 保存图片
        String outputFileName = imageFile.getName();
        Path outputPath = outputDir.resolve(outputFileName);
        String formatName = getFileExtension(imageFile);
        boolean success = ImageIO.write(image, formatName, outputPath.toFile());
        if (success) {
            logger.log(Level.INFO, "已添加水印并保存到: {0}", outputPath.toAbsolutePath());
        } else {
            logger.log(Level.WARNING, "无法保存图片到: {0}", outputPath.toAbsolutePath());
        }
    }
    
    private String getWatermarkText(File imageFile) throws ImageProcessingException, IOException, ParseException {
        Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        
        if (directory != null) {
            Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            if (date != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                return dateFormat.format(date);
            }
        }
        
        // 如果无法获取拍摄时间，返回默认的水印文本
        logger.log(Level.WARNING, "无法获取文件的拍摄时间: {0}，使用默认水印", imageFile.getAbsolutePath());
        return "Watermark";
    }
    
    private Color parseColor(String colorString) {
        // 移除#符号
        colorString = colorString.replaceFirst("^#", "");
        
        // 解析RGB值
        int red = Integer.parseInt(colorString.substring(0, 2), 16);
        int green = Integer.parseInt(colorString.substring(2, 4), 16);
        int blue = Integer.parseInt(colorString.substring(4, 6), 16);
        
        return new Color(red, green, blue);
    }
    
    private int[] calculateWatermarkPosition(BufferedImage image, String watermarkText, Font font) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        
        // 获取字体 metrics 以计算文本宽度和高度
        java.awt.FontMetrics metrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics().getFontMetrics(font);
        int textWidth = metrics.stringWidth(watermarkText);
        int textHeight = metrics.getHeight();
        
        // 边距
        int margin = 10;
        
        int x = 0, y = 0;
        
        // 根据用户选择的位置计算水印坐标
        y = switch (parser.getPosition()) {
            case TOP_LEFT -> {
                x = margin;
                yield margin + textHeight;
            }
            case TOP_CENTER -> {
                x = (imageWidth - textWidth) / 2;
                yield margin + textHeight;
            }
            case TOP_RIGHT -> {
                x = imageWidth - textWidth - margin;
                yield margin + textHeight;
            }
            case MIDDLE_LEFT -> {
                x = margin;
                yield (imageHeight + textHeight) / 2;
            }
            case MIDDLE_CENTER -> {
                x = (imageWidth - textWidth) / 2;
                yield (imageHeight + textHeight) / 2;
            }
            case MIDDLE_RIGHT -> {
                x = imageWidth - textWidth - margin;
                yield (imageHeight + textHeight) / 2;
            }
            case BOTTOM_LEFT -> {
                x = margin;
                yield imageHeight - margin;
            }
            case BOTTOM_CENTER -> {
                x = (imageWidth - textWidth) / 2;
                yield imageHeight - margin;
            }
            case BOTTOM_RIGHT -> {
                x = imageWidth - textWidth - margin;
                yield imageHeight - margin;
            }
        };
        
        return new int[]{x, y};
    }
}