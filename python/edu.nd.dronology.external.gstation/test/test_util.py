import unittest
import util
import numpy as np


class TestPosition(unittest.TestCase):
    def test_update(self):
        a = util.Lla(41.697987, -86.233922, 261.9)
        ned = np.array([[91.44], [0], [0]])
        a_new = a.update(ned, t=1.0)

        print(a_new.as_array())


class TestNVector(unittest.TestCase):
    def test_to_pvector(self):
        nvec = util.Nvector(0, 1, 0, 0)
        expected = util.Pvector(0.0, 6.37813700E06, 0.0)
        actual = nvec.to_pvector()

        self.assertEqual(expected, actual)

    def test_to_lla(self):
        nvec = util.Nvector(0, 1, 0, 0)
        expected = util.Lla(0, 90, 0)
        actual = nvec.to_lla()

        self.assertEqual(expected, actual)

    def test_getters(self):
        nvec = util.Nvector(0, 1, 1, -100)
        x = 0.0
        y = 1.0
        z = 1.0
        depth = -100.0

        self.assertEqual(x, nvec.get_x())
        self.assertEqual(y, nvec.get_y())
        self.assertEqual(z, nvec.get_z())
        self.assertEqual(depth, nvec.get_depth())


class TestPVector(unittest.TestCase):
    def test_getters(self):
        nvec = util.Pvector(0.0, 6.37813700E06, 0.0)
        x = 0.0
        y = 6.37813700E06
        z = 0.0

        self.assertEqual(x, nvec.get_x())
        self.assertEqual(y, nvec.get_y())
        self.assertEqual(z, nvec.get_z())


class TestLLA(unittest.TestCase):
    def test_getters(self):
        lla = util.Lla(0, 90, 0)
        lat_deg = 0
        lon_deg = 90
        lat_rad = np.deg2rad(lat_deg)
        lon_rad = np.deg2rad(lon_deg)
        alt = 0

        self.assertEqual(lat_deg, lla.get_latitude())
        self.assertEqual(lon_deg, lla.get_longitude())
        self.assertEqual(lat_rad, lla.get_latitude(as_rad=True))
        self.assertEqual(lon_rad, lla.get_longitude(as_rad=True))
        self.assertEqual(alt, lla.get_altitude())


class TestDistance(unittest.TestCase):
    def test_distance_1(self):
        a = util.Lla(41.697987, -86.233922, 261.9)
        b = util.Lla(41.698811, -86.233933, 261.9)

        expected = 91.44
        actual = a.distance(b)
        self.assertAlmostEqual(expected, actual, delta=0.25)

    def test_distance_2(self):
        a = util.Lla(41.697987, -86.233922, 261.9).to_nvector()
        b = util.Lla(41.698811, -86.233933, 261.9)

        expected = 91.44
        actual = a.distance(b)
        self.assertAlmostEqual(expected, actual, delta=0.25)

    def test_distance_3(self):
        a = util.Lla(41.697987, -86.233922, 261.9).to_nvector()
        b = util.Lla(41.698811, -86.233933, 261.9).to_pvector()

        expected = 91.44
        actual = a.distance(b)
        self.assertAlmostEqual(expected, actual, delta=0.25)


class TestEarth(unittest.TestCase):
    def test_meridional_radius_curvature_1(self):
        lat = 0
        expected = (util.SEMI_MINOR ** 2) / util.SEMI_MAJOR
        actual = util.Earth.meridional_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_meridional_radius_curvature_2(self):
        lat = 90
        expected = (util.SEMI_MAJOR ** 2) / util.SEMI_MINOR
        actual = util.Earth.meridional_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_meridional_radius_curvature_3(self):
        lat = -90
        expected = (util.SEMI_MAJOR ** 2) / util.SEMI_MINOR
        actual = util.Earth.meridional_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_transverse_radius_curvature_1(self):
        lat = 0
        expected = util.SEMI_MAJOR
        actual = util.Earth.transverse_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_transverse_radius_curvature_2(self):
        lat = 90
        expected = (util.SEMI_MAJOR ** 2) / util.SEMI_MINOR
        actual = util.Earth.transverse_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_transverse_radius_curvature_3(self):
        lat = -90
        expected = (util.SEMI_MAJOR ** 2) / util.SEMI_MINOR
        actual = util.Earth.transverse_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)




if __name__ == '__main__':
    unittest.main()