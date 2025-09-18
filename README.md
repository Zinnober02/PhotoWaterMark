# PhotoWaterMark

一个基于Java 17+的命令行工具，用于给图片添加拍摄时间水印。

## 功能特点

- 读取图片的EXIF信息中的拍摄时间，并将年月日作为水印
- 支持自定义水印字体大小、颜色和位置
- 可以处理单个图片文件或整个目录中的所有图片
- 处理后的图片保存在原目录名_watermark的新目录下

## 支持的图片格式

- JPG/JPEG
- PNG
- GIF
- BMP

## 依赖

- Java 17 或更高版本
- Maven 3.6 或更高版本
- metadata-extractor 库 (用于读取EXIF信息)

## 构建项目

1. 确保已安装Java 17和Maven
2. 克隆或下载此项目
3. 进入项目根目录
4. 执行以下命令构建项目：

```bash
mvn clean package
```

构建成功后，会在`target`目录下生成可执行的jar文件：`PhotoWaterMark-1.0-SNAPSHOT-jar-with-dependencies.jar`

## 使用方法

### 基本语法

```bash
java -jar PhotoWaterMark-1.0-SNAPSHOT-jar-with-dependencies.jar <图片文件路径> [选项]
```

### 选项

- `--font-size, -s <大小>`: 设置水印字体大小 (默认: 24)
- `--font-color, -c <颜色>`: 设置水印字体颜色 (格式: #RRGGBB, 默认: #000000)
- `--position, -p <位置>`: 设置水印位置
  可用位置: top_left, top_center, top_right,
           middle_left, middle_center, middle_right,
           bottom_left, bottom_center, bottom_right
- `--help, -h`: 显示帮助信息

### 示例

1. 处理单个图片文件：

```bash
java -jar PhotoWaterMark-1.0-SNAPSHOT-jar-with-dependencies.jar /path/to/image.jpg
```

2. 处理整个目录中的所有图片：

```bash
java -jar PhotoWaterMark-1.0-SNAPSHOT-jar-with-dependencies.jar /path/to/images
```

3. 自定义水印样式：

```bash
java -jar PhotoWaterMark-1.0-SNAPSHOT-jar-with-dependencies.jar /path/to/image.jpg --font-size 36 --font-color #FF0000 --position bottom_right
```

## 输出

处理后的图片会保存在与原目录同名但后缀为`_watermark`的子目录中。例如，如果原图片在`photos`目录下，则处理后的图片会保存在`photos_watermark`目录下。

## 注意事项

- 程序会尝试读取图片的EXIF信息中的拍摄时间，如果无法读取到该信息，则不会为该图片添加水印
- 对于不包含EXIF信息的图片（如PNG、GIF等格式），程序将无法添加水印
- 请确保有足够的磁盘空间来保存处理后的图片

## 许可证

[MIT License](LICENSE)