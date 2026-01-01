#!/bin/bash

# Primihub Meta 项目构建脚本
# 此脚本解决构建过程中遇到的常见问题

set -e  # 遇到错误时退出

echo "=========================================="
echo "Primihub Meta 项目构建脚本"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        log_error "命令 '$1' 未找到"
        return 1
    fi
    return 0
}

# 修复 protobuf 配置
fix_protobuf_config() {
    local pom_file="meta-grpc/pom.xml"
    
    if [ ! -f "$pom_file" ]; then
        log_error "找不到文件: $pom_file"
        return 1
    fi
    
    # 检测操作系统
    local os_type=""
    local arch=$(uname -m)
    
    case "$(uname -s)" in
        Linux*)     os_type="linux" ;;
        Darwin*)    os_type="osx" ;;
        *)          os_type="unknown" ;;
    esac
    
    # 确定架构
    local classifier=""
    if [ "$os_type" = "linux" ]; then
        if [ "$arch" = "x86_64" ]; then
            classifier="linux-x86_64"
        elif [ "$arch" = "aarch64" ] || [ "$arch" = "arm64" ]; then
            classifier="linux-aarch_64"
        fi
    elif [ "$os_type" = "osx" ]; then
        if [ "$arch" = "x86_64" ]; then
            classifier="osx-x86_64"
        elif [ "$arch" = "arm64" ]; then
            classifier="osx-aarch_64"
        fi
    fi
    
    if [ -z "$classifier" ]; then
        log_warn "无法确定系统类型，使用默认配置"
        return 0
    fi
    
    log_info "检测到系统: $os_type, 架构: $arch"
    log_info "设置 protobuf classifier 为: $classifier"
    
    # 备份原文件
    cp "$pom_file" "${pom_file}.backup"
    
    # 更新配置
    sed -i "s|<os.detected.classifier>.*</os.detected.classifier>|<os.detected.classifier>$classifier</os.detected.classifier>|" "$pom_file"
    
    if grep -q "<os.detected.classifier>$classifier</os.detected.classifier>" "$pom_file"; then
        log_info "成功更新 protobuf 配置"
    else
        log_warn "未能自动更新配置，请手动检查 $pom_file"
        # 恢复备份
        mv "${pom_file}.backup" "$pom_file"
    fi
}

# 检查并修复 Maven settings.xml
fix_maven_settings() {
    local maven_dir="$HOME/.m2"
    local settings_file="$maven_dir/settings.xml"
    
    if [ -f "$settings_file" ]; then
        local file_size=$(stat -c%s "$settings_file" 2>/dev/null || stat -f%z "$settings_file" 2>/dev/null)
        if [ "$file_size" -lt 10 ]; then
            log_warn "检测到损坏的 Maven settings.xml 文件 (大小: ${file_size}字节)"
            mv "$settings_file" "${settings_file}.corrupted"
            log_info "已备份损坏的 settings.xml 文件"
        fi
    fi
}

# 检查 Java 环境
check_java_env() {
    log_info "检查 Java 环境..."
    
    # 检查 java 命令
    if ! check_command "java"; then
        log_error "Java 未安装"
        echo "请安装 Java:"
        echo "  Ubuntu/Debian: sudo apt-get install -y openjdk-8-jdk"
        echo "  CentOS/RHEL: sudo yum install -y java-1.8.0-openjdk-devel"
        return 1
    fi
    
    # 检查 javac 命令
    if ! check_command "javac"; then
        log_error "javac 未找到，可能只安装了 JRE 而不是 JDK"
        echo "请安装 JDK (不仅仅是 JRE):"
        echo "  Ubuntu/Debian: sudo apt-get install -y openjdk-8-jdk"
        echo "  CentOS/RHEL: sudo yum install -y java-1.8.0-openjdk-devel"
        return 1
    fi
    
    # 检查 Java 版本
    local java_version=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
    log_info "Java 版本: $java_version"
    
    # 检查是否为 JDK 1.8
    if [[ ! "$java_version" =~ ^1\.8 ]]; then
        log_warn "推荐使用 Java 1.8，当前版本: $java_version"
    fi
}

# 检查 Maven 环境
check_maven_env() {
    log_info "检查 Maven 环境..."
    
    if ! check_command "mvn"; then
        log_error "Maven 未安装"
        echo "请安装 Maven:"
        echo "  Ubuntu/Debian: sudo apt-get install -y maven"
        echo "  CentOS/RHEL: sudo yum install -y maven"
        return 1
    fi
    
    local maven_version=$(mvn -v 2>&1 | grep "Apache Maven" | awk '{print $3}')
    log_info "Maven 版本: $maven_version"
}

# 构建项目
build_project() {
    local skip_tests=""
    
    if [ "$1" = "--skip-tests" ] || [ "$1" = "-s" ]; then
        skip_tests="-Dmaven.test.skip=true"
        log_info "跳过测试"
    fi
    
    log_info "开始构建项目..."
    
    # 修复常见问题
    fix_maven_settings
    fix_protobuf_config
    
    # 执行构建
    if [ -n "$skip_tests" ]; then
        mvn clean install $skip_tests
    else
        mvn clean install
    fi
    
    if [ $? -eq 0 ]; then
        log_info "构建成功!"
        
        # 显示生成的 jar 文件
        echo ""
        log_info "生成的 jar 文件:"
        find . -name "*.jar" -type f | grep -v ".m2" | while read jar_file; do
            local size=$(ls -lh "$jar_file" | awk '{print $5}')
            echo "  - $jar_file ($size)"
        done
        
        echo ""
        log_info "运行应用程序:"
        echo "  java -jar -Dfile.encoding=UTF-8 ./meta-api/target/*-SNAPSHOT.jar --server.port=8099"
    else
        log_error "构建失败"
        return 1
    fi
}

# 清理构建
clean_build() {
    log_info "清理构建文件..."
    mvn clean
    
    # 删除生成的 jar 文件
    find . -name "*.jar" -type f | grep -v ".m2" | xargs rm -f 2>/dev/null || true
    
    log_info "清理完成"
}

# 显示帮助信息
show_help() {
    echo "使用方法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  build, -b          构建项目（默认）"
    echo "  build --skip-tests 构建项目并跳过测试"
    echo "  clean, -c          清理构建文件"
    echo "  check, -k          检查环境依赖"
    echo "  help, -h           显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 build           构建项目"
    echo "  $0 build --skip-tests 构建项目并跳过测试"
    echo "  $0 clean           清理构建文件"
    echo "  $0 check           检查环境依赖"
}

# 主函数
main() {
    local action="build"
    
    case "$1" in
        "build"|"-b")
            action="build"
            shift
            ;;
        "clean"|"-c")
            action="clean"
            ;;
        "check"|"-k")
            action="check"
            ;;
        "help"|"-h"|"--help")
            show_help
            exit 0
            ;;
        "")
            action="build"
            ;;
        *)
            log_error "未知选项: $1"
            show_help
            exit 1
            ;;
    esac
    
    case "$action" in
        "build")
            check_java_env
            check_maven_env
            build_project "$@"
            ;;
        "clean")
            clean_build
            ;;
        "check")
            check_java_env
            check_maven_env
            log_info "环境检查完成"
            ;;
    esac
}

# 执行主函数
main "$@"