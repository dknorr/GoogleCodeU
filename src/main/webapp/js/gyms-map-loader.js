let map;

function createMap() {
  map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 37.422, lng: -122.084}, // Gives div element "map" a default center on coordinates of Googleplex
    zoom: 16
  });

  /* Creating pin indicating exact location of Stan T-Rex statue on Google campus */
  var pinkMarkerIcon = 'http://maps.google.com/mapfiles/ms/icons/pink-dot.png';
  const trexMarker = new google.maps.Marker({
    position: {lat: 37.421903, lng: -122.084674},
    map: map,
    icon: pinkMarkerIcon,
    title: 'Stan the T-Rex'
  });

  /* Displays InfoWindow above marker after clicking on marker */
  var trexInfoWindow = new google.maps.InfoWindow({
    content: 'This is Stan, the T-Rex statue.'
  });

  trexMarker.addListener('click', function() {
    trexInfoWindow.open(map, trexMarker);
  });
}