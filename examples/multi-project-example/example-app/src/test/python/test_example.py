from lib import Example


def test_example():
    example = Example()
    assert example.name == ""
    example.greet("Manu")
    assert example.name == "Manu"
