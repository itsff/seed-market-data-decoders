cmake_minimum_required(VERSION 3.10)
project(seedcx-marketdata)

set(CMAKE_CXX_STANDARD 14)

find_package(Boost 1.65.0 REQUIRED COMPONENTS system chrono program_options)
IF(Boost_FOUND)
    include_directories(${Boost_INCLUDE_DIRS})
    link_directories(${Boost_LIBRARY_DIRS})
ENDIF(Boost_FOUND)

include_directories(seedcx-md/include)
include_directories(../generated-code/cpp/seedcx-md/include)

add_subdirectory(seedcx-md)
add_subdirectory(sniffer)
