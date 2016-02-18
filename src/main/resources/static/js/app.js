'use strict';

var app = angular.module('wingz', [
	'ngRoute', 'ngMaterial',
	'wingz.waze'
]);

app.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
	$locationProvider.html5Mode(true);
  $routeProvider.otherwise({redirectTo: '/waze'});
}]);

app.run(['$rootScope', function($rootScope){

}]);

