import runner
import common
import time

gcs_runner = runner.GCSRunner('default', 'localhost', 1234, '../cfg/global_cfg.json')
gcs_runner.start()

d1_id = 'd1'
d1 = {'vehicle_id': d1_id,
      'vehicle_type': common.DRONE_TYPE_SITL_VRTL,
      'speedup': 5,
      'home': [41.6795175, -86.2525]}

gcs_runner.add_vehicle(d1)
while not gcs_runner.is_vehicle_ready(d1_id):
    time.sleep(1.0)

# time for dronology to do something... (horrible, I know)
time.sleep(60)
gcs_runner.remove_vehicle(d1_id)

# ...
# do other things

# shut everything down
gcs_runner.stop()


