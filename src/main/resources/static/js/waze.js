'use strict';

var module = angular.module('wingz.waze', 
		[
		 'ngRoute',
		 'angucomplete-alt',
		 'uiGmapgoogle-maps'
		 ]);

module.config(['$routeProvider', 'uiGmapGoogleMapApiProvider', function($routeProvider, uiGmapGoogleMapApiProvider) {
	 $routeProvider
	 .when('/waze', {
		 controller: 'WazeController',
		 templateUrl: '/template/waze.html',
	 });
   uiGmapGoogleMapApiProvider.configure({
     key: 'AIzaSyB-4FuznzXLDFsaaPRt5qy1w2IPMsLFbYs',
   });
}]);

module.controller('WazeController', ['$scope', '$http', '$log', 'uiGmapGoogleMapApi', 'uiGmapIsReady',
                                     function($scope, $http, $log, uiGmapGoogleMapApi, uiGmapIsReady){

	$scope.mapId = "map1.json";
	$scope.pointsMap = {};
	$scope.points = [];
	$scope.path = [];
	
	$scope.map = { 
			center: { latitude: 45.7578296698746, longitude: 4.868395040197143 },
			zoom: 13,
			options: {
			},
			control:{},
			checkpointsControl : {},
			positionsControl: {}
		};

	$scope.maps = {};
	
  $http.get("/map/" + $scope.mapId)
	.then(function(response, responseHeaders){
		$scope.points = response.data;
		$scope.points.forEach(function(point){
			$scope.pointsMap[point.id] = point;
		});
	});  
  
  
  $scope.searchPoint = function(query) {
  	if(query){
  		return $scope.points.filter(function(point){
        var lcQuery = angular.lowercase(query);
        var lcName = angular.lowercase(point.name);
        return lcName.indexOf(lcQuery) === 0;
      } );
  	} else {
  		return $scope.points;
  	}
  }
  $scope.selectedPointChange = function(query) {
  	if( $scope.startPoint && $scope.endPoint){
  		 $http.get("/solve?mapId=" + $scope.mapId + "&start=" + $scope.startPoint.id + "&end=" + $scope.endPoint.id)
  			.then(function(response, responseHeaders){
  				$scope.path = [];
  				response.data.forEach(function(item){
  					var point = $scope.pointsMap[item.id];
  					if(point){
  						var pathElem = {};
   						pathElem.id = item.id;
   						pathElem.longitude = point.longitude;
   						pathElem.latitude = point.latitude;
   						$scope.path.push(pathElem);
   					}
  				});
  			});  
  		  		
  	}
  }
  
  $scope.startPoint = null;
  $scope.endPoint = null;
}]);

