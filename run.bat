@echo off

rem 设置类路径分隔符（Windows使用分号）
set "CLASSPATH_SEPARATOR=;"

rem 创建target目录（如果不存在）
mkdir target 2>nul

rem 编译Java源文件
echo 编译Java源文件...
javac -cp "src" -d "target" src\*.java

if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

rem 检查是否提供了图片路径参数
if "%1" == "" (
    echo 用法: %0 ^<图片文件路径^> [选项]
    echo 选项:
    echo   --font-size, -s ^<大小^>      设置水印字体大小 (默认: 24)
    echo   --font-color, -c ^<颜色^>     设置水印字体颜色 (格式: #RRGGBB, 默认: #000000)
    echo   --position, -p ^<位置^>       设置水印位置
    echo                              可用位置: top_left, top_center, top_right,
    echo                                      middle_left, middle_center, middle_right,
    echo                                      bottom_left, bottom_center, bottom_right
    echo   --help, -h                  显示此帮助信息
    pause
    exit /b 1
)

rem 检查lib目录是否存在
if not exist "lib" (
    echo 警告: lib目录不存在！
    echo 请手动下载以下依赖库并放入lib目录:
    echo 1. metadata-extractor-2.18.0.jar
    echo 2. xmpcore-6.1.11.jar
    echo 
    echo 下载地址:
    echo - metadata-extractor: https://github.com/drewnoakes/metadata-extractor/releases
    echo - xmpcore: https://mvnrepository.com/artifact/com.adobe.xmp/xmpcore
    pause
)

rem 运行主程序
echo 运行程序...
java -cp "target%CLASSPATH_SEPARATOR%lib\*" Main %*

if %errorlevel% neq 0 (
    echo 程序执行失败！
    pause
    exit /b 1
)

echo 程序执行完成！请检查输出目录中的图片。
pause