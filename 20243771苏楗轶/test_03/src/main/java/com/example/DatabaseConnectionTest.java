package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * 数据库连接测试类
 * 启动时自动运行，检查数据库连接状态
 */
@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========================================");
        System.out.println("开始检查数据库连接...");
        System.out.println("========================================");
        
        try {
            // 获取数据库连接
            Connection connection = dataSource.getConnection();
            System.out.println("✅ 数据库连接成功！");
            
            // 获取数据库元数据
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("数据库产品: " + metaData.getDatabaseProductName());
            System.out.println("数据库版本: " + metaData.getDatabaseProductVersion());
            System.out.println("数据库URL: " + metaData.getURL());
            System.out.println("用户名: " + metaData.getUserName());
            System.out.println("驱动: " + metaData.getDriverName());
            
            // 检查policy表是否存在
            ResultSet tables = metaData.getTables("hebei_policy_db", null, "policy", null);
            if (tables.next()) {
                System.out.println("✅ policy表存在！");
                
                // 获取表中的记录数
                java.sql.Statement stmt = connection.createStatement();
                ResultSet countResult = stmt.executeQuery("SELECT COUNT(*) FROM policy");
                if (countResult.next()) {
                    System.out.println("📊 policy表记录数: " + countResult.getInt(1));
                }
                
                // 获取分类统计
                ResultSet typeResult = stmt.executeQuery(
                    "SELECT type, COUNT(*) as count FROM policy WHERE type IS NOT NULL GROUP BY type ORDER BY count DESC LIMIT 10"
                );
                System.out.println("\n📋 政策分类统计（前10个）:");
                while (typeResult.next()) {
                    System.out.println("  - " + typeResult.getString("type") + ": " + typeResult.getInt("count") + " 条");
                }
                
                // 查看最新5条数据
                ResultSet latestResult = stmt.executeQuery(
                    "SELECT id, name, type, organ, pubdata FROM policy ORDER BY pubdata DESC LIMIT 5"
                );
                System.out.println("\n📝 最新5条政策:");
                while (latestResult.next()) {
                    System.out.println("  ID: " + latestResult.getLong("id"));
                    System.out.println("  名称: " + latestResult.getString("name"));
                    System.out.println("  分类: " + latestResult.getString("type"));
                    System.out.println("  机关: " + latestResult.getString("organ"));
                    System.out.println("  日期: " + latestResult.getDate("pubdata"));
                    System.out.println("  ---");
                }
            } else {
                System.out.println("❌ policy表不存在！");
            }
            
            connection.close();
            
        } catch (Exception e) {
            System.out.println("❌ 数据库连接失败！");
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("========================================");
        System.out.println("数据库连接检查完成！");
        System.out.println("========================================");
    }
}