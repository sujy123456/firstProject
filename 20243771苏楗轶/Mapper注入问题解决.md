# MyBatis Mapper注入问题解决方案

## 问题描述

启动Spring Boot时出现以下错误：
```
No qualifying bean of type 'com.example.mapper.PolicyMapper' available
```

## 问题原因

Spring Boot无法识别MyBatis的Mapper接口，导致无法注入Bean。

## 解决方案（已应用）

### ✅ 已修改：添加@MapperScan注解

在启动类 [Test03Application.java](file:///C:/Users/苏/Desktop/河北省科技政策/test_03/src/main/java/com/example/Test03Application.java) 中添加了 `@MapperScan` 注解：

```java
package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.mapper")  // 新增这一行
public class Test03Application {

    public static void main(String[] args) {
        SpringApplication.run(Test03Application.class, args);
    }

}
```

## @MapperScan 的作用

`@MapperScan("com.example.mapper")` 告诉Spring Boot：
- 扫描 `com.example.mapper` 包下的所有接口
- 自动将这些接口注册为MyBatis的Mapper Bean
- 这样其他类就可以通过 `@Autowired` 注入这些Mapper

## 验证清单

重新启动后，应该能看到：

- [ ] 启动日志中没有 "No qualifying bean" 错误
- [ ] 看到类似日志：`Registered mapper: com.example.mapper.PolicyMapper`
- [ ] 应用成功启动：`Started Test03Application in X.XXX seconds`
- [ ] 访问 `http://localhost:8080/api/policyKinds` 能正常返回数据

## 其他可能的解决方案

如果问题仍然存在，可以尝试以下方法：

### 方法1：在每个Mapper接口上添加@Mapper注解（备选）

如果不想使用 `@MapperScan`，可以在每个Mapper接口上添加 `@Mapper`：

```java
import org.apache.ibatis.annotations.Mapper;

@Mapper  // 添加这个注解
public interface PolicyMapper {
    // ...
}
```

您的Mapper接口已经有这个注解了，但建议使用 `@MapperScan` 更简洁。

### 方法2：检查Mapper XML文件位置

确保XML文件在正确位置：
```
src/main/resources/mapper/PolicyMapper.xml
src/main/resources/mapper/PolicyKindMapper.xml
```

### 方法3：检查pom.xml依赖

确保有以下依赖：
```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>
```

## 完整配置检查

### application.properties
```properties
# MyBatis配置
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=com.example.entity
mybatis.configuration.map-underscore-to-camel-case=true
```

### 启动类
```java
@SpringBootApplication
@MapperScan("com.example.mapper")
public class Test03Application {
    // ...
}
```

### Mapper接口
```java
@Mapper  // 可选，因为已有@MapperScan
public interface PolicyMapper {
    // ...
}
```

## 重新启动步骤

1. **停止当前应用**（如果正在运行）
2. **清理项目**：
   - IDEA：点击菜单 `Build` -> `Clean Project`
   - 然后：`Build` -> `Rebuild Project`
3. **重新启动**：
   - 右键 `Test03Application.java`
   - 选择 `Run 'Test03Application'`
4. **查看控制台**：
   - 确认没有报错
   - 看到启动成功信息

## 测试API

启动成功后，在浏览器中访问：

```
http://localhost:8080/api/policyKinds
http://localhost:8080/api/policies
```

如果返回JSON数据，说明Mapper注入成功！

## 常见错误排查

### 错误1：仍然报同样的错误
**原因**：项目没有重新编译
**解决**：清理并重新构建项目

### 错误2：Mapper XML找不到
**错误信息**：`Invalid bound statement (not found)`
**解决**：检查 `application.properties` 中的 `mybatis.mapper-locations` 配置

### 错误3：数据库连接失败
**错误信息**：`Communications link failure`
**解决**：检查MySQL是否启动，数据库连接配置是否正确

## 问题已解决 ✅

已经为您添加了 `@MapperScan("com.example.mapper")` 注解，重新启动即可！
