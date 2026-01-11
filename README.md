# PrimiHub Meta

PrimiHub Meta 是一个基于 Spring Boot 的元数据管理系统，用于隐私计算和联邦学习场景下的数据资源管理。

## 项目简介

PrimiHub Meta 提供了完整的数据资源元数据管理能力，支持跨组织的数据共享与协作。系统采用微服务架构，同时支持 REST API 和 gRPC 协议。

### 核心功能

- **数据资源管理** - 注册、查询、更新和删除数据资源
- **数据集管理** - 管理数据集的访问信息和元数据
- **组织管理** - 支持多组织隔离和跨组织协作
- **权限控制** - 支持公开、私有和可见性三种权限模型
- **标签管理** - 数据资源的分类和检索
- **数据融合** - 跨组织数据共享和融合

### 项目结构

```
primihub-meta/
├── meta-api/          # REST API主服务模块
│   ├── Controller     # FusionController, FusionResourceController等
│   ├── Service        # 资源管理、组织管理等业务服务
│   └── Entity         # 数据实体和DTO
├── meta-simple/       # 简化版数据集管理模块（SQLite）
│   └── Controller     # 数据集REST接口
├── meta-grpc/         # gRPC服务模块
│   └── proto          # Protocol Buffer定义
└── script/            # 数据库初始化脚本
```

### 技术栈

- **后端框架**: Spring Boot 2.3.8
- **数据库**: MySQL 5.0+ / SQLite 3.x
- **持久层**: MyBatis 3.4.1 + MyBatis Plus 3.4.3
- **通信协议**: REST API + gRPC
- **服务注册**: Nacos
- **构建工具**: Maven 3.6+
- **运行环境**: JDK 1.8+

## 环境要求

在运行项目之前，需要安装以下依赖：

- **[JDK 1.8+](https://www.oracle.com/java/technologies/javase/javase8u211-later-archive-downloads.html)** - Java开发工具包（必须是JDK，不仅仅是JRE）
- **[Maven 3.6+](https://maven.apache.org/download.cgi)** - 项目构建工具
- **[MySQL 5.0+](https://dev.mysql.com/downloads/mysql)** - 数据库（meta-api模块需要）
- **[Docker](https://docs.docker.com/get-docker/)** - 容器化部署（可选）

## 快速开始

### 方法一：使用自动化脚本（推荐）

项目提供了自动化构建脚本，可以快速完成编译、打包和Docker镜像构建：

```bash
# 1. 克隆项目
git clone <repository-url>
cd primihub-meta

# 2. 检查环境依赖（可选）
./build.sh check

# 3. 编译项目
./build.sh build --skip-tests

# 4. 编译并构建Docker镜像
./build-and-push.sh

# 5. 使用自定义标签构建镜像
./build-and-push.sh -t v1.0.0

# 6. 构建并推送到镜像仓库
./build-and-push.sh -t latest -p

# 查看更多选项
./build-and-push.sh --help
```

### 方法二：手动编译和部署

#### 1. 配置数据库

首先定位到配置文件目录：

```bash
cd ./meta-api/src/main/resources/
```

编辑 `application.yaml` 文件，修改数据库配置：

```yaml
server:
  port: 8099  # 服务端口

spring:
  profiles:
    active: dev  # 激活的配置文件

  datasource:
    druid:
      username: your_mysql_username
      password: your_mysql_password
      url: jdbc:mysql://localhost:3306/fusion?useUnicode=true&characterEncoding=utf-8&useSSL=false
```

#### 2. 初始化数据库

执行数据库初始化脚本：

```bash
cd ./script

# 使用脚本初始化（推荐）
sh init.sh [your_mysql_username] [your_mysql_password]

# 或者手动执行SQL文件
mysql -u username -p fusion < init.sql
```

#### 3. 编译和打包

使用 Maven 编译项目：

```bash
# 跳过测试编译
mvn clean install -Dmaven.test.skip=true

# 或使用build.sh脚本
chmod +x build.sh
./build.sh build --skip-tests
```

编译成功后，会生成以下jar文件：
- `meta-api/target/meta-api-1.0-SNAPSHOT.jar` (~74MB)
- `meta-simple/target/meta-simple-1.0-SNAPSHOT.jar` (~48MB)
- `meta-grpc/target/meta-grpc-1.0-SNAPSHOT.jar` (~123KB)

#### 4. 运行应用

确保依赖服务（MySQL、Nacos）已启动并正确配置，然后运行：

```bash
# 运行 meta-api 服务
java -jar -Dfile.encoding=UTF-8 \
  ./meta-api/target/meta-api-1.0-SNAPSHOT.jar \
  --server.port=8099 \
  --spring.cloud.nacos.discovery.server-addr=nacos:8848 \
  --spring.cloud.nacos.discovery.namespace=demo \
  --spring.cloud.nacos.config.server-addr=nacos:8848 \
  --spring.cloud.nacos.config.namespace=demo

# 验证服务是否启动成功
curl http://localhost:8099/fusion/healthConnection
```

## Docker部署

### 构建Docker镜像

项目提供了两种Dockerfile：

1. **Dockerfile** - 多阶段构建，包含编译和运行
2. **Dockerfile.local** - 基于已编译的jar包构建

使用自动化脚本构建镜像（推荐）：

```bash
# 编译并构建镜像（使用Dockerfile.local）
./build-and-push.sh

# 使用自定义配置
./build-and-push.sh \
  -r docker.io/myrepo \
  -n primihub-meta \
  -t v1.0.0 \
  -p  # 推送到镜像仓库
```

或手动构建：

```bash
# 先编译项目
mvn clean install -Dmaven.test.skip=true -Dos.detected.classifier=linux-x86_64

# 构建镜像（使用Dockerfile.local）
docker build -f Dockerfile.local -t primihub/meta:latest .

# 或使用多阶段构建（自动编译）
docker build -f Dockerfile -t primihub/meta:latest .
```

### 运行Docker容器

```bash
# 运行容器
docker run -d \
  --name primihub-meta \
  -p 8080:8080 \
  -e JAVA_OPTS="-Xms512m -Xmx1024m" \
  192.168.99.10/primihub/meta:latest

# 查看日志
docker logs -f primihub-meta

# 进入容器
docker exec -it primihub-meta /bin/bash
```

## 主要API接口

### 健康检查

```bash
GET /fusion/healthConnection
```

### 资源管理

```bash
# 查询资源列表
GET /fusionResource/getResourceList?pageNo=1&pageSize=10&globalId=your_org_id

# 查询资源详情
GET /fusionResource/getDataResource?resourceId=your_resource_id

# 保存资源
POST /fusionResource/saveResource
Content-Type: application/json
{
  "resourceList": [...]
}

# 获取资源标签
GET /fusionResource/getResourceTagList
```

### 组织管理

```bash
# 获取/创建组织
GET /fusion/organData?globalId=your_org_id&globalName=your_org_name
```

### 数据集管理（meta-simple）

```bash
# 保存单个数据集
POST /dataset/one
Content-Type: application/json

# 批量保存数据集
POST /dataset/many
Content-Type: application/json

# 删除数据集
POST /dataset/delete?id=dataset_id

# 健康检查
GET /health
```

## 构建脚本说明

### build.sh - Maven构建脚本

```bash
# 检查环境依赖
./build.sh check

# 编译项目（跳过测试）
./build.sh build --skip-tests

# 清理构建文件
./build.sh clean

# 显示帮助
./build.sh help
```

### build-and-push.sh - Docker镜像构建脚本

基于Jenkins配置生成的自动化脚本，支持以下功能：

```bash
# 基本用法
./build-and-push.sh [选项]

# 选项说明
-r, --registry REGISTRY    # Docker镜像仓库地址（默认：192.168.99.10/primihub）
-n, --name NAME            # 镜像名称（默认：meta）
-t, --tag TAG              # 镜像标签（默认：时间戳）
-f, --dockerfile FILE      # Dockerfile文件（默认：Dockerfile.local）
-s, --skip-build           # 跳过Maven编译步骤
-p, --push                 # 构建后推送镜像到仓库
-c, --clean                # 构建前清理target目录
-h, --help                 # 显示帮助信息

# 使用示例
./build-and-push.sh                           # 默认配置构建
./build-and-push.sh -t v1.0.0                 # 指定标签
./build-and-push.sh -s                        # 跳过编译，仅构建镜像
./build-and-push.sh -p -t latest              # 构建并推送
./build-and-push.sh -r docker.io/myrepo -n primihub-meta -t v1.2.3 -p
```

该脚本执行流程：
1. 检查依赖（Maven、Docker）
2. 执行Maven编译（与Jenkins配置一致）
3. 检查编译产物
4. 构建Docker镜像
5. 推送镜像到仓库（可选）

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

完整的快速开始指南：

```bash
# 1. 克隆项目
git clone <repository-url>
cd primihub-meta

# 2. 使用构建脚本（推荐）
chmod +x build.sh
./build.sh check
./build.sh build --skip-tests

# 3. 配置数据库（修改application.yaml）
cd meta-api/src/main/resources/
# 编辑 application.yaml 配置数据库连接
cd ../../..

# 4. 初始化数据库
cd script
sh init.sh your_mysql_username your_mysql_password
cd ..

# 5. 运行应用
java -jar -Dfile.encoding=UTF-8 ./meta-api/target/*-SNAPSHOT.jar --server.port=8099

# 6. 验证
curl http://localhost:8099/fusion/healthConnection
```

## 权限模型

系统支持三种资源权限类型：

| 权限类型 | 说明 | 适用场景 |
|---------|------|---------|
| **PUBLIC (公开)** | 所有组织可见 | 公开数据集 |
| **PRIVATE (私有)** | 仅所属组织可见 | 组织内部数据 |
| **VISIBILITY (可见性)** | 指定组织可见 | 跨组织协作数据 |

## 数据库表结构

主要数据表：

- **fusion_resource** - 资源主表
- **fusion_resource_field** - 资源字段表
- **fusion_resource_visibility_auth** - 可见性权限表
- **fusion_resource_tag** - 资源标签表
- **fusion_organ** - 组织表
- **data_set** - 数据集表

详细的表结构请查看 `script/init.sql` 文件。

## 开发指南

### 添加新的资源类型

1. 在 `ResourceTypeEnum` 中添加新类型
2. 更新数据库表结构（如需要）
3. 在 `ResourceService` 中添加处理逻辑
4. 更新 API 文档

### 扩展gRPC服务

1. 修改 `meta-grpc/src/main/proto/dataset.proto`
2. 运行 `mvn clean compile` 生成Java类
3. 在 `DataGrpcService` 中实现新接口
4. 更新客户端调用代码

## 贡献指南

欢迎提交 Issue 和 Pull Request 来帮助改进项目。

## 许可证

本项目采用 Apache License 2.0 许可证，详见 [LICENSE](LICENSE) 文件。

## 联系方式

- 项目主页：[PrimiHub](https://github.com/primihub)
- 问题反馈：通过 GitHub Issues 提交
