@echo off

rem 设置类路径分隔符（Windows使用分号）
set "CLASSPATH_SEPARATOR=;"

rem 检查是否存在测试图片
if not exist "src\1.jpg" (
    echo 错误: 测试图片不存在！
    echo 请确保在src目录下有一个名为1.jpg的测试图片文件
    pause
    exit /b 1
)

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

rem 下载依赖库（如果需要）
if not exist "lib" (
    echo 正在创建lib目录...
    mkdir lib
    
    rem 这里假设用户已经通过Maven下载了依赖库
    rem 如果没有Maven，可以手动下载metadata-extractor和xmpcore库
    echo 警告: 请手动下载以下依赖库并放入lib目录:
    echo 1. metadata-extractor-2.18.0.jar
    echo 2. xmpcore-6.1.11.jar
    echo 
    echo 下载地址:
    echo - metadata-extractor: https://github.com/drewnoakes/metadata-extractor/releases
    echo - xmpcore: https://mvnrepository.com/artifact/com.adobe.xmp/xmpcore
    pause
)

rem 运行测试
echo 运行测试...
java -cp "target%CLASSPATH_SEPARATOR%lib\*" Test

if %errorlevel% neq 0 (
    echo 测试失败！
    pause
    exit /b 1
)

echo 测试完成！请检查输出目录中的图片。
pause