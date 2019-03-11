/** Creates a map that shows gyms near the Googleplex. */
function createMap() {
  const map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 37.422, lng: -122.084}, // Gives div element "map" a default center on coordinates of Googleplex
    zoom: 13
  });

    addGymLocation(map, 37.414830, -122.090050, 'Fit4life', 'Treadmills. Bikes. Ellipticals. Weights ...And much more!'),
    addGymLocation(map, 37.410702, -122.084671, 'Whisman Sports Center', 'Onsite gymnasium and outdoor multi-use sports fields with lighting.'),
    addGymLocation(map, 37.402460, -122.109860, '24 Hour Fitness', 'Includes fitness classes, premium gym amenities and is open 24/7.');
}

/** Adds a marker indicating gym location that shows an InfoWindow when clicked. */
function addGymLocation(map, lat, lng, title, description) {
  const marker = new google.maps.Marker({
    position: {lat: lat, lng: lng},
    map: map,
    title: title
  });
      
  var infoWindow = new google.maps.InfoWindow({
    content: description
  });
  marker.addListener('click', function() {
    infoWindow.open(map, marker);
  });
}