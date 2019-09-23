
def test_example():
    example = Example()
    assert example.get == 0
    example.increment()
    assert example.get() == 1
