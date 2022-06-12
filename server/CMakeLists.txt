cmake_minimum_required(VERSION 3.19)
project(server C)

set(CMAKE_C_STANDARD 99)

add_executable(server lib/json-maker.c lib/json-maker.h lib/tiny-json.c lib/tiny-json.h src/Server.c src/JsonManager.c src/GameManager.c src/GameStructure.c src/Constants.h)