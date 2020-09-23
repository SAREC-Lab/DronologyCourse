marker_count=0;
markers = {}
number_drones = 1
function initMap() {

  // map center on the screen - 50,50 
  // change it according to the home location of the drone. 
    var myLatLng = new google.maps.LatLng( 41.714469, -86.241786 )
        myOptions = {
            zoom: 17,
            center: myLatLng,
            mapTypeId: google.maps.MapTypeId.ROADMAP
            }
        map = new google.maps.Map( document.getElementById( 'map-canvas' ), myOptions )

        add_markers(myLatLng, map)
}
function get_marker_for_drone(drone_id){
  return markers[drone_id]

}
function add_markers(position,map){
  while(marker_count!=number_drones){
    marker_count+=1;
    marker = new google.maps.Marker( {position: position, map: map} );
    marker.setMap( map );
    markers[marker_count] = marker;
    
  }
  console.log(markers);
}

function move(id,lat,lon) {
    console.log("moving", id, lat, lon);
    marker_obj = get_marker_for_drone(id)
    console.log(marker_obj);
    marker_obj.setPosition( new google.maps.LatLng( lat, lon ) );
};

initMap();
doConnect();

function doConnect()
{
  websocket = new WebSocket('ws://localhost:8000/');
  websocket.onopen = function(evt) { onOpen(evt) };
  websocket.onclose = function(evt) { onClose(evt) };
  websocket.onmessage = function(evt) { onMessage(evt) };
  websocket.onerror = function(evt) { onError(evt) };
}

function onOpen(evt)
{
  console.log("connected\n");
}

function onClose(evt)
{
  console.log("disconnected\n");
}

function onMessage(evt)
{
  drone = JSON.parse(evt.data);

  console.log(drone+'\n');
  move(drone.id, drone.lat,drone.lon)
  
}

function onError(evt)
{
  console.log('error: ' + evt.data + '\n');
  websocket.close();
}

