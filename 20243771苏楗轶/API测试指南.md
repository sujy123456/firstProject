# API测试指南

## 测试工具
可以使用以下工具测试API：
- 浏览器直接访问
- Postman
- curl命令
- 浏览器开发者工具

## API接口测试

### 1. 获取政策分类树
**请求：**
```
GET http://localhost:8080/api/policyKinds
```

**预期响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "typeId": "0100",
      "type": "综合",
      "policyCount": 1
    },
    {
      "typeId": "0200",
      "type": "科研机构改革",
      "policyCount": 0
    },
    ...
  ]
}
```

**curl测试：**
```bash
curl http://localhost:8080/api/policyKinds
```

---

### 2. 查询所有政策（默认分页）
**请求：**
```
GET http://localhost:8080/api/policies
```

**预期响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "20220608001",
        "policyName": "河北省科技创新条例",
        "publishOrg": "河北省人民政府",
        "publishDate": "2022-06-08",
        "policyKindName": "综合",
        ...
      }
    ],
    "total": 5,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 1
  }
}
```

**curl测试：**
```bash
curl http://localhost:8080/api/policies
```

---

### 3. 按政策名称查询
**请求：**
```
GET http://localhost:8080/api/policies?policyName=科技创新
```

**说明：** 模糊查询政策名称包含"科技创新"的政策

**curl测试：**
```bash
curl "http://localhost:8080/api/policies?policyName=%E7%A7%91%E6%8A%80%E5%88%9B%E6%96%B0"
```

---

### 4. 按政策文号查询
**请求：**
```
GET http://localhost:8080/api/policies?policyNumber=冀政发
```

**curl测试：**
```bash
curl "http://localhost:8080/api/policies?policyNumber=%E5%86%80%E6%94%BF%E5%8F%91"
```

---

### 5. 按发文机构查询
**请求：**
```
GET http://localhost:8080/api/policies?publishOrg=科技厅
```

**curl测试：**
```bash
curl "http://localhost:8080/api/policies?publishOrg=%E7%A7%91%E6%8A%80%E5%8E%85"
```

---

### 6. 全文检索
**请求：**
```
GET http://localhost:8080/api/policies?keyword=高新技术
```

**说明：** 在政策名称、正文、关键词中搜索

**curl测试：**
```bash
curl "http://localhost:8080/api/policies?keyword=%E9%AB%98%E6%96%B0%E6%8A%80%E6%9C%AF"
```

---

### 7. 按政策分类查询
**请求：**
```
GET http://localhost:8080/api/policies?policyKindId=0100
```

**说明：** 查询"综合"分类下的所有政策

**curl测试：**
```bash
curl "http://localhost:8080/api/policies?policyKindId=0100"
```

---

### 8. 多条件组合查询
**请求：**
```
GET http://localhost:8080/api/policies?policyName=河北&publishOrg=科技厅&pageNum=1&pageSize=10
```

**curl测试：**
```bash
curl "http://localhost:8080/api/policies?policyName=%E6%B2%B3%E5%8C%97&publishOrg=%E7%A7%91%E6%8A%80%E5%8E%85&pageNum=1&pageSize=10"
```

---

### 9. 分页查询（第2页，每页2条）
**请求：**
```
GET http://localhost:8080/api/policies?pageNum=2&pageSize=2
```

**curl测试：**
```bash
curl "http://localhost:8080/api/policies?pageNum=2&pageSize=2"
```

---

### 10. 获取政策详情
**请求：**
```
GET http://localhost:8080/api/policy/20220608001
```

**预期响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "20220608001",
    "policyName": "河北省科技创新条例",
    "policyNumber": "冀政发〔2022〕1号",
    "publishOrg": "河北省人民政府",
    "publishDate": "2022-06-08",
    "implementDate": "2022-07-01",
    "policyKindName": "综合",
    "scopeName": "河北省",
    "effectiveStatus": "有效",
    "content": "本条例是为了促进科技创新，推动经济高质量发展而制定。",
    ...
  }
}
```

**curl测试：**
```bash
curl http://localhost:8080/api/policy/20220608001
```

---

## 常见测试场景

### 场景1：测试完整查询流程
```bash
# 1. 获取所有分类
curl http://localhost:8080/api/policyKinds

# 2. 查询所有政策
curl http://localhost:8080/api/policies

# 3. 查询"综合"分类的政策
curl "http://localhost:8080/api/policies?policyKindId=0100"

# 4. 查看某条政策详情
curl http://localhost:8080/api/policy/20220608001
```

### 场景2：测试多条件查询
```bash
# 查询河北省科技厅发布的关于科技的政策
curl "http://localhost:8080/api/policies?publishOrg=科技厅&keyword=科技"
```

### 场景3：测试分页
```bash
# 第1页，每页2条
curl "http://localhost:8080/api/policies?pageNum=1&pageSize=2"

# 第2页，每页2条
curl "http://localhost:8080/api/policies?pageNum=2&pageSize=2"

# 第3页，每页2条
curl "http://localhost:8080/api/policies?pageNum=3&pageSize=2"
```

## 错误响应示例

### 政策不存在
```json
{
  "code": 404,
  "message": "政策不存在"
}
```

### 服务器错误
```json
{
  "code": 500,
  "message": "查询失败：具体错误信息"
}
```

## Postman测试集合

您可以将以下JSON导入Postman创建测试集合：

```json
{
  "info": {
    "name": "河北省科技政策查询系统API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "获取政策分类",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/policyKinds"
      }
    },
    {
      "name": "查询政策列表",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/policies"
      }
    },
    {
      "name": "获取政策详情",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/policy/20220608001"
      }
    }
  ]
}
```

## 浏览器测试

直接在浏览器地址栏输入以下URL进行测试：

1. `http://localhost:8080/api/policyKinds`
2. `http://localhost:8080/api/policies`
3. `http://localhost:8080/api/policies?policyName=科技创新`
4. `http://localhost:8080/api/policy/20220608001`

浏览器会以JSON格式显示响应数据。
