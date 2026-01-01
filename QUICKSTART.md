# Primihub Meta 快速开始指南

## 一键构建和运行

### 方法1: 使用构建脚本（推荐）
```bash
# 1. 使脚本可执行
chmod +x build.sh

# 2. 检查环境
./build.sh check

# 3. 构建项目（跳过测试）
./build.sh build --skip-tests

# 4. 运行应用
java -jar -Dfile.encoding=UTF-8 ./meta-api/target/*-SNAPSHOT.jar --server.port=8099
```

### 方法2: 手动构建
```bash
# 1. 安装依赖
sudo apt-get update
sudo apt-get install -y maven openjdk-8-jdk

# 2. 修复 protobuf 配置（如果需要）
# 编辑 meta-grpc/pom.xml，确保 os.detected.classifier 正确
# Linux x86_64: linux-x86_64
# Linux ARM64: linux-aarch_64
# macOS: osx-x86_64

# 3. 构建项目
mvn clean install -Dmaven.test.skip=true

# 4. 运行应用
java -jar -Dfile.encoding=UTF-8 ./meta-api/target/*-SNAPSHOT.jar --server.port=8099
```

## 验证安装

### 1. 检查构建结果
构建成功后应生成以下文件：
```
meta-api/target/meta-api-1.0-SNAPSHOT.jar      (~74MB)
meta-grpc/target/meta-grpc-1.0-SNAPSHOT.jar    (~123KB)
meta-simple/target/meta-simple-1.0-SNAPSHOT.jar (~48MB)
```

### 2. 验证应用运行
```bash
# 运行应用后，在另一个终端中执行：
curl http://localhost:8099/fusion/healthConnection

# 预期输出：应用状态信息
```

## 常见问题速查

### 问题1: `mvn: command not found`
```bash
sudo apt-get install -y maven
```

### 问题2: `No compiler is provided in this environment`
```bash
# 安装 JDK（不仅仅是 JRE）
sudo apt-get install -y openjdk-8-jdk
```

### 问题3: `protoc did not exit cleanly`
编辑 `meta-grpc/pom.xml`，修改第17行：
```xml
<os.detected.classifier>linux-x86_64</os.detected.classifier>
```
根据你的系统调整：
- Linux x86_64: `linux-x86_64`
- Linux ARM64: `linux-aarch_64`
- macOS: `osx-x86_64`

### 问题4: Maven settings.xml 损坏
```bash
rm ~/.m2/settings.xml
```

## 构建脚本功能

### 检查环境
```bash
./build.sh check
```
检查 Java 和 Maven 环境是否配置正确。

### 构建项目
```bash
# 包含测试
./build.sh build

# 跳过测试（推荐）
./build.sh build --skip-tests
```

### 清理构建
```bash
./build.sh clean
```

### 获取帮助
```bash
./build.sh help
```

## 开发命令

### 重新构建单个模块
```bash
mvn clean install -Dmaven.test.skip=true -pl meta-api -am
```

### 仅编译不打包
```bash
mvn compile
```

### 运行测试
```bash
mvn test
```

## 配置数据库

### 初始化数据库
```bash
cd script
# 使用默认用户名 root
sh init.sh root your_password

# 或指定用户名
sh init.sh username password
```

### 手动执行 SQL
```mysql
-- 创建数据库
DROP database IF EXISTS meta;
CREATE database meta;
USE meta;

-- 执行 init.sql 中的 SQL 语句
-- 文件位置: script/init.sql
```

## 端口配置

默认端口：8099

修改端口：
```bash
java -jar -Dfile.encoding=UTF-8 ./meta-api/target/*-SNAPSHOT.jar --server.port=8080
```

## 日志查看

应用运行时，日志输出到控制台。如需查看详细日志，检查应用配置中的日志设置。

---

**提示**: 遇到问题时，首先运行 `./build.sh check` 检查环境，然后查看 README.md 中的"常见问题与解决方案"部分。