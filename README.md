# Market Data Decoders

This repository contains referece decoders for Seed CX Binary Market Data.
It is still a work in progress. 


## Layout

* `code-gen` Python code generator which produces code for different target programming languages.
* `java` Java decoders and samples.
* `cpp` C++ decoders and samples.

## Building

1. Generate common source files by invoking `code-gen-all.py` file. This requires Python3.
   It will create a folder `generated-code` which will contain source files used by
   market data decoders written in different programming languages.

2. Use language-specific build process. For example, for `java` invoke `./gradlew build install`.

