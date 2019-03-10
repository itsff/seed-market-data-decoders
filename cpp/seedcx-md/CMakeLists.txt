cmake_minimum_required(VERSION 3.10)
project(seedcx-md)

file(GLOB GENERATED_FILES
    "../../generated-code/cpp/seedcx-md/src/*.cpp"
)

add_library(${PROJECT_NAME} STATIC
  src/exch_assigned_id.cpp
  ${GENERATED_FILES}
  )

include_directories(${PROJECT_NAME} STATIC
  include/
  ../../generated-code/cpp/seedcx-md/include/)
