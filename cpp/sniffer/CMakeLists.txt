cmake_minimum_required(VERSION 3.10)
project(sniffer)

add_executable(${PROJECT_NAME}
        main.cpp)

target_link_libraries(${PROJECT_NAME}
        seedcx-md
        pthread
        ${Boost_SYSTEM_LIBRARY}
        ${Boost_CHRONO_LIBRARY}
        ${Boost_PROGRAM_OPTIONS_LIBRARY})
