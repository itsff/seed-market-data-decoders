#!/usr/bin/env python3
import sys
import argparse
import java_generator
import cpp_generator
import python_generator

name_to_gen = {
        'java': java_generator.JavaGenerator,
        'cpp': cpp_generator.CppGenerator,
        'python': python_generator.PythonGenerator,
    }


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('-g', '--gen',
                        required=True,
                        help='Name of the generator to use (ex: java)')
    parser.add_argument('-s', '--schema',
                        required=True,
                        help='Patch to XML schema file')
    parser.add_argument('-t', '--templates',
                        required=True,
                        help='Path to templates folder')
    parser.add_argument('-o', '--out',
                        required=True,
                        help='Path to output folder')

    args = parser.parse_args()

    gen_class = name_to_gen.get(args.gen)
    if not gen_class:
        print("Unknown generator:", args.gen)
        print("Supported generators:")
        for gen_name, gen_class in name_to_gen.items():
            print("\t", gen_name)
            sys.exit(1)

    gen = gen_class(args.schema, args.templates, args.out)
    gen.generate()
    print(args.gen, 'done')


if __name__ == '__main__':
    main()
