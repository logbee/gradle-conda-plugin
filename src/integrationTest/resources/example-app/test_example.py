from example import Calc

def test_Calc_add():
    testee = Calc()
    a = 3
    b = 4

    assert testee.add(a, b) == 7
