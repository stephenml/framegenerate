# **执行Jar包即可生成**

# **一、项目文档**

## 1、部分目录说明

### gradle

	gradle 目录, 相关文件 build.gradle

### libs

    无法从Maven获取的jar包放置此处

### src/main/java

    ml.stephen.core : 核心目录
    
    包含 :
        Redis相关(cache)
        MongoDB相关(mongo)
        MyBatis注解自动加载(mybatis)

### src/main/resources

    ml.stephen : MyBatis XML文件
    properties : 项目配置目录
    spring : Spring配置目录
    mybatis : MyBatis配置目录
    velocity : Velocity配置目录

## 2、Seajs

### 环境安装

    mac:
    1、ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
    2、brew install npm

### spm安装

    sudo npm install spm -g
    sudo npm install spm-sea -g
    sudo npm install spm-chaos-build -g

## 3、Gradle

### 环境安装

    mac:
    1、ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
    2、brew install gradle

### IDEA 设置

    打开 Preferences -> Build,Execution,Deployment -> Build Tools -> Gradle
    选择 use local gradle distribution
    设置 Gradle Home: /usr/local/Cellar/gradle/{version}/libexec

# **二、发布项目**

### 1、修改web.xml

    修改 spring.profiles.default
    改为 release  (发布环境)

### 2、构建js文件

### 删除目录

	webapp/resource/js/modules/root

### 构建

	终端打开 webapp/resource/modules/root	执行 make

### 3、修改版本号 并提交

    修改 build.gradle version
    格式 1.0.1 build-0920

	规则：主版本号.子版本号[.修正版本号 [.编译版本号 ]]

		1、进行了局部修改或 bug 修正时，主版本号和子版本号都不变，修正版本号加 1
		2、在原有的基础上增加了部分功能时，主版本号不变，子版本号加 1，修正版本号复位为 0
		3、在进行了重大修改或局部修正累积较多，而导致项目整体发生全局变化时，主版本号加 1
		4、编译版本号未使用

### 4、打war包

	执行 release

	生成文件位置 target/ROOT-<Versions>.war

### 5、部署到服务器