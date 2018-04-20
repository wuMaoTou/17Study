##CMake说明
###1.简介
CMake是AndroidStudio2.2版本后引进的编译C/C++代码的JNI使用方式

###2.CMake工具配置
在Android SDK  -> SDKTools勾选安装CMake,LLDB(C/C++代码调试用),NDK(底层开发环境)就可以了

###3.使用AndroidStudio自带的Demo了解CMake
在AndroidStudio创建新的项目时勾选Include C++ Support就能够创建一个使用CMake的JNI示例了
其中在项目目录结构上主要的新增文件包括
app/.externalNativeBuild -- CMake工具编译是生成的一些资源文件
app/src/main/cpp         -- 存放C/C++代码的文件
app/CMakeLists.txt       -- CMake脚本配置的文件

在app/build.gradle文件添加了2处代码
***

android {
    compileSdkVersion 27
    defaultConfig {
        applicationId "com.maotou.cmakedemo"
        minSdkVersion 14
        targetSdkVersion 27
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags ""
            }
        }
        ndk {
              abiFilters 'x86', 'x86_64', 'armeabi', 'armeabi-v7a', 'arm64-v8a'
        }
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    externalNativeBuild {
        cmake {
            path "CMakeLists.txt"
        }
    }
}

***

###4.使用关注的点
1.使用是将C/C++代码放入cpp文件夹
2.修改CMakeLists.txt文件
    CMakeLists文件中主要的3个方法
        cmake_minimum_required(VERSION 3.4.1)
        add_library(hello-lib SHARED 路径/hello-lib.cpp)
        target_link_libraries(hello-lib log)
就像demo中添加了hello-lib.cpp文件有2中方式引入
    1.直接在app/CMakeLists文件下添加
        ***

          add_library(hello-lib SHARED src/main/cpp/hello-lib.cpp)
          target_link_libraries( native-lib hello-lib ${log-lib} )

         ***
    2.在cpp创建子目录,创建独立的CMakeLists,在用add_subdirectory(src/main/cpp/test)方法引入到主CMakeLists中

###5.so包生成路径
在app/build/intermediates/cmake下对应的编译方式下会生成不同硬件设备支持的so包

###5.生成头文件
将加载so库的java类编译除字节文件(class),在通过javah命令生成,生成是在包名的上一个路径执行命令
生成java对应的头文件命令:
***
    javah -classpath . -jni 包名.类名
***

C/C++日志输出: https://www.cnblogs.com/chenxibobo/p/7678389.html
CMake和之前JNI对比: https://blog.csdn.net/qq_25817651/article/details/53135685