import unittest
import util
import numpy as np


class TestNVector(unittest.TestCase):
    def test_to_pvector(self):
        nvec = util.LlaCoordinate(0, 90, 0).to_nvector().to_pvector().as_array()
        xyz = np.array([0.0, 6.37813700E06, 0.0])

        self.assertTrue(np.isclose(nvec, xyz).all())


class TestDistance(unittest.TestCase):
    def test_distance_1(self):
        a = util.LlaCoordinate(41.697983, -86.234213, 261.9)
        b = util.LlaCoordinate(41.698808, -86.234222, 261.9)

        dist = a.distance(b)
        self.assertAlmostEqual(91.44, dist, places=1)


if __name__ == '__main__':
    unittest.main()