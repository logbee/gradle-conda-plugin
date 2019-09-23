
class Example:

    def __init__(self):
        self.counter = 0

    def hello_world(self):
        print("Hello World!")

    def increment(self):
        self.counter += 1

    def get(self):
        return self.counter

def main():
    example = Example()
    example.hello_world()

if __name__ == "__main__":
    main()