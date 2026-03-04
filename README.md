# 斗地主（Landlords）

基于 Netty 的多人在线斗地主游戏，支持命令行交互，采用 Java 21 开发，包含完整的用户账号系统、房间管理、游戏逻辑及积分系统。

## 项目结构

```
landlords/
├── landlords-common/        # 公共模块（实体类、枚举、工具类、网络编解码）
├── landlords-server/        # 服务端模块（游戏逻辑、用户数据库、房间管理）
└── landlords-client/        # 客户端模块（命令行交互界面）
```

### 模块说明

| 模块 | 描述 |
|------|------|
| `landlords-common` | 共享实体（牌、房间、消息）、枚举定义、网络协议编解码、工具类 |
| `landlords-server` | Netty 服务端、事件处理器、MyBatis 用户持久层、游戏状态机 |
| `landlords-client` | Netty 客户端、命令行输入输出、客户端事件处理器 |

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 21 | 开发语言 |
| Maven | - | 构建工具 |
| Netty | 4.1.114.Final | 高性能网络通信框架 |
| MyBatis | 3.5.16 | ORM 持久层框架 |
| MySQL | 8.x | 用户数据存储 |
| Lombok | 1.18.34 | 简化 Java 样板代码 |
| Jackson | 2.17.2 | JSON 序列化/反序列化 |
| Logback | 1.5.6 | 日志框架 |
| Commons CLI | 1.4 | 命令行参数解析 |

## 功能特性

- **用户系统**：注册 / 登录，密码 MD5 加密存储，积分持久化
- **房间管理**：创建房间、获取房间列表、加入房间（最多 3 人）、退出房间
- **完整游戏流程**：发牌 → 抢地主 → 出牌 → 游戏结束
- **牌型判断**：支持所有标准斗地主牌型（见下方牌型说明）
- **积分系统**：地主/农民胜负积分计算，支持"春天"加倍、炸弹翻倍
- **聊天功能**：游戏内支持 `@[用户名] 消息` 格式私聊
- **心跳检测**：服务端 10 分钟读空闲超时自动踢出掉线玩家，客户端 8 秒写空闲自动发送心跳
- **自定义协议**：魔数（`buaa`）+ 4 字节长度 + JSON 消息体

## 环境准备

- JDK 21+
- Maven 3.6+
- MySQL 8.x

## 构建

在项目根目录执行：

```bash
mvn clean package -DskipTests
```

构建完成后：
- 服务端 JAR：`landlords-server/target/landlords-server-1.0-SNAPSHOT.jar`
- 客户端 JAR：`landlords-client/target/landlords-client-1.0-SNAPSHOT.jar`

## 数据库配置

### 建表

```sql
CREATE TABLE `user` (
    `id`       INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name`     VARCHAR(64)  NOT NULL UNIQUE,
    `password` VARCHAR(64)  NOT NULL,
    `score`    INT          NOT NULL DEFAULT 100
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 配置文件

在 `landlords-server/src/main/resources/` 目录下创建 `db.properties` 文件：

```properties
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/landlords?useSSL=false&serverTimezone=UTC
username=your_db_username
password=your_db_password
```

## 运行

### 启动服务端

```bash
java -jar landlords-server/target/landlords-server-1.0-SNAPSHOT.jar [-p <port>]
```

| 参数 | 简写 | 默认值 | 说明 |
|------|------|--------|------|
| `--port` | `-p` | `32112` | 监听端口 |

示例：

```bash
java -jar landlords-server-1.0-SNAPSHOT.jar -p 32112
```

### 启动客户端

```bash
java -jar landlords-client/target/landlords-client-1.0-SNAPSHOT.jar [-h <host>] [-p <port>]
```

| 参数 | 简写 | 默认值 | 说明 |
|------|------|--------|------|
| `--host` | `-h` | `127.0.0.1` | 服务器地址 |
| `--port` | `-p` | `32112` | 服务器端口 |

示例：

```bash
java -jar landlords-client-1.0-SNAPSHOT.jar -h 127.0.0.1 -p 32112
```

## 游戏流程

```
连接服务器
    └── 登录 / 注册
            └── 主菜单
                    ├── 1. 创建房间
                    ├── 2. 获取房间列表
                    ├── 3. 加入房间
                    ├── 4. 退出程序
                    ├── 5. 聊天（@[用户名] 内容）
                    └── 6. 查看个人信息

等待 3 名玩家就绪
    └── 开始游戏（发牌 17+17+17，底牌 3 张）
            └── 抢地主（每人叫分 0~3，最高分者得地主，平局重新发牌）
                    └── 出牌回合（地主先出，依次按座位顺序）
                            └── 游戏结束（某方打完手牌）
                                    └── 积分结算并写入数据库
```

## 牌型说明

| 牌型 | 描述 | 示例 |
|------|------|------|
| 单牌 | 任意单张 | `3` |
| 对子 | 两张相同点数 | `3 3` |
| 三张 | 三张相同点数 | `3 3 3` |
| 三带一 | 三张 + 任意单张 | `3 3 3 5` |
| 三带二 | 三张 + 对子 | `3 3 3 5 5` |
| 四带二单 | 四张 + 两张单牌 | `3 3 3 3 5 6` |
| 四带两对 | 四张 + 两对 | `3 3 3 3 5 5 6 6` |
| 单顺子 | 5 张以上连续单牌（3~A） | `3 4 5 6 7` |
| 双顺子 | 3 对以上连续对子 | `3 3 4 4 5 5` |
| 三顺子（飞机） | 2 组以上连续三张 | `3 3 3 4 4 4` |
| 飞机带单牌 | 连续三张 + 等组数单牌 | `3 3 3 4 4 4 5 6` |
| 飞机带对牌 | 连续三张 + 等组数对子 | `3 3 3 4 4 4 5 5 6 6` |
| 炸弹 | 四张相同点数 | `3 3 3 3` |
| 王炸 | 小王 + 大王 | `S X` |

> 牌面大小：`3 < 4 < 5 < 6 < 7 < 8 < 9 < 10 < J < Q < K < A < 2 < 小王(S) < 大王(X)`

### 牌面输入别名

| 牌面 | 输入字符 |
|------|---------|
| A | `A` / `a` / `1` |
| 10 | `T` / `t` / `0` |
| J | `J` / `j` |
| Q | `Q` / `q` |
| K | `K` / `k` |
| 小王 | `S` / `s` |
| 大王 | `X` / `x` |

## 积分规则

- 基础积分：3 分
- 地主赢：农民各扣 `基础积分 × 倍率`，地主得 `基础积分 × 倍率 × 2`
- 农民赢：地主扣 `基础积分 × 倍率 × 2`，农民各得 `基础积分 × 倍率`
- **叫分加倍**：叫几分，基础积分变为几分（最高 3 分）
- **炸弹/王炸**：每出一次倍率 ×2
- **春天**：对方未出过牌即获胜，额外 ×2

## 网络协议

采用自定义二进制帧协议，防止粘包：

```
+----------+------------+---------+
| 魔数(4B) | 长度(4B)   | JSON体  |
| 'buaa'   | int        | UTF-8   |
+----------+------------+---------+
```

消息体为 JSON，包含事件码（`code`）和数据（`data`）。

## 项目信息

本项目为北京航空航天大学（BUAA）面向对象程序设计课程大作业。
