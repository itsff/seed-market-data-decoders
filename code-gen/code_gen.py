import java_generator

if __name__ == '__main__':
    gen = java_generator.JavaGenerator('./wire.xml', './templates', './out')
    gen.generate()
