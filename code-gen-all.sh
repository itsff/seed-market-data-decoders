#!/bin/bash

# Create Python3 virtual environment and install dependencies
python3 -m venv ./code-gen/venv
source ./code-gen/venv/bin/activate
#pip install --upgrade pip --quiet
pip install -r ./code-gen/requirements.txt --quiet

# Common settings
SCHEMA=./code-gen/wire.xml
TEMPLATES=./code-gen/templates/
OUT=./generated-code/

# Generate some code
python3 ./code-gen/code_gen.py -s ${SCHEMA} -t ${TEMPLATES} -o ${OUT} -g java
