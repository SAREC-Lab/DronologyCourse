import unittest
import mathutil
import numpy as np
from common import DEFAULT_SAR_BOUNDS


class TestGeoPoly(unittest.TestCase):
    def setUp(self):
        self.verts = map(lambda args: mathutil.Lla(*args), DEFAULT_SAR_BOUNDS)
        self.gp = mathutil.GeoPoly(self.verts)

    def test_furthest_east(self):
        expected = mathutil.Lla(41.519028, -86.239411, 0)
        actual = self.gp.furthest_east().to_lla()
        self.assertEqual(expected, actual)

    def test_furthest_west(self):
        expected = mathutil.Lla(41.519007, -86.240396, 0)
        actual = self.gp.furthest_west().to_lla()
        self.assertEqual(expected, actual)

    def test_furthest_north(self):
        expected = mathutil.Lla(41.519391, -86.239414, 0)
        actual = self.gp.furthest_north().to_lla()
        self.assertEqual(expected, actual)

    def test_furthest_south(self):
        expected = mathutil.Lla(41.519007, -86.240396, 0)
        actual = self.gp.furthest_south().to_lla()
        self.assertEqual(expected, actual)


class TestNVector(unittest.TestCase):
    def test_to_pvector(self):
        nvec = mathutil.Nvector(0, 1, 0, 0)
        expected = mathutil.Pvector(0.0, 6.37813700E06, 0.0)
        actual = nvec.to_pvector()

        self.assertEqual(expected, actual)

    def test_to_lla(self):
        nvec = mathutil.Nvector(0, 1, 0, 0)
        expected = mathutil.Lla(0, 90, 0)
        actual = nvec.to_lla()

        self.assertEqual(expected, actual)

    def test_getters(self):
        nvec = mathutil.Nvector(0, 1, 1, -100)
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
        nvec = mathutil.Pvector(0.0, 6.37813700E06, 0.0)
        x = 0.0
        y = 6.37813700E06
        z = 0.0

        self.assertEqual(x, nvec.get_x())
        self.assertEqual(y, nvec.get_y())
        self.assertEqual(z, nvec.get_z())


class TestLLA(unittest.TestCase):
    def test_getters(self):
        lla = mathutil.Lla(0, 90, 0)
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
        a = mathutil.Lla(41.697987, -86.233922, 261.9)
        b = mathutil.Lla(41.698811, -86.233933, 261.9)

        expected = 91.44
        actual = a.distance(b)
        self.assertAlmostEqual(expected, actual, delta=0.25)

    def test_distance_2(self):
        a = mathutil.Lla(41.697987, -86.233922, 261.9).to_nvector()
        b = mathutil.Lla(41.698811, -86.233933, 261.9)

        expected = 91.44
        actual = a.distance(b)
        self.assertAlmostEqual(expected, actual, delta=0.25)

    def test_distance_3(self):
        a = mathutil.Lla(41.697987, -86.233922, 261.9).to_nvector()
        b = mathutil.Lla(41.698811, -86.233933, 261.9).to_pvector()

        expected = 91.44
        actual = a.distance(b)
        self.assertAlmostEqual(expected, actual, delta=0.25)


class TestEarth(unittest.TestCase):
    def test_meridional_radius_curvature_1(self):
        lat = 0
        expected = (mathutil.SEMI_MINOR ** 2) / mathutil.SEMI_MAJOR
        actual = mathutil.Earth.meridional_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_meridional_radius_curvature_2(self):
        lat = 90
        expected = (mathutil.SEMI_MAJOR ** 2) / mathutil.SEMI_MINOR
        actual = mathutil.Earth.meridional_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_meridional_radius_curvature_3(self):
        lat = -90
        expected = (mathutil.SEMI_MAJOR ** 2) / mathutil.SEMI_MINOR
        actual = mathutil.Earth.meridional_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_transverse_radius_curvature_1(self):
        lat = 0
        expected = mathutil.SEMI_MAJOR
        actual = mathutil.Earth.transverse_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_transverse_radius_curvature_2(self):
        lat = 90
        expected = (mathutil.SEMI_MAJOR ** 2) / mathutil.SEMI_MINOR
        actual = mathutil.Earth.transverse_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_transverse_radius_curvature_3(self):
        lat = -90
        expected = (mathutil.SEMI_MAJOR ** 2) / mathutil.SEMI_MINOR
        actual = mathutil.Earth.transverse_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)


if __name__ == '__main__':
    unittest.main()