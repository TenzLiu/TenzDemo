# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.6) #设置cmake最低版本

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.  设置生成so文件名
             SerialPort

             # Sets the library as a shared library. 设置库的类型，一种是静态的STATIC .a文件，一种是动态SHARED so文件
             SHARED

             # Provides a relative path to your source file(s). 要编译的C/C++文件
             src/main/jni/SerialPort.c )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library. 指定连接的目标库
                       SerialPort

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )