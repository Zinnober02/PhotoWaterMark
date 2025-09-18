import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandLineParser {
    private static final Logger logger = Logger.getLogger(CommandLineParser.class.getName());
    
    private Path imagePath;
    private int fontSize = 24; // 默认字体大小
    private String fontColor = "#000000"; // 默认黑色
    private Position position = Position.BOTTOM_RIGHT; // 默认右下角
    
    public enum Position {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }
    
    public CommandLineParser(String[] args) {
        parseArgs(args);
    }
    
    private void parseArgs(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("必须提供图片文件路径");
        }
        
        // 第一个参数是图片路径
        this.imagePath = Paths.get(args[0]);
        if (!this.imagePath.toFile().exists()) {
            throw new IllegalArgumentException("指定的图片路径不存在: " + this.imagePath);
        }
        
        // 解析可选参数
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--font-size":
                case "-s":
                    if (i + 1 < args.length) {
                        try {
                            this.fontSize = Integer.parseInt(args[++i]);
                            if (this.fontSize <= 0) {
                                throw new IllegalArgumentException("字体大小必须为正数");
                            }
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("无效的字体大小: " + args[i]);
                        }
                    } else {
                        throw new IllegalArgumentException("--font-size 参数需要一个值");
                    }
                    break;
                case "--font-color":
                case "-c":
                    if (i + 1 < args.length) {
                        String color = args[++i];
                        if (!color.matches("^#[0-9A-Fa-f]{6}$")) {
                            throw new IllegalArgumentException("无效的颜色格式，应为#RRGGBB: " + color);
                        }
                        this.fontColor = color;
                    } else {
                        throw new IllegalArgumentException("--font-color 参数需要一个值");
                    }
                    break;
                case "--position":
                case "-p":
                    if (i + 1 < args.length) {
                        try {
                            this.position = Position.valueOf(args[++i].toUpperCase());
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("无效的位置值: " + args[i] + ", 可用值: " + String.join(", ", getPositionValues()));
                        }
                    } else {
                        throw new IllegalArgumentException("--position 参数需要一个值");
                    }
                    break;
                case "--help":
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                default:
                    logger.log(Level.WARNING, "未知参数: " + args[i]);
                    break;
            }
        }
        
        logger.log(Level.INFO, "解析参数完成: 图片路径={0}, 字体大小={1}, 字体颜色={2}, 位置={3}", 
                new Object[]{imagePath, fontSize, fontColor, position});
    }
    
    private String[] getPositionValues() {
        Position[] positions = Position.values();
        String[] values = new String[positions.length];
        for (int i = 0; i < positions.length; i++) {
            values[i] = positions[i].name().toLowerCase();
        }
        return values;
    }
    
    public static void printHelp() {
        System.out.println("用法: java -jar PhotoWaterMark.jar <图片文件路径> [选项]");
        System.out.println("选项:");
        System.out.println("  --font-size, -s <大小>      设置水印字体大小 (默认: 24)");
        System.out.println("  --font-color, -c <颜色>     设置水印字体颜色 (格式: #RRGGBB, 默认: #000000)");
        System.out.println("  --position, -p <位置>       设置水印位置");
        System.out.println("                              可用位置: top_left, top_center, top_right,");
        System.out.println("                                      middle_left, middle_center, middle_right,");
        System.out.println("                                      bottom_left, bottom_center, bottom_right");
        System.out.println("  --help, -h                  显示此帮助信息");
    }
    
    // Getters
    public Path getImagePath() {
        return imagePath;
    }
    
    public int getFontSize() {
        return fontSize;
    }
    
    public String getFontColor() {
        return fontColor;
    }
    
    public Position getPosition() {
        return position;
    }
}