import nvector as nv
import numpy as np
import util
import unittest


# nv.n_E2R_EN()

class TestECEFtoNED(unittest.TestCase):
    def test_1(self):
        vel_ecef = [3, 1, 2]
        lla = util.Lla(41.519367, -86.240419, 0)

        p_EB_E = lla.to_pvector().as_array(flat=False)
        p_EB_E_east = np.cross([[0], [0], 1], p_EB_E, axis=0)
        p_EB_E_north = np.cross(p_EB_E, p_EB_E_east, axis=0)

        #
        # R_EN = [
        #     -
        # ]



if __name__ == '__main__':
    unittest.main()

