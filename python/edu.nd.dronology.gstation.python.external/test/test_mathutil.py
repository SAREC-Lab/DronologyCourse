import unittest

import numpy as np

from common import FLY_FIELD_BOUNDS
from util import mathtools


class TestGeoPoly(unittest.TestCase):
    def setUp(self):
        self.verts = map(lambda (lat, lon): mathtools.Lla(lat, lon, 0), FLY_FIELD_BOUNDS)
        self.gp = mathtools.GeoPoly(self.verts)

    def test_furthest_east(self):
        expected = mathtools.Lla(41.519028, -86.239411, 0)
        actual = self.gp.furthest_east().to_lla()
        self.assertEqual(expected, actual)

    def test_furthest_west(self):
        expected = mathtools.Lla(41.519007, -86.240396, 0)
        actual = self.gp.furthest_west().to_lla()
        self.assertEqual(expected, actual)

    def test_furthest_north(self):
        expected = mathtools.Lla(41.519391, -86.239414, 0)
        actual = self.gp.furthest_north().to_lla()
        self.assertEqual(expected, actual)

    def test_furthest_south(self):
        expected = mathtools.Lla(41.519007, -86.240396, 0)
        actual = self.gp.furthest_south().to_lla()
        self.assertEqual(expected, actual)

    def test_contained_rectangle(self):
        grid = [[41.68645504, -86.25961],
                [41.6869052178, -86.25961],
                [41.6869052163, -86.2590094402],
                [41.6864550384, -86.2590094444]]
        grid_lla = map(lambda (lat, lon): mathtools.Lla(lat, lon, 0), grid)
        grid_geo = mathtools.GeoPoly(grid_lla)
        contained = grid_geo.point_in_rectangle(mathtools.Lla(41.68675, -86.25957, 0))

        self.assertTrue(contained)

    def test_not_contained_rectangle(self):
        grid = [[41.68645504, -86.25961],
                [41.6869052178, -86.25961],
                [41.6869052163, -86.2590094402],
                [41.6864550384, -86.2590094444]]
        grid_lla = map(lambda (lat, lon): mathtools.Lla(lat, lon, 0), grid)
        grid_geo = mathtools.GeoPoly(grid_lla)
        contained = grid_geo.point_in_rectangle(mathtools.Lla(41.68675, -86.25964, 0))

        self.assertFalse(contained)

class TestNVector(unittest.TestCase):
    def test_to_pvector(self):
        nvec = mathtools.Nvector(0, 1, 0, 0)
        expected = mathtools.Pvector(0.0, 6.37813700E06, 0.0)
        actual = nvec.to_pvector()

        self.assertEqual(expected, actual)

    def test_to_lla(self):
        nvec = mathtools.Nvector(0, 1, 0, 0)
        expected = mathtools.Lla(0, 90, 0)
        actual = nvec.to_lla()

        self.assertEqual(expected, actual)

    def test_getters(self):
        nvec = mathtools.Nvector(0, 1, 1, -100)
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
        nvec = mathtools.Pvector(0.0, 6.37813700E06, 0.0)
        x = 0.0
        y = 6.37813700E06
        z = 0.0

        self.assertEqual(x, nvec.get_x())
        self.assertEqual(y, nvec.get_y())
        self.assertEqual(z, nvec.get_z())


class TestLLA(unittest.TestCase):
    def test_getters(self):
        lla = mathtools.Lla(0, 90, 0)
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
        a = mathtools.Lla(41.697987, -86.233922, 261.9)
        b = mathtools.Lla(41.698811, -86.233933, 261.9)

        expected = 91.44
        actual = a.distance(b)
        self.assertAlmostEqual(expected, actual, delta=0.25)

    def test_distance_2(self):
        a = mathtools.Lla(41.697987, -86.233922, 261.9).to_nvector()
        b = mathtools.Lla(41.698811, -86.233933, 261.9)

        expected = 91.44
        actual = a.distance(b)
        self.assertAlmostEqual(expected, actual, delta=0.25)

    def test_distance_3(self):
        a = mathtools.Lla(41.697987, -86.233922, 261.9).to_nvector()
        b = mathtools.Lla(41.698811, -86.233933, 261.9).to_pvector()

        expected = 91.44
        actual = a.distance(b)
        self.assertAlmostEqual(expected, actual, delta=0.25)


class TestEarth(unittest.TestCase):
    def test_meridional_radius_curvature_1(self):
        lat = 0
        expected = (mathtools.SEMI_MINOR ** 2) / mathtools.SEMI_MAJOR
        actual = mathtools.Earth.meridional_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_meridional_radius_curvature_2(self):
        lat = 90
        expected = (mathtools.SEMI_MAJOR ** 2) / mathtools.SEMI_MINOR
        actual = mathtools.Earth.meridional_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_meridional_radius_curvature_3(self):
        lat = -90
        expected = (mathtools.SEMI_MAJOR ** 2) / mathtools.SEMI_MINOR
        actual = mathtools.Earth.meridional_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_transverse_radius_curvature_1(self):
        lat = 0
        expected = mathtools.SEMI_MAJOR
        actual = mathtools.Earth.transverse_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_transverse_radius_curvature_2(self):
        lat = 90
        expected = (mathtools.SEMI_MAJOR ** 2) / mathtools.SEMI_MINOR
        actual = mathtools.Earth.transverse_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)

    def test_transverse_radius_curvature_3(self):
        lat = -90
        expected = (mathtools.SEMI_MAJOR ** 2) / mathtools.SEMI_MINOR
        actual = mathtools.Earth.transverse_radius_curvature(lat)

        self.assertAlmostEqual(expected, actual, places=6)


if __name__ == '__main__':
    unittest.main()
