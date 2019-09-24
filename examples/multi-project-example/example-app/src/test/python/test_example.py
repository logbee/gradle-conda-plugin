from example import Calc

def test_Calc_add():
    computer = Calc()
    a = 3
    b = 4
    assert computer.add(a, b) == 7
