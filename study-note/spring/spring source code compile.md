## 编译 Spring 源码, 并导入Idea中

### 一、下载想要编译的 Spring 版本

#### 1.1访问下面链接, 下载指定版本

​	https://github.com/spring-projects/spring-framework/archive/v5.0.2.RELEASE.zip

#### 1.2 访问下面链接, 选择想要的版本进行下载

​	https://github.com/spring-projects/spring-framework/releases

### 二、安装Gradle, 进行文件编译

​	**MAC地址类似, 这里暂时不补充了, 后期有需要进行添加**

#### 2.1 源文件包含的内容, 如下图 (截图和官网说明, Spring编译需要使用Gradle)

![](.\spring source code compile\spring source code compile 01.png)



#### 2.2 访问下面地址, 下载gradle

​	https://gradle.org/releases/

#### 2.3 配置gradle环境变量

- 配置 GRADLE_HOME 环境变量

![](.\spring source code compile\gradle_home.png)

- 配置bin环境变量

<font color="#ff00">**在PATH添加信息, %GRADLE_HOME%\bin**</font>

- 配置 GRADLE_USER_HOME, local文件仓库位置

![](.\spring source code compile\gradle_user_home.png)

- 查询gradle安装完成

![](.\spring source code compile\gradle install complete.png)

#### 2.4 使用gradle编译spring文件

- cmd 切换目录到Spring文件根目录, 这里是

  D:\libs\workspace\openworkspace\spring-framework-5.0.2.RELEASE

- 执行命令 gradlew.bat, 出现下面的结果表示成功

![](.\spring source code compile\spring source code compile 02.png)

#### 2.5 修改build.gradle

​	让下载的仓库地址走国内阿里云, 避免网络问题造成jar下载不成功

```gradle
//1. 原来的配置
repositories {
    maven { url "https://repo.spring.io/libs-release" }
    maven { url "https://repo.spring.io/milestone" }  // for AspectJ 1.9 beta
}

//2. 修改之后的配置
repositories {
    mavenLocal()
    maven { url 'http://maven.aliyun.com/nexus/content/groups/public' }
    maven { url "https://repo.spring.io/libs-release" }
    maven { url "https://repo.spring.io/milestone" }  // for AspectJ 1.9 beta
}
```

#### 2.6 将编译后的工程导入idea中

![](.\spring source code compile\spring source code compile 03.png)



#### 2.7 选择Spring源码所在位置, 以Gradle工程导入

#### 2.8 idea下载需要的jar文件, 如下图

![](.\spring source code compile\spring source code compile 04.png)

#### 2.9 下载完成, 没有报错, 可以正确的打开类结构图, 说明成功

![](.\spring source code compile\spring source code compile 05.png)



<font color="#ff00">**NOTE: 如果需要将工程导入eclipse, 编译之后在根目录执行脚本 import-into-eclipse.bat , 等待构建成功即可。**</font>