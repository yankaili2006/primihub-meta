.PHONY: build test clean run docker-build docker-up docker-up-full help

help:
	@echo "primihub-meta 管理命令"
	@echo ""
	@echo "用法: make <target>"
	@echo ""
	@echo "构建:"
	@echo "  build           编译全部模块并运行测试"
	@echo "  build-api       编译 meta-api 模块"
	@echo "  build-simple    编译 meta-simple 模块"
	@echo "  build-quick     编译全部模块, 跳过测试"
	@echo "  clean           清理构建产物"
	@echo ""
	@echo "测试:"
	@echo "  test            运行全部测试"
	@echo "  test-api        运行 meta-api 测试"
	@echo "  test-simple     运行 meta-simple 测试"
	@echo ""
	@echo "Docker:"
	@echo "  docker-build        构建全部 Docker 镜像"
	@echo "  docker-build-api    构建 meta-api 镜像"
	@echo "  docker-build-simple 构建 meta-simple 镜像"
	@echo "  docker-up           启动 meta-simple 服务 (docker-compose)"
	@echo "  docker-up-full      启动 meta-api 全套服务 (MySQL+Nacos)"
	@echo "  docker-down         停止服务"
	@echo ""
	@echo "部署:"
	@echo "  deploy-simple   编译 -> 构建 -> 推送 meta-simple 镜像"

build:
	mvn clean install

build-api:
	mvn clean install -pl meta-grpc,meta-api -am

build-simple:
	mvn clean install -pl meta-grpc,meta-simple -am

build-quick:
	mvn clean install -Dmaven.test.skip=true

test:
	mvn test

test-api:
	mvn test -pl meta-grpc,meta-api -am

test-simple:
	mvn test -pl meta-grpc,meta-simple -am

clean:
	mvn clean

docker-build:
	./build-and-push.sh -s

docker-build-api:
	./build-and-push.sh -s -m api -f Dockerfile.meta-api

docker-build-simple:
	./build-and-push.sh -s -m simple -f Dockerfile.meta-simple

docker-up:
	docker compose up -d

docker-up-full:
	docker compose -f docker-compose.full.yaml up -d

docker-down:
	docker compose down

deploy-simple:
	./build-and-push.sh -m simple -p
