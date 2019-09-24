from example import Greeter


def test_greeter():
    greeter = Greeter("Hello")
    assert greeter.greet("Elmar") == "Hello Elmar"
