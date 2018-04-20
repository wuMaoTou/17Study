cmake_minimum_required(VERSION 3.4.1)

add_library(hello-lib SHARED hello-lib.cpp)

target_link_libraries(hello-lib log)