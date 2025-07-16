<p align="center">
	<img alt="logo" src="https://oscimg.oschina.net/oscnet/up-d3d0a9303e11d522a06cd263f3079027715.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">鑫乐途二手车管理系统 v3.9.0</h1>
<h4 align="center">基于SpringBoot+Vue3前后端分离的二手车管理系统</h4>
<p align="center">
	<a href="https://github.com/"><img src="https://img.shields.io/badge/鑫乐途-v3.9.0-brightgreen.svg"></a>
	<a href="LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg"></a>
</p>

## 平台简介

鑫乐途二手车管理系统是一套全面的二手车交易管理平台，基于前后端分离架构开发。

* 前端采用 Vue3、Element Plus、Vite 构建。
* 后端采用 Spring Boot、Spring Security、Redis & JWT。
* 权限认证使用 JWT，支持多终端认证系统。
* 支持加载动态权限菜单，多方式轻松权限控制。
* 高效率开发，使用代码生成器可以一键生成前后端代码。
* 集成 MyBatis Plus，简化数据库操作，提高开发效率。

## 技术栈

### 前端技术
- Vue 3
- Element Plus
- Vite
- Vue Router
- Vuex
- Axios
- Sass

### 后端技术
- Spring Boot 3.2.4
- Spring Security 6.2.3
- MyBatis 3.0.3
- MyBatis Plus 3.5.5
- Redis
- JWT
- Druid
- Quartz

## 模块结构

项目分为以下主要模块：

- **xinletu-admin**: 后端管理模块，系统的入口点
- **xinletu-common**: 公共模块，包含通用工具类和基础设施
- **xinletu-framework**: 框架核心模块，提供安全、数据源等核心功能
- **xinletu-generator**: 代码生成模块，用于自动生成代码
- **xinletu-quartz**: 定时任务模块，处理系统定时任务
- **xinletu-system**: 系统功能模块，包含用户、角色、菜单等核心业务功能
- **xinletu-ui**: 前端界面模块，基于 Vue.js 实现

## 内置功能

1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
13. 代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。
14. 系统接口：根据业务代码自动生成相关的api接口文档。
15. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
16. 缓存监控：对系统的缓存信息查询，命令统计等。
17. 二手车管理：车辆信息管理、车辆评估、库存管理等。
18. 交易管理：买卖合同、交易记录、付款管理等。

## 快速开始

### 环境要求
- JDK 17+
- MySQL 5.7+
- Redis 5.0+
- Maven 3.6+
- Node.js 16+

### 后端运行
```bash
# 克隆项目
git clone https://github.com/your-username/xinletu.git

# 进入项目目录
cd xinletu

# 编译项目
mvn clean package -DskipTests

# 运行项目
java -jar xinletu-admin/target/xinletu-admin.jar
```

### 前端运行
```bash
# 进入前端项目目录
cd xinletu-ui

# 安装依赖
npm install

# 启动服务
npm run dev

# 构建生产环境
npm run build:prod
```

## MyBatis Plus 集成

系统已集成 MyBatis Plus 3.5.5，可以与原生 MyBatis 共存：

- 配置类: `MybatisPlusConfig.java`
- 自动填充处理器: `MybatisPlusMetaObjectHandler.java`

### 使用示例

```java
// 继承 BaseMapper 获取通用 CRUD 方法
public interface UserMapper extends BaseMapper<User> {
    // 自定义方法...
}

// 条件查询
LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
query.eq(User::getStatus, "0");
List<User> users = userMapper.selectList(query);
```

## 开发规范

- 代码风格遵循阿里巴巴Java开发手册
- 前端组件和样式遵循项目已有的风格
- 接口设计遵循RESTful API规范
- 提交代码前进行代码格式化

## 许可证

[MIT License](LICENSE)