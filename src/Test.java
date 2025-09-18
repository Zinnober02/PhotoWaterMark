import java.io.File;
import java.io.IOException;
import com.drew.imaging.ImageProcessingException;

public class Test {
    public static void main(String[] args) {
        try {
            // 使用src目录下的1.jpg文件作为测试
            String testImagePath = "src/1.jpg";
            File testImage = new File(testImagePath);
            
            if (!testImage.exists()) {
                System.out.println("测试图片不存在: " + testImagePath);
                System.out.println("请确保在src目录下有一个名为1.jpg的测试图片文件");
                return;
            }
            
            System.out.println("开始测试图片水印功能...");
            
            // 创建命令行参数数组
            String[] commandLineArgs = {testImagePath};
            
            // 解析命令行参数
            CommandLineParser parser = new CommandLineParser(commandLineArgs);
            PhotoWatermarkProcessor processor = new PhotoWatermarkProcessor(parser);
            
            // 处理图片
            processor.processPhotos();
            
            System.out.println("测试完成！请检查输出目录中的图片。");
            
        } catch (IllegalArgumentException e) {
            System.err.println("参数错误: " + e.getMessage());
            CommandLineParser.printHelp();
        } catch (ImageProcessingException e) {
            System.err.println("图像处理错误: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO错误: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("处理过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}