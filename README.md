# primihub meta
primihub meta is based on spring boot,use maven to compile and package.
## Getting Started
First of all ,when we run the project, we need some service dependencies like this:
- [jdk 1.8](https://www.oracle.com/java/technologies/javase/javase8u211-later-archive-downloads.html)
- [maven](https://maven.apache.org/download.cgi)
- [mysql 5.0+](https://dev.mysql.com/downloads/mysql)


##Modify Configuration
Now we should locate the next path:

    ./meta-api/src/main/resources/

edit the "application.yaml" and modify the configuration to be the configuration of service dependencies which you have deployed.

especially those items should be paid attention literally.

    server:
      port: 
    spring:
      profiles:
        active: 
    ...
    spring:
      datasource:
        druid:
          ...:
            username: 
            url: 
            password: 

then we should locate this path:

    ./script
        init.sh

to this path,you can execute next command:
    
    cd ./script
    sh init.sh [your mysql username] [your mysql password]

or you can execute "init.sql" in your mysql management manually.



## Compile and Package
run this command:

    mvn clean install -Dmaven.test.skip=true 

As long as the finished infos show up, the project have been compiled and packaged successfully.

## How to run
Before run, make sure that your service dependencies are available and the configuration is correct.

    java -jar -Dfile.encoding=UTF-8 ./meta-api/target/*-SNAPSHOT.jar --server.port=8099 --spring.cloud.nacos.discovery.server-addr=nacos:8848 --spring.cloud.nacos.discovery.namespace=demo --spring.cloud.nacos.config.server-addr=nacos:8848 --spring.cloud.nacos.config.namespace=demo

    execute that the command,you can check the url:
    
    http://localhost:8099/fusion/healthConnection

## 常见问题与解决方案

### 1. Maven安装问题
**问题**: `mvn: command not found`
**解决方案**:
```bash
# Ubuntu/Debian系统
sudo apt-get update
sudo apt-get install -y maven

# 验证安装
mvn -v
```

### 2. Maven settings.xml损坏
**问题**: `Non-readable settings /home/user/.m2/settings.xml`
**解决方案**:
```bash
# 删除损坏的settings.xml文件
rm ~/.m2/settings.xml
# Maven将使用默认配置
```

### 3. Protobuf编译错误
**问题**: `protoc did not exit cleanly` 或 `Syntax error: word unexpected`
**原因**: meta-grpc/pom.xml中硬编码了macOS的protoc配置
**解决方案**:
编辑 `meta-grpc/pom.xml`，将第17行修改为：
```xml
<os.detected.classifier>linux-x86_64</os.detected.classifier>
```
根据你的系统架构修改：
- Linux x86_64: `linux-x86_64`
- Linux ARM64: `linux-aarch_64`
- macOS: `osx-x86_64`

### 4. Java编译错误
**问题**: `No compiler is provided in this environment. Perhaps you are running on a JRE rather than a JDK?`
**解决方案**:
```bash
# 安装JDK（不仅仅是JRE）
sudo apt-get install -y openjdk-8-jdk

# 验证javac可用
javac -version
```

### 5. 网络连接问题
**问题**: Maven无法下载依赖或Docker无法拉取镜像
**解决方案**:
- 检查网络连接
- 使用国内镜像源（如阿里云Maven镜像）
- 对于Docker，可以配置镜像加速器

### 6. 构建成功验证
构建成功后，应生成以下jar文件：
- `meta-api/target/meta-api-1.0-SNAPSHOT.jar` (~74MB)
- `meta-grpc/target/meta-grpc-1.0-SNAPSHOT.jar` (~123KB)
- `meta-simple/target/meta-simple-1.0-SNAPSHOT.jar` (~48MB)

### 7. 快速构建命令
```bash
# 跳过测试的完整构建
mvn clean install -Dmaven.test.skip=true

# 仅编译不运行测试
mvn compile -DskipTests

# 重新构建单个模块
mvn clean install -Dmaven.test.skip=true -pl meta-api -am
```

### 8. 使用构建脚本
项目提供了一个构建脚本 `build.sh`，可以自动处理常见问题：

```bash
# 使脚本可执行
chmod +x build.sh

# 检查环境依赖
./build.sh check

# 构建项目（跳过测试）
./build.sh build --skip-tests

# 清理构建文件
./build.sh clean

# 显示帮助信息
./build.sh help
```

### 9. 系统要求
- **Java**: JDK 1.8+（必须安装JDK，不仅仅是JRE）
- **Maven**: 3.6+
- **操作系统**: Linux/macOS/Windows（需要相应调整protoc配置）

### 10. 快速开始
```bash
# 1. 克隆项目
git clone <repository-url>
cd primihub-meta

# 2. 使用构建脚本
chmod +x build.sh
./build.sh check
./build.sh build --skip-tests

# 3. 运行应用
java -jar -Dfile.encoding=UTF-8 ./meta-api/target/*-SNAPSHOT.jar --server.port=8099

# 4. 验证
curl http://localhost:8099/fusion/healthConnection
```
