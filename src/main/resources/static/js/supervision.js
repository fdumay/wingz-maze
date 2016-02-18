var module = angular.module('cnr.supervision',['uiGmapgoogle-maps']);

module.config(['uiGmapGoogleMapApiProvider',function(uiGmapGoogleMapApiProvider) {
    uiGmapGoogleMapApiProvider.configure({
        key: 'AIzaSyB-4FuznzXLDFsaaPRt5qy1w2IPMsLFbYs',
    });
}]);

module.factory('locationService', [ 'webSocket', function(webSocket){
	
	var service = {};
	
	service.positions = [];
	var MIN_ACCURACY = 2000; // Min accuracy for gps locations that will be shown on the map inteface.

	webSocket.followTopic('location');
	function locationUpdate(payload){
		var point = payload.location;
		if(point.accuracy < MIN_ACCURACY){			
			setLocation(point);
		}
	}
	webSocket.registerTypeListener("locationUpdate", locationUpdate);
	function setLocation(location){
		var found = false;
		var i = 0;
		for (;i<service.positions.length;i++){
			if(service.positions[i].deviceId == location.deviceId){
				location.id = i;
				service.positions[i] = location;
				found = true;
			}
		}
		if(!found){
			location.id = i;
			service.positions[i] = location;
		}
	}
	
	return service;
}]);

module.factory('eventsService', ['webSocket', 'broadcastService', '$mdDialog', '$http',
                                 function(webSocket, broadcastService, $mdDialog, $http){
	var service = {};
	
	var selectedItem = null;
	
	service.events = [];
	service.items = [];
	var showAlert = false;
	service.enableAlert = function(){showAlert = true;};
	service.stopAlert = function(){showAlert = false;};
	
	function addItem(item){
		service.items.push(item);
		service.items.sort(function(a,b){
			return b.time - a.time;
		});
	}
	function addItems(items){
		angular.copy(service.items.concat(items), service.items);
		service.items.sort(function(a,b){
			return b.time - a.time;
		});
	}
	function addEvent(event){
		service.events.push(event);
		service.events.sort(function compareEvents(a, b) {
		  return b.item.time - a.item.time;
		});
	}
	function addEvents(events){
		angular.copy(service.events.concat(events), service.events);
		service.events.sort(function compareEvents(a, b) {
		  return b.item.time - a.item.time;
		});
	}
	
    function showAlertPopup($event, alert) {
        $mdDialog.show({
            targetEvent: $event,
            templateUrl: '/assets/supervision/alert-dialog.html',
            controller: 'AlertDialogController',
            locals : { alert: alert }
          })
          .then(function(answer) {
  	    }, function() {
  	    	//Dialog closed
  	    });   
  	};
	
	webSocket.followTopic('alert');
	webSocket.followTopic('incident');
	webSocket.followTopic('report');

	webSocket.registerTypeListener("alertUpdate", function(payload){
		var alert = payload.alert;
		alert.type = 'alert';
		var alertEvent = new TimelineEvent(alert, "alert");
		addEvent(alertEvent);
		addItem(alert);
		if(showAlert){			
			showAlertPopup(null, alert);
		}
	});
	webSocket.registerTypeListener("incidentUpdate", function(payload){
		var incident = payload.incident;
		incident.dbType = incident.type;
		incident.type = 'incident';
		addEvent(new TimelineEvent(incident, "incident", incident.validated));
		addItem(incident);
	});
	webSocket.registerTypeListener("reportUpdate", function(payload){
		var report = payload.report;
		report.type = 'report';
		
		if(!service.updateItem(report)){
			addEvent(new TimelineEvent(report, "report", report.ok));
			addItem(report);
		}
	});
	
	service.setActiveItem = function setActiveItem(item, type){
		selectedItem = item;
		broadcastService.send(type + 'Selected', item);
	};
	
	service.unselect = function unselect(){
		broadcastService.send('itemUnselected', selectedItem);
		selectedItem = null;
	};
	
	service.updateItem = function(item){
		var oldEvent = findEvent(item);
		if(oldEvent){
			var oldItemIndex = itemIndexOfByUuid(oldEvent.item.uuid);
			if(oldItemIndex != -1){
				service.items[oldItemIndex] = item;
				oldEvent.setItem(item);
				return true;
			}
		}else{
			return false;
		}
	};
	
	function itemIndexOfByUuid(uuid){
		var indexOf = -1;
		for(var i = 0; i< service.items.length; i++){
			if(service.items[i].uuid == uuid){
				return i;
			}
		}
		return indexOf;
	}
	service.getEvent = findEvent;
	function findEvent(item){
		var foundEvent = undefined;
		service.events.forEach(function(event){
			if(event.item.uuid == item.uuid){
				foundEvent = event;
			}
		});
		return foundEvent;
	}
	service.findReportByCheckpointUuid = function(checkpointUuid){
		var foundEvent = undefined;
		service.events.forEach(function(event){
			if(event.item.checkpoint && event.item.checkpoint.uuid == checkpointUuid && !foundEvent){
				foundEvent = event;
			}
		});
		return foundEvent;
	}
	service.infiniteScroll = {
			shouldLoadPage : 0,
			lastAlertDate: null,
			lastIncidentDate: null,
			lastReportDate: null,
			loading : false,
	        fetchMoreItems: function() {
	            if (!service.infiniteScroll.loading) {
	              loadMore();
	            }
	        }
	};
	function loadMore(){
		var infScroll = service.infiniteScroll;
		var url = '/events';
		var params = null;
		var isLastDay = false;
		if( !infScroll.lastAlertDate && !infScroll.lastIncidentDate && !infScroll.lastReportDate ){
			url += '/last-day';
			isLastDay = true;
			params = {};
		}
		
        setDates();
        
        params = params || { alertDate : service.infiniteScroll.lastAlertDate.getTime(),
						  incidentDate : service.infiniteScroll.lastIncidentDate.getTime(),
						    reportDate : service.infiniteScroll.lastReportDate.getTime() };
		
		
		service.infiniteScroll.loading = true;
		
		$http.get(url, { params : params } )
        .then(function(response){


      	  service.infiniteScroll.loading = false;
      	  if( response.data && 
      			  Object.prototype.toString.call( response.data ) === '[object Array]' 
      				  && response.data.length > 0) {

      		  var events = response.data.map(function(event){
      			  return new TimelineEvent(event.item, event.type);
      		  });
      		  var items = response.data.map(function(event){
      			  if(event.item.type){      				  
      				  event.item.dbType = event.item.type;
      			  }
      			 event.item.type = event.type;
      			 return event.item;
      		  });
      		  addEvents(events);
      		  addItems(items);

      		  
      	  }else{
      		  
      	  }
      	  if( isLastDay ){
      		  
      		  loadMore();
      	  }
      	  
        },function(errorResponse){
        	service.infiniteScroll.loading = false;
        	console.log(errorResponse);
        });
	}
	
	function setDates(){
		var index = service.events.length - 1;
		var lastAlertDate, lastIncidentDate, lastReportDate;
		
		if(index < 0){
			var yesterday = new Date();
			yesterday.setDate(yesterday.getDate()-1);
			service.infiniteScroll.lastAlertDate = yesterday;
			service.infiniteScroll.lastIncidentDate = yesterday;
			service.infiniteScroll.lastReportDate = yesterday;
		}

		service.events.forEach(function (event){
			switch(event.type){
			case 'report':
				if(event.getTime() < service.infiniteScroll.lastReportDate.getTime()){
					lastReportDate = event.getTime();
					service.infiniteScroll.lastReportDate = new Date(lastReportDate);
				}
				break;
			case 'alert':
				if(event.getTime() < service.infiniteScroll.lastAlertDate.getTime()){
					lastAlertDate = event.getTime();
					service.infiniteScroll.lastAlertDate = new Date(lastAlertDate);
				}
				break;
			case 'incident':
				if(event.getTime() < service.infiniteScroll.lastIncidentDate.getTime()){
					lastIncidentDate = event.getTime();
					service.infiniteScroll.lastIncidentDate = new Date(lastIncidentDate);
				}
				break;
			}
		});
	}
	
	return service;
}]);

module.controller('SupervisionController', ['$scope', 'webSocket', 'uiGmapGoogleMapApi', 'uiGmapIsReady', '$rootScope','sidenavHelper',
                                            '$location', 'AuthService', 'Checkpoints', 'People', 'alertSoundService','eventsService',
                                            'locationService',
                                            function($scope, webSocket, uiGmapGoogleMapApi, uiGmapIsReady, $rootScope, sidenavHelper, 
                                            $location, AuthService, Checkpoints, People, alertSoundService, eventsService,
                                            locationService){
	
	if(!AuthService.hasRole('ROLE_ADMIN')){
		$location.path('/');
	} else {
		$scope.secured = true;
		eventsService.enableAlert();
	}

	$scope.positionOptions = { icon: {url: "assets/images/agent_icon.png", scaledSize:{height:36,width:36}} };
	$scope.positions = locationService.positions;
	
	$scope.greenCheckpoints = [];
	$scope.redCheckpoints = [];
	Checkpoints.query(function(checkpoints){
		$scope.greenCheckpoints = checkpoints.filter(function(checkpoint){
			return checkpoint.visible && checkpoint.ok;
		});
		$scope.redCheckpoints = checkpoints.filter(function(checkpoint){
			return checkpoint.visible && !checkpoint.ok;
		});
	});


	
	$scope.allItems = eventsService.items;
	$scope.alertOptions = { icon: {url: "assets/images/sos.png", scaledSize:{height:24, width:50}} };
	$scope.greenIncidentOptions = { icon: {url: "assets/images/green_warning.png", scaledSize:{height:24, width:24}} };
	$scope.redIncidentOptions = { icon: {url: "assets/images/red_warning.png", scaledSize:{height:24, width:24}} };
	$scope.redCheckpointOptions = { icon: {url: "assets/images/checkpoint-fail.png", scaledSize:{height:24, width:24}} };
	$scope.greenCheckpointOptions = { icon: {url: "assets/images/checkpoint-success.png", scaledSize:{height:24, width:24}} };

	$scope.alerts = [];
	$scope.greenIncidents = [];
	$scope.redIncidents = [];
	function updateItems() {
		$scope.greenIncidents = $scope.allItems.filter( function(item){
			return (item.type == 'incident' && isLessThan24hOld(item.time) && item.validated);
		});
		$scope.redIncidents = $scope.allItems.filter( function(item){
			return (item.type == 'incident' && isLessThan24hOld(item.time) && !item.validated);
		});
		$scope.alerts = $scope.allItems.filter( function(item){
			return (item.type == 'alert' && isLessThan24hOld(item.time));
		});
		setCheckpoints();
	}
	$scope.$watchCollection('allItems', updateItems);
	
	function setCheckpoints(){
		var index = $scope.greenCheckpoints.length -1;
		for( ; index >= 0; index--){
			var checkpoint = $scope.greenCheckpoints[index];
			if( checkpoint ){
				var found = false;
				var i = 0;
				while( !found && i < $scope.allItems.length  ){
					var currentItem = $scope.allItems[i];
					if( currentItem.type == 'report' && 
							currentItem.checkpoint.uuid == checkpoint.uuid ){
						found = true;
						if( checkpoint.lastReportTime == null || 
								currentItem.time > checkpoint.lastReportTime ){
							var reportOk = isReportOk(currentItem);
							if( !reportOk ){
								$scope.greenCheckpoints.splice(index, 1);
								$scope.redCheckpoints.push(checkpoint);
							}
							checkpoint.ok = reportOk;
							checkpoint.lastReportTime = currentItem.time;
						}
					}
				i++;
				}
			}
		}
		var checkpointIndex = $scope.redCheckpoints.length -1;
		for( ; checkpointIndex >= 0; checkpointIndex--){
			var checkpoint = $scope.redCheckpoints[checkpointIndex];
			if(checkpoint){
				var found = false;
				var i = 0;
				while( !found && i < $scope.allItems.length  ){
					var currentItem = $scope.allItems[i];
					if( currentItem.type == 'report' && 
							currentItem.checkpoint.uuid == checkpoint.uuid ){
						found = true;
						if( checkpoint.lastReportTime == null || 
								currentItem.time > checkpoint.lastReportTime){
							var reportOk = isReportOk(currentItem);
							if( reportOk ){
								$scope.redCheckpoints.splice(checkpointIndex, 1);
								$scope.greenCheckpoints.push(checkpoint);
							}
							checkpoint.ok = reportOk;
							checkpoint.lastReportTime = currentItem.time;
						}
					}
					i++;
				}
			}
		}
	}

	function isReportOk(report){
		var kos = [];
		if(report && report.questions){
			kos = report.questions.filter(function(question){
				return !question.yes;
			});
		}
		return kos.length ==0; 
	}
	function isLessThan24hOld(time){
		return new Date().getTime() - time < (1000 * 60 * 60 * 24) 
	}
	
  	$scope.map = { 
			center: { latitude: 45.7147, longitude: 4.85 },
			zoom: 15,
			options: {
				styles: [{	
					featureType: 'transit',
					elementType: 'labels',
					stylers: [{ visibility: 'off' }]
				},
				{	featureType: 'poi',
					elementType: 'labels',
					stylers: [{ visibility: 'off' }]
				}],
				panControl: false,
				draggable: false,
				scrollwheel: false,
				zoomControl: false,
				disableDoubleClickZoom: true,
				streetViewControl: false,
			},
			control:{},
			checkpointsControl : {},
			positionsControl: {}
		};
	$scope.maps = {};
	uiGmapGoogleMapApi.then(function(maps) {
		$scope.maps = maps;
		
		maps.event.addDomListener(window, 'resize', function() {
			resizeAndCenter($scope.map.control.getGMap(), maps.event );
		});
    });
    uiGmapIsReady.promise(1).then(function(instances) {
        instances.forEach(function(inst) {
        	resizeAndCenter(inst.map, $scope.maps.event);
        });
    });
    function resizeAndCenter(map, event){
    	var center = map.getCenter();
    	event.trigger(map,'resize');
    	map.setCenter(center);
    }
	
	$scope.markerClicked = function(marker, event, model){
		if(!model.type){
			return eventsService.setActiveItem(eventsService.findReportByCheckpointUuid(model.uuid).item, 'report');
		}
		return eventsService.setActiveItem(model, model.type);
	};
	
	$scope.$on('$destroy', function(){
		eventsService.stopAlert();
	});
}]);

module.controller('RightMenuController',['$scope','webSocket','Incidents','eventsService','$mdToast','Reports',
                                         function($scope, webSocket, Incidents, eventsService, $mdToast, Reports){
	$scope.events = eventsService.events;
	
	$scope.$on('reportSelected', function($event, report){
		var reportEvent = eventsService.getEvent(report);
		$scope.selectedEvent = angular.copy(reportEvent);
	});
	$scope.$on('incidentSelected', function($event, incident){
		var incidentEvent = eventsService.getEvent(incident);
		$scope.selectedEvent = angular.copy(incidentEvent);
	});
	$scope.$on('itemUnselected', function($event, item){
		$scope.selectedEvent = null;
	});
	
	$scope.selectEvent = function selectEvent(event){
		eventsService.setActiveItem(event.item, event.type);
	};
	
	$scope.cancelEdition = function cancelEdition(){
		eventsService.unselect();
	};
	
	$scope.fetchMoreItems = eventsService.infiniteScroll.fetchMoreItems;
	
	$scope.confirmEdition = function confirmEdition(event){
		switch(event.type){
			case 'incident':
				event.item.validated = true;
				var toUpdate = angular.copy(event.item);
				toUpdate.type = toUpdate.dbType;
				Incidents.update(toUpdate)
				.then(function(response){
					eventsService.updateItem(event.item);
					showToast("Le rapport d'incident a été mis à jour.");
					$scope.cancelEdition();
				}, 
				function(errorResponse){
					$scope.cancelEdition();
					showToast("Une erreur est survenue.");
					event.item.validated = false;
				});
				break;
			case 'report':
				Reports.update(event.item)
				.then(function(response){
					eventsService.updateItem(event.item);
					showToast("Le rapport de contrôle a été mis à jour.");
					$scope.cancelEdition();
				}, 
				function(errorResponse){
					showToast("Une erreur est survenue.");
					$scope.cancelEdition();
				});	
				break;
			default :
				break;
		
		}
	};
	
	function showToast(message) {
	    $mdToast.show($mdToast.simple().content(message).position("bottom right"));
	};
}]);

module.factory('alertSoundService', [function(){
	var service = {};
	
	service.audio = new Audio("/assets/sounds/alarm-ring.mp3");
	
	service.play = function(){
		service.audio.onended = function() {
			service.audio.currentTime = 0;
			service.audio.play();
		};
		service.audio.play();
	};
	
	service.stop = function(){
		service.audio.pause();
		service.audio.currentTime = 0;
	};
	
	return service;
}]);

module.controller('AlertDialogController', ['$scope', 'alertSoundService', '$mdDialog', 'alert',
                                            function($scope, alertSoundService, $mdDialog, alert){
	alertSoundService.play();
	
	$scope.alert = alert;
	
    $scope.$on("$destroy", function() {
        alertSoundService.stop();
    });
    
    $scope.dismiss = $mdDialog.hide;
}]);

module.directive('whenScrolled',['$window', function($window) {
    return {
    	scope: { whenScrolled: '&whenScrolled' },
    	link: function(scope, elm, attr) {
    
	        var raw = elm[0];
	
	        scope.whenScrolled();
	        elm.bind('scroll', function() {
	            if (raw.scrollTop + raw.offsetHeight >= raw.scrollHeight) {
	                scope.whenScrolled();
	            }
	        });
    	}
    };
}]);
