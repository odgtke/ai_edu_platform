#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
智慧教育平台自动化开发脚本
功能：根据PRD文档自动生成功能模块代码和测试用例
作者：AI开发助手
日期：2026年3月
"""

import os
import sys
import json
import yaml
import subprocess
import argparse
from pathlib import Path
from typing import Dict, List, Any
import shutil
import re

class AutoDevFramework:
    """自动化开发框架"""
    
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.config = self.load_config()
        self.current_module = None
        
    def load_config(self) -> Dict:
        """加载配置文件"""
        config_path = self.project_root / "scripts" / "config.yaml"
        if config_path.exists():
            with open(config_path, 'r', encoding='utf-8') as f:
                return yaml.safe_load(f)
        else:
            return self.create_default_config()
    
    def create_default_config(self) -> Dict:
        """创建默认配置"""
        default_config = {
            "project": {
                "name": "智慧教育平台",
                "version": "1.0.0",
                "group_id": "com.chinaunicom",
                "artifact_id": "ai-edu-platform"
            },
            "technology_stack": {
                "backend": {
                    "framework": "spring-boot",
                    "version": "2.7.10",
                    "database": "mysql",
                    "cache": "redis"
                },
                "frontend": {
                    "framework": "vue3",
                    "ui_library": "element-plus"
                }
            },
            "modules": {
                "user": {"priority": "P0", "dependencies": []},
                "course": {"priority": "P0", "dependencies": ["user"]},
                "assignment": {"priority": "P1", "dependencies": ["user", "course"]},
                "message": {"priority": "P1", "dependencies": ["user"]},
                "resource": {"priority": "P1", "dependencies": ["user"]}
            },
            "testing": {
                "unit_test_coverage": 80,
                "integration_test": True,
                "test_framework": "junit5"
            }
        }
        return default_config
    
    def setup_project_structure(self):
        """设置项目目录结构"""
        print("🔧 正在设置项目目录结构...")
        
        # 创建后端目录结构
        backend_dirs = [
            "src/main/java/com/chinaunicom/edu",
            "src/main/resources",
            "src/test/java/com/chinaunicom/edu",
            "src/main/java/com/chinaunicom/edu/common",
            "src/main/java/com/chinaunicom/edu/config",
            "src/main/java/com/chinaunicom/edu/controller",
            "src/main/java/com/chinaunicom/edu/service",
            "src/main/java/com/chinaunicom/edu/mapper",
            "src/main/java/com/chinaunicom/edu/entity",
            "src/main/java/com/chinaunicom/edu/dto",
            "src/main/java/com/chinaunicom/edu/vo"
        ]
        
        for dir_path in backend_dirs:
            full_path = self.project_root / dir_path
            full_path.mkdir(parents=True, exist_ok=True)
            
        # 创建前端目录结构
        frontend_dirs = [
            "frontend/src/views",
            "frontend/src/components",
            "frontend/src/api",
            "frontend/src/store",
            "frontend/src/router",
            "frontend/src/utils"
        ]
        
        for dir_path in frontend_dirs:
            full_path = self.project_root / dir_path
            full_path.mkdir(parents=True, exist_ok=True)
        
        print("✅ 项目目录结构设置完成")
    
    def generate_pom_xml(self):
        """生成Maven配置文件"""
        print("🔧 正在生成pom.xml...")
        
        pom_content = '''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>{group_id}</groupId>
    <artifactId>{artifact_id}</artifactId>
    <version>{version}</version>
    <packaging>jar</packaging>

    <name>{name}</name>
    <description>智慧教育平台</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.10</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>8</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <mybatis-plus.version>3.5.3</mybatis-plus.version>
        <mysql.version>8.0.32</mysql.version>
        <redis.version>2.7.10</redis.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${{mysql.version}}</version>
        </dependency>
        
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${{mybatis-plus.version}}</version>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.9.1</version>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>'''.format(**self.config['project'])

        pom_path = self.project_root / "pom.xml"
        with open(pom_path, 'w', encoding='utf-8') as f:
            f.write(pom_content)
        
        print("✅ pom.xml生成完成")

    def generate_application_properties(self):
        """生成应用配置文件"""
        print("🔧 正在生成application.properties...")
        
        config_content = '''# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/edu_platform?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=root123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# MyBatis Plus Configuration
mybatis-plus.mapper-locations=classpath*:/mapper/**/*.xml
mybatis-plus.type-aliases-package=com.chinaunicom.edu.entity
mybatis-plus.global-config.db-config.id-type=auto
mybatis-plus.configuration.map-underscore-to-camel-case=true

# Redis Configuration
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.database=0
spring.redis.timeout=2000ms

# JWT Configuration
jwt.secret=mySecretKey
jwt.expiration=86400000

# Logging Configuration
logging.level.com.chinaunicom.edu=DEBUG
logging.pattern.console=%d{{yyyy-MM-dd HH:mm:ss}} [%thread] %-5level %logger{36} - %msg%n

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
'''.format()

        config_path = self.project_root / "src" / "main" / "resources" / "application.properties"
        with open(config_path, 'w', encoding='utf-8') as f:
            f.write(config_content)
        
        print("✅ application.properties生成完成")

    def generate_module_code(self, module_name: str):
        """生成指定模块的代码"""
        print(f"🔧 正在生成{module_name}模块代码...")
        
        module_config = self.config['modules'][module_name]
        self.current_module = module_name
        
        # 生成实体类
        self.generate_entity(module_name)
        
        # 生成Mapper接口
        self.generate_mapper(module_name)
        
        # 生成Service层
        self.generate_service(module_name)
        
        # 生成Controller层
        self.generate_controller(module_name)
        
        # 生成DTO和VO
        self.generate_dto_vo(module_name)
        
        print(f"✅ {module_name}模块代码生成完成")

    def generate_entity(self, module_name: str):
        """生成实体类"""
        entity_template = '''package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("{table_name}")
public class {entity_name} {{
    
    @TableId(type = IdType.AUTO)
    private Long {id_field};
    
    private String {name_field};
    private String description;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}}
'''
        
        # 根据模块名生成对应的实体类
        entity_mapping = {
            'user': {'table_name': 'edu_user', 'entity_name': 'User', 'id_field': 'userId', 'name_field': 'username'},
            'course': {'table_name': 'edu_course', 'entity_name': 'Course', 'id_field': 'courseId', 'name_field': 'courseName'},
            'assignment': {'table_name': 'edu_assignment', 'entity_name': 'Assignment', 'id_field': 'assignmentId', 'name_field': 'title'}
        }
        
        if module_name in entity_mapping:
            mapping = entity_mapping[module_name]
            content = entity_template.format(**mapping)
            
            entity_path = self.project_root / "src" / "main" / "java" / "com" / "chinaunicom" / "edu" / "entity" / f"{mapping['entity_name']}.java"
            with open(entity_path, 'w', encoding='utf-8') as f:
                f.write(content)

    def generate_mapper(self, module_name: str):
        """生成Mapper接口"""
        mapper_template = '''package com.chinaunicom.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.entity.{entity_name};
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface {entity_name}Mapper extends BaseMapper<{entity_name}> {{
}}
'''
        
        entity_names = {
            'user': 'User',
            'course': 'Course', 
            'assignment': 'Assignment'
        }
        
        if module_name in entity_names:
            entity_name = entity_names[module_name]
            content = mapper_template.format(entity_name=entity_name)
            
            mapper_path = self.project_root / "src" / "main" / "java" / "com" / "chinaunicom" / "edu" / "mapper" / f"{entity_name}Mapper.java"
            with open(mapper_path, 'w', encoding='utf-8') as f:
                f.write(content)

    def generate_service(self, module_name: str):
        """生成Service层代码"""
        service_template = '''package com.chinaunicom.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinaunicom.edu.entity.{entity_name};

public interface {entity_name}Service extends IService<{entity_name}> {{
}}

package com.chinaunicom.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinaunicom.edu.entity.{entity_name};
import com.chinaunicom.edu.mapper.{entity_name}Mapper;
import com.chinaunicom.edu.service.{entity_name}Service;
import org.springframework.stereotype.Service;

@Service
public class {entity_name}ServiceImpl extends ServiceImpl<{entity_name}Mapper, {entity_name}> implements {entity_name}Service {{
}}
'''
        
        entity_names = {
            'user': 'User',
            'course': 'Course',
            'assignment': 'Assignment'
        }
        
        if module_name in entity_names:
            entity_name = entity_names[module_name]
            content = service_template.format(entity_name=entity_name)
            
            # 生成接口
            interface_path = self.project_root / "src" / "main" / "java" / "com" / "chinaunicom" / "edu" / "service" / f"{entity_name}Service.java"
            with open(interface_path, 'w', encoding='utf-8') as f:
                f.write(service_template.split('package com.chinaunicom.edu.service.impl')[0].format(entity_name=entity_name))
            
            # 生成实现类
            impl_path = self.project_root / "src" / "main" / "java" / "com" / "chinaunicom" / "edu" / "service" / "impl" / f"{entity_name}ServiceImpl.java"
            impl_path.parent.mkdir(exist_ok=True)
            with open(impl_path, 'w', encoding='utf-8') as f:
                f.write('package com.chinaunicom.edu.service.impl;\n\n' + service_template.split('package com.chinaunicom.edu.service.impl')[1].format(entity_name=entity_name))

    def generate_controller(self, module_name: str):
        """生成Controller层代码"""
        controller_template = '''package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.entity.{entity_name};
import com.chinaunicom.edu.service.{entity_name}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{api_path}")
public class {entity_name}Controller {{

    @Autowired
    private {entity_name}Service {service_var}Service;

    @GetMapping
    public Result<List<{entity_name}>> getAll() {{
        List<{entity_name}> list = {service_var}Service.list();
        return Result.success(list);
    }}

    @GetMapping("/{{id}}")
    public Result<{entity_name}> getById(@PathVariable Long id) {{
        {entity_name} {var_name} = {service_var}Service.getById(id);
        return Result.success({var_name});
    }}

    @PostMapping
    public Result<Boolean> create(@RequestBody {entity_name} {var_name}) {{
        boolean result = {service_var}Service.save({var_name});
        return Result.success(result);
    }}

    @PutMapping
    public Result<Boolean> update(@RequestBody {entity_name} {var_name}) {{
        boolean result = {service_var}Service.updateById({var_name});
        return Result.success(result);
    }}

    @DeleteMapping("/{{id}}")
    public Result<Boolean> delete(@PathVariable Long id) {{
        boolean result = {service_var}Service.removeById(id);
        return Result.success(result);
    }}
}}
'''
        
        controller_mapping = {
            'user': {'entity_name': 'User', 'api_path': 'users', 'service_var': 'user', 'var_name': 'user'},
            'course': {'entity_name': 'Course', 'api_path': 'courses', 'service_var': 'course', 'var_name': 'course'},
            'assignment': {'entity_name': 'Assignment', 'api_path': 'assignments', 'service_var': 'assignment', 'var_name': 'assignment'}
        }
        
        if module_name in controller_mapping:
            mapping = controller_mapping[module_name]
            content = controller_template.format(**mapping)
            
            controller_path = self.project_root / "src" / "main" / "java" / "com" / "chinaunicom" / "edu" / "controller" / f"{mapping['entity_name']}Controller.java"
            with open(controller_path, 'w', encoding='utf-8') as f:
                f.write(content)

    def generate_dto_vo(self, module_name: str):
        """生成DTO和VO类"""
        dto_template = '''package com.chinaunicom.edu.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class {entity_name}DTO {{
    @NotBlank(message = "{chinese_name}不能为空")
    private String {name_field};
    private String description;
}}
'''
        
        vo_template = '''package com.chinaunicom.edu.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class {entity_name}VO {{
    private Long {id_field};
    private String {name_field};
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}}
'''
        
        dto_mapping = {
            'user': {'entity_name': 'User', 'chinese_name': '用户名', 'name_field': 'username'},
            'course': {'entity_name': 'Course', 'chinese_name': '课程名称', 'name_field': 'courseName'},
            'assignment': {'entity_name': 'Assignment', 'chinese_name': '作业标题', 'name_field': 'title'}
        }
        
        vo_mapping = {
            'user': {'entity_name': 'User', 'id_field': 'userId', 'name_field': 'username'},
            'course': {'entity_name': 'Course', 'id_field': 'courseId', 'name_field': 'courseName'},
            'assignment': {'entity_name': 'Assignment', 'id_field': 'assignmentId', 'name_field': 'title'}
        }
        
        if module_name in dto_mapping:
            # 生成DTO
            dto_content = dto_template.format(**dto_mapping[module_name])
            dto_path = self.project_root / "src" / "main" / "java" / "com" / "chinaunicom" / "edu" / "dto" / f"{dto_mapping[module_name]['entity_name']}DTO.java"
            dto_path.parent.mkdir(exist_ok=True)
            with open(dto_path, 'w', encoding='utf-8') as f:
                f.write(dto_content)
            
            # 生成VO
            vo_content = vo_template.format(**vo_mapping[module_name])
            vo_path = self.project_root / "src" / "main" / "java" / "com" / "chinaunicom" / "edu" / "vo" / f"{vo_mapping[module_name]['entity_name']}VO.java"
            vo_path.parent.mkdir(exist_ok=True)
            with open(vo_path, 'w', encoding='utf-8') as f:
                f.write(vo_content)

    def generate_unit_tests(self, module_name: str):
        """生成单元测试"""
        print(f"🔧 正在生成{module_name}模块单元测试...")
        
        test_template = '''package com.chinaunicom.edu.service;

import com.chinaunicom.edu.entity.{entity_name};
import com.chinaunicom.edu.mapper.{entity_name}Mapper;
import com.chinaunicom.edu.service.impl.{entity_name}ServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class {entity_name}ServiceTest {{

    @Mock
    private {entity_name}Mapper {var_name}Mapper;

    @InjectMocks
    private {entity_name}ServiceImpl {var_name}Service;

    @Test
    void testGetAll() {{
        // Given
        List<{entity_name}> mockList = Arrays.asList(new {entity_name}(), new {entity_name}());
        when({var_name}Mapper.selectList(null)).thenReturn(mockList);

        // When
        List<{entity_name}> result = {var_name}Service.list();

        // Then
        assertEquals(2, result.size());
        verify({var_name}Mapper).selectList(null);
    }}

    @Test
    void testCreate() {{
        // Given
        {entity_name} {var_name} = new {entity_name}();
        when({var_name}Mapper.insert({var_name})).thenReturn(1);

        // When
        boolean result = {var_name}Service.save({var_name});

        // Then
        assertTrue(result);
        verify({var_name}Mapper).insert({var_name});
    }}
}}
'''
        
        entity_mapping = {
            'user': {'entity_name': 'User', 'var_name': 'user'},
            'course': {'entity_name': 'Course', 'var_name': 'course'},
            'assignment': {'entity_name': 'Assignment', 'var_name': 'assignment'}
        }
        
        if module_name in entity_mapping:
            mapping = entity_mapping[module_name]
            content = test_template.format(**mapping)
            
            test_path = self.project_root / "src" / "test" / "java" / "com" / "chinaunicom" / "edu" / "service" / f"{mapping['entity_name']}ServiceTest.java"
            test_path.parent.mkdir(exist_ok=True)
            with open(test_path, 'w', encoding='utf-8') as f:
                f.write(content)
        
        print(f"✅ {module_name}模块单元测试生成完成")

    def run_unit_tests(self, module_name: str = None):
        """运行单元测试"""
        print("🧪 正在运行单元测试...")
        
        try:
            cmd = ["mvn", "test"]
            if module_name:
                cmd.extend(["-Dtest=*{}*".format(module_name.capitalize())])
            
            result = subprocess.run(cmd, cwd=self.project_root, capture_output=True, text=True)
            
            if result.returncode == 0:
                print("✅ 单元测试通过")
                return True
            else:
                print("❌ 单元测试失败:")
                print(result.stdout)
                print(result.stderr)
                return False
        except Exception as e:
            print(f"❌ 测试执行出错: {e}")
            return False

    def build_project(self):
        """构建项目"""
        print("🔨 正在构建项目...")
        
        try:
            result = subprocess.run(["mvn", "clean", "package"], 
                                  cwd=self.project_root, 
                                  capture_output=True, 
                                  text=True)
            
            if result.returncode == 0:
                print("✅ 项目构建成功")
                return True
            else:
                print("❌ 项目构建失败:")
                print(result.stdout)
                print(result.stderr)
                return False
        except Exception as e:
            print(f"❌ 构建出错: {e}")
            return False

    def execute_development_cycle(self):
        """执行完整的开发周期"""
        print("🚀 开始自动化开发流程...")
        
        # 1. 设置项目结构
        self.setup_project_structure()
        
        # 2. 生成配置文件
        self.generate_pom_xml()
        self.generate_application_properties()
        
        # 3. 按优先级顺序开发模块
        priority_modules = ['user', 'course', 'assignment']
        
        for module in priority_modules:
            print(f"\n🎯 开始开发 {module} 模块...")
            
            # 生成模块代码
            self.generate_module_code(module)
            
            # 生成测试用例
            self.generate_unit_tests(module)
            
            # 运行测试
            test_result = self.run_unit_tests(module)
            
            if test_result:
                print(f"✅ {module} 模块开发完成")
            else:
                print(f"❌ {module} 模块测试失败，请检查代码")
                return False
        
        # 4. 构建完整项目
        build_result = self.build_project()
        
        if build_result:
            print("\n🎉 所有模块开发完成，项目构建成功！")
            return True
        else:
            print("\n❌ 项目构建失败")
            return False

def main():
    parser = argparse.ArgumentParser(description='智慧教育平台自动化开发脚本')
    parser.add_argument('--module', help='指定要开发的模块')
    parser.add_argument('--test-only', action='store_true', help='仅运行测试')
    parser.add_argument('--build-only', action='store_true', help='仅构建项目')
    
    args = parser.parse_args()
    
    # 获取项目根目录
    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    
    # 初始化开发框架
    dev_framework = AutoDevFramework(project_root)
    
    if args.test_only:
        dev_framework.run_unit_tests(args.module)
    elif args.build_only:
        dev_framework.build_project()
    elif args.module:
        dev_framework.generate_module_code(args.module)
        dev_framework.generate_unit_tests(args.module)
        dev_framework.run_unit_tests(args.module)
    else:
        dev_framework.execute_development_cycle()

if __name__ == "__main__":
    main()