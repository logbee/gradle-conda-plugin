
class Greeter:

    def __init__(self, message):
        self.message = message

    def greet(self, name):
        return self.message + " " + name

def main():
    greeter = Greeter("Good afternoon")
    print(greeter.greet("Elmar"))


if __name__ == "__main__":
    main()
