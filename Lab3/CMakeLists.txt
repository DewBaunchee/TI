cmake_minimum_required(VERSION 3.17)
project(Lab3)

set(CMAKE_CXX_STANDARD 14)

add_executable(Lab3 main.cpp bigIntegers/BigInteger.cpp bigIntegers/BigInteger.h rabin/RabinCypher.cpp rabin/RabinCypher.h)