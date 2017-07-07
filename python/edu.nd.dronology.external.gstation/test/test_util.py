import unittest
import util


class TestNVector(unittest.TestCase):
    def test_to_pvector(self):
        nvec = util.NVector(0, 1, 0, 0)
        expected = util.PVector(0.0, 6.37813700E06, 0.0)
        actual = nvec.to_pvector()

        self.assertEqual(expected, actual)

    def test_to_lla(self):
        nvec = util.NVector(0, 1, 0, 0)
        expected = util.LlaCoordinate(0, 90, 0)
        actual = nvec.to_lla()

        self.assertEqual(expected, actual)


class TestDistance(unittest.TestCase):
    def test_distance_1(self):
        a = util.LlaCoordinate(41.697987, -86.233922, 261.9)
        b = util.LlaCoordinate(41.698811, -86.233933, 261.9)

        expected = 91.44
        actual = a.distance(b)
        self.assertAlmostEqual(expected, actual, delta=0.25)

    def test_distance_2(self):
        a = util.LlaCoordinate(41.697987, -86.233922, 261.9).to_nvector()
        b = util.LlaCoordinate(41.698811, -86.233933, 261.9)

        expected = 91.44
        actual = a.distance(b)
        self.assertAlmostEqual(expected, actual, delta=0.25)

    def test_distance_3(self):
        a = util.LlaCoordinate(41.697987, -86.233922, 261.9).to_nvector()
        b = util.LlaCoordinate(41.698811, -86.233933, 261.9).to_pvector()

        expected = 91.44
        actual = a.distance(b)
        self.assertAlmostEqual(expected, actual, delta=0.25)


if __name__ == '__main__':
    unittest.main()