class Utility:

    def __init__(self):
        pass
    def custom_sleep(self, vehicle,drone_model, sleep_time):
        current_time = 0
        while(current_time<sleep_time):
            drone_model.update_status(vehicle.location.global_relative_frame.lat, vehicle.location.global_relative_frame.lon)
            ws.send(drone_model.toJSON())
            time.sleep(1)
            current_time+=1