#!/bin/bash
# primihub-meta 编译和构建镜像脚本
# 基于Jenkins配置生成

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 默认配置
DEFAULT_REGISTRY="192.168.99.10/primihub"
DEFAULT_IMAGE_NAME="meta"
BUILD_NUMBER=${BUILD_NUMBER:-$(date +%Y%m%d%H%M%S)}
DOCKERFILE="Dockerfile.local"

# 打印带颜色的消息
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 显示使用帮助
show_help() {
    cat << EOF
用法: $0 [选项]

选项:
    -r, --registry REGISTRY    Docker镜像仓库地址 (默认: ${DEFAULT_REGISTRY})
    -n, --name NAME            镜像名称 (默认: ${DEFAULT_IMAGE_NAME})
    -t, --tag TAG              镜像标签 (默认: ${BUILD_NUMBER})
    -f, --dockerfile FILE      Dockerfile文件 (默认: ${DOCKERFILE})
    -s, --skip-build           跳过Maven编译步骤
    -p, --push                 构建后推送镜像到仓库
    -c, --clean                构建前清理target目录
    -h, --help                 显示此帮助信息

示例:
    # 使用默认配置编译和构建镜像
    $0

    # 指定自定义标签
    $0 -t v1.0.0

    # 跳过编译直接构建镜像
    $0 -s

    # 构建并推送镜像
    $0 -p -t latest

    # 使用自定义仓库和标签
    $0 -r docker.io/myrepo -n primihub-meta -t v1.2.3 -p

EOF
}

# 检查依赖
check_dependencies() {
    print_info "检查依赖..."

    local missing_deps=()

    if ! command -v mvn &> /dev/null; then
        missing_deps+=("maven")
    fi

    if ! command -v docker &> /dev/null; then
        missing_deps+=("docker")
    fi

    if [ ${#missing_deps[@]} -ne 0 ]; then
        print_error "缺少依赖: ${missing_deps[*]}"
        print_error "请先安装所需依赖后再运行此脚本"
        exit 1
    fi

    print_info "依赖检查通过"
}

# Maven编译
maven_build() {
    print_info "开始Maven编译..."
    print_info "执行命令: mvn clean install -Dmaven.test.skip=true -Dasciidoctor.skip=true -Dos.detected.classifier=linux-x86_64"

    if [ "$CLEAN_BUILD" = true ]; then
        print_info "清理构建目录..."
        mvn clean
    fi

    mvn clean install \
        -Dmaven.test.skip=true \
        -Dasciidoctor.skip=true \
        -Dos.detected.classifier=linux-x86_64

    if [ $? -eq 0 ]; then
        print_info "Maven编译成功"
    else
        print_error "Maven编译失败"
        exit 1
    fi
}

# 检查编译产物
check_artifacts() {
    print_info "检查编译产物..."

    local artifacts=(
        "meta-api/target/meta-api-1.0-SNAPSHOT.jar"
        "meta-simple/target/meta-simple-1.0-SNAPSHOT.jar"
    )

    local missing=()
    for artifact in "${artifacts[@]}"; do
        if [ ! -f "$artifact" ]; then
            missing+=("$artifact")
        fi
    done

    if [ ${#missing[@]} -ne 0 ]; then
        print_error "缺少编译产物:"
        for item in "${missing[@]}"; do
            echo "  - $item"
        done
        print_error "请先执行Maven编译"
        exit 1
    fi

    print_info "编译产物检查通过"
}

# 构建Docker镜像
build_docker_image() {
    local full_image_name="${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"

    print_info "开始构建Docker镜像..."
    print_info "镜像名称: ${full_image_name}"
    print_info "Dockerfile: ${DOCKERFILE}"

    if [ ! -f "${DOCKERFILE}" ]; then
        print_error "Dockerfile不存在: ${DOCKERFILE}"
        exit 1
    fi

    docker build -f "${DOCKERFILE}" -t "${full_image_name}" .

    if [ $? -eq 0 ]; then
        print_info "Docker镜像构建成功: ${full_image_name}"
    else
        print_error "Docker镜像构建失败"
        exit 1
    fi

    # 显示镜像信息
    print_info "镜像信息:"
    docker images "${REGISTRY}/${IMAGE_NAME}" | grep "${IMAGE_TAG}"
}

# 推送镜像
push_docker_image() {
    local full_image_name="${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"

    print_info "推送镜像到仓库: ${full_image_name}"

    docker push "${full_image_name}"

    if [ $? -eq 0 ]; then
        print_info "镜像推送成功"
    else
        print_error "镜像推送失败"
        exit 1
    fi
}

# 主函数
main() {
    # 默认值
    REGISTRY="${DEFAULT_REGISTRY}"
    IMAGE_NAME="${DEFAULT_IMAGE_NAME}"
    IMAGE_TAG="${BUILD_NUMBER}"
    SKIP_BUILD=false
    PUSH_IMAGE=false
    CLEAN_BUILD=false

    # 解析参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            -r|--registry)
                REGISTRY="$2"
                shift 2
                ;;
            -n|--name)
                IMAGE_NAME="$2"
                shift 2
                ;;
            -t|--tag)
                IMAGE_TAG="$2"
                shift 2
                ;;
            -f|--dockerfile)
                DOCKERFILE="$2"
                shift 2
                ;;
            -s|--skip-build)
                SKIP_BUILD=true
                shift
                ;;
            -p|--push)
                PUSH_IMAGE=true
                shift
                ;;
            -c|--clean)
                CLEAN_BUILD=true
                shift
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            *)
                print_error "未知选项: $1"
                show_help
                exit 1
                ;;
        esac
    done

    print_info "=========================================="
    print_info "primihub-meta 编译和构建脚本"
    print_info "=========================================="
    print_info "仓库地址: ${REGISTRY}"
    print_info "镜像名称: ${IMAGE_NAME}"
    print_info "镜像标签: ${IMAGE_TAG}"
    print_info "Dockerfile: ${DOCKERFILE}"
    print_info "跳过编译: ${SKIP_BUILD}"
    print_info "推送镜像: ${PUSH_IMAGE}"
    print_info "=========================================="

    # 检查依赖
    check_dependencies

    # Maven编译
    if [ "$SKIP_BUILD" = false ]; then
        maven_build
    else
        print_warn "跳过Maven编译步骤"
        check_artifacts
    fi

    # 构建Docker镜像
    build_docker_image

    # 推送镜像
    if [ "$PUSH_IMAGE" = true ]; then
        push_docker_image
    else
        print_info "跳过镜像推送 (使用 -p 或 --push 参数可推送镜像)"
    fi

    print_info "=========================================="
    print_info "构建完成!"
    print_info "镜像: ${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"

    if [ "$PUSH_IMAGE" = false ]; then
        print_info ""
        print_info "运行镜像:"
        print_info "  docker run -p 8080:8080 ${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
    fi
    print_info "=========================================="
}

# 执行主函数
main "$@"
