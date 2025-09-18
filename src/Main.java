import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        try {
            // 解析命令行参数
            CommandLineParser parser = new CommandLineParser(args);
            PhotoWatermarkProcessor processor = new PhotoWatermarkProcessor(parser);
            
            // 处理图片
            processor.processPhotos();
            
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "参数错误: " + e.getMessage());
            CommandLineParser.printHelp();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "处理过程中发生错误: " + e.getMessage(), e);
        }
    }
}