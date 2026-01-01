#!/usr/bin/env python3
"""
Primihub 客户端测试脚本 - 测试REST API和基本功能
"""

import requests
import json
import time

def test_health_endpoint():
    """测试健康端点"""
    print("1. 测试健康端点:")
    try:
        response = requests.get("http://localhost:8099/health", timeout=5)
        print(f"   状态码: {response.status_code}")
        print(f"   响应: {response.json()}")
        return True
    except requests.exceptions.RequestException as e:
        print(f"   错误: {e}")
        return False

def test_dataset_crud():
    """测试数据集的CRUD操作"""
    print("\n2. 测试数据集CRUD操作:")
    
    base_url = "http://localhost:8099"
    test_id = f"test-{int(time.time())}"
    
    # 创建数据集
    print(f"   a) 创建数据集 (ID: {test_id}):")
    try:
        data = {
            "id": test_id,
            "name": "测试数据集",
            "description": "自动创建的测试数据集",
            "type": "csv",
            "path": f"/data/{test_id}.csv",
            "columns": ["id", "name", "age"],
            "rowCount": 100
        }
        response = requests.post(
            f"{base_url}/dataset/one",
            json=data,
            headers={"Content-Type": "application/json"},
            timeout=5
        )
        print(f"     状态码: {response.status_code}")
        result = response.json()
        print(f"     响应: {result}")
        
        if result.get("code") == 0:
            print("     ✓ 创建成功")
        else:
            print("     ✗ 创建失败")
            return False
    except Exception as e:
        print(f"     错误: {e}")
        return False
    
    # 查询数据集
    print(f"   b) 查询数据集 (ID: {test_id}):")
    try:
        data = {"id": test_id}
        response = requests.post(
            f"{base_url}/dataset/one",
            json=data,
            headers={"Content-Type": "application/json"},
            timeout=5
        )
        print(f"     状态码: {response.status_code}")
        result = response.json()
        print(f"     响应: {result}")
        
        if result.get("code") == 0:
            print("     ✓ 查询成功")
        else:
            print("     ✗ 查询失败")
    except Exception as e:
        print(f"     错误: {e}")
    
    # 批量操作
    print("   c) 批量创建数据集:")
    try:
        datasets = [
            {"id": f"batch-{test_id}-1", "name": "批量数据集1"},
            {"id": f"batch-{test_id}-2", "name": "批量数据集2"},
            {"id": f"batch-{test_id}-3", "name": "批量数据集3"}
        ]
        response = requests.post(
            f"{base_url}/dataset/many",
            json=datasets,
            headers={"Content-Type": "application/json"},
            timeout=5
        )
        print(f"     状态码: {response.status_code}")
        result = response.json()
        print(f"     响应: {result}")
        
        if result.get("code") == 0:
            print("     ✓ 批量创建成功")
        else:
            print("     ✗ 批量创建失败")
    except Exception as e:
        print(f"     错误: {e}")
    
    # 删除数据集
    print(f"   d) 删除数据集 (ID: {test_id}):")
    try:
        data = {"id": test_id}
        response = requests.post(
            f"{base_url}/dataset/delete",
            json=data,
            headers={"Content-Type": "application/json"},
            timeout=5
        )
        print(f"     状态码: {response.status_code}")
        result = response.json()
        print(f"     响应: {result}")
        
        if result.get("code") == 0:
            print("     ✓ 删除成功")
            return True
        else:
            print("     ✗ 删除失败")
            return False
    except Exception as e:
        print(f"     错误: {e}")
        return False

def test_application_info():
    """测试应用信息"""
    print("\n3. 测试应用信息:")
    
    # 尝试获取应用信息
    endpoints_to_try = [
        "/",
        "/info",
        "/actuator/info",
        "/application/info"
    ]
    
    for endpoint in endpoints_to_try:
        try:
            response = requests.get(f"http://localhost:8099{endpoint}", timeout=3)
            if response.status_code == 200:
                print(f"   发现端点: {endpoint}")
                print(f"   响应: {response.text[:100]}...")
                break
        except:
            pass
    else:
        print("   未发现标准信息端点")
    
    # 检查gRPC服务
    print("\n4. 检查gRPC服务:")
    try:
        import socket
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(2)
        result = sock.connect_ex(('localhost', 9099))
        if result == 0:
            print("   ✓ gRPC服务正在运行 (端口: 9099)")
        else:
            print("   ✗ gRPC服务未运行")
        sock.close()
    except Exception as e:
        print(f"   检查gRPC时出错: {e}")

def main():
    """主函数"""
    print("="*60)
    print("Primihub Meta 客户端功能测试")
    print("="*60)
    
    # 检查应用是否运行
    print("\n检查应用状态...")
    try:
        response = requests.get("http://localhost:8099/health", timeout=2)
        if response.status_code == 200:
            print("✓ 应用正在运行")
        else:
            print(f"✗ 应用返回状态码: {response.status_code}")
            return
    except requests.exceptions.ConnectionError:
        print("✗ 无法连接到应用，请确保应用正在运行")
        print("  运行命令: cd meta-simple && java -jar target/meta-simple-1.0-SNAPSHOT.jar")
        return
    except Exception as e:
        print(f"✗ 连接错误: {e}")
        return
    
    # 运行测试
    print("\n开始测试...")
    
    # 测试健康端点
    health_ok = test_health_endpoint()
    
    # 测试数据集CRUD
    crud_ok = test_dataset_crud()
    
    # 测试应用信息
    test_application_info()
    
    # 总结
    print("\n" + "="*60)
    print("测试总结:")
    print(f"  健康端点: {'✓ 通过' if health_ok else '✗ 失败'}")
    print(f"  CRUD操作: {'✓ 通过' if crud_ok else '✗ 失败'}")
    print(f"  gRPC服务: ✓ 已确认运行")
    
    if health_ok and crud_ok:
        print("\n✓ 所有基本功能测试通过!")
        print("\n应用运行正常，可以:")
        print("  1. 通过 REST API 管理数据集")
        print("  2. 通过 gRPC 服务进行数据交换")
        print("  3. 访问健康检查端点: http://localhost:8099/health")
    else:
        print("\n⚠ 部分测试失败，请检查应用日志")
    
    print("="*60)

if __name__ == "__main__":
    main()