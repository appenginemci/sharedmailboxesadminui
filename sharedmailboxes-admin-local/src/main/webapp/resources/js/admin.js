var app = angular.module('myApp', ['ui.bootstrap']);

app.factory("services", ['$http', function($http) {
    var obj = {};
    obj.getEvents = function(){
        return $http.get('/data?type=getEvents');
    };

    obj.getAllUsers = function() {
    	return $http.get('/data?type=getAllUsers');
    };
    
    obj.checkUser = function(user) {
    	return $http.get('/data?type=checkUser&user='+user);
    }
    
    obj.checkEventName = function(eventName) {
    	return $http.get('/data?type=checkEventName&eventName='+eventName);
    }
    
    obj.checkEventMail = function(eventMail) {
    	return $http.get('/data?type=checkEventMail&eventMailPrefix='+eventMail);
    }
    
    obj.addEvent = function(eventToCreate) {
    	return $http.get('/data?type=addEvent&eventToCreate='+eventToCreate, { headers : {'Content-Type' : "application/x-www-form-urlencoded"}});
    };
    
    obj.updEvent = function(eventToUpdate) {
    	return $http.get('/data?type=updEvent&eventToUpdate='+eventToUpdate, { headers : {'Content-Type' : "application/x-www-form-urlencoded"}});
    };
    
    obj.getUserAndEvent = function(groupId){
        return $http.get('/data?type=getUserAndEvent&groupId='+groupId);
    };
    
    obj.getAllSites = function(){
        return $http.get('/data?type=getAllSites');
    };
    
    obj.createNewSite = function(newSite){
        return $http.get('/data?type=createNewSite&newSite='+newSite);
    };

    
    return obj;   
}]);


app.controller('listCtrl', function($scope, $filter, services) {

	services.getEvents().success(function(data) {
		console.log(data);
		$scope.events = data;
	});

	$scope.submit = function(row) {
		console.log(row);
		$("#groupId").val(row.groupId);
		$("#formList").submit();
	}

});

app.controller('addEvt', function ($scope, $filter, services) {
	$scope.users = [];
	$scope.alerts = [];
	$scope.event = {};
	$scope.user = {};
	$scope.eventTypes = [
	                          	{'name': 'Abstract',
	                          	'value': 'abstract'},
	                          	{'name': 'Congress',
	                          	'value': 'congress'}
	                          ];
	
	$scope.event.type = $scope.eventTypes[1].value;
	
	
	services.getAllSites().success(function(data) {
		console.log(data);
		$scope.eventSites = data;
    });
	
//	$scope.event.site = $scope.eventSites[0].folder_id;
	
	$scope.userRoles = [
	                          	{
	                          		'name': 'Team Member',
		                          	'value': 'teamMember'
		                        },
		                        {
		                        	'name': 'Event Head',
		                          	'value': 'eventHead'
		                        },
		                        {
		                        	'name': 'Pool Head',
		                        	'value': 'poolHead'
		                        }
		                     ];
	$scope.user.role = $scope.userRoles[0].value;
	
	services.getAllUsers().success(function(data) {
		console.log(data);
		$scope.allUsers = data;
    });
	
	$scope.addEvt = function() {
		console.log($scope.event);
		$scope.event["users"] = $scope.users;
		console.log($scope.users);
		//$scope.event.push({"users":$scope.users});
		
		services.addEvent(encodeURIComponent(JSON.stringify($scope.event))).success(function(data) {
			console.log(data);
			if(data.status == "failure") {
				displayAlert('danger', data.messages);
			} else if (data.status == "success") {
				displayAlert('info', ['Event correctly created']);
				$scope.users = [];
				$scope.event = {};
			}
		});
	}
	
	$scope.addUser = function() {
		console.log($scope.user);
		services.checkUser($scope.user.mail).success(function(data) {
			console.log(data);
			if(data == 'true') {
				if(!alreadyInTable($scope.user.mail)) {
					$scope.users.push({'mail':$scope.user.mail,'role':$scope.user.role});
				}
			} else {
				displayAlert('danger', ['The user does not exist in the group domain']);
			}
			$scope.user={};
		});
	}
	
	$scope.removeUser = function(rowUser, index) {
		$scope.users.splice(index, 1);
	}
	
	$scope.createNewSite = function() {
		console.log("new site = " + $scope.newSite);
		
				services.createNewSite(encodeURIComponent(($scope.newSite))).success(function(data) {
					
					if(data.status == "failure") {
						console.log("error on new site creation");
						displayAlert('danger', data.messages);
					} else if (data.status == "success") {
						console.log("New site creation -OK-");
						
						// Update of the site list
						services.getAllSites().success(function(data) {
							console.log(data);
							$scope.eventSites = data;
					    });
						
						displayAlert('info', ['New Site correctly created']);
					}
				});
	}
	
	$scope.checkEventName = function() {
		console.log($scope.event.name);
		services.checkEventName($scope.event.name).success(function(data) {
			console.log(data);
			if(data == 'false') {
				$scope.event.name="";
				displayAlert('danger', ['The event name is already used']);
			} else if (data == 'error'){
				$scope.event.name="";
				displayAlert('danger', ['Error when checking the event name']);
			}
		});
	}
	
	$scope.checkEventMail = function() {
		console.log($scope.event.mail);
		var emailPattern = new RegExp(/^[a-zA-Z0-9\._\-]+$/i);
		if(!emailPattern.test($scope.event.mail)) {
			$scope.event.mail="";
			displayAlert('danger', ['The event mail prefix doesnot match the pattern (only letters, digits, -, _, . are allowed)']);
			return false;
		} else {
			services.checkEventMail($scope.event.mail).success(function(data) {
			console.log(data);
			if(data == 'false') {
				$scope.event.mail="";
				displayAlert('danger', ['The event mail prefix is already used']);
				return false;
			} else if (data == 'error'){
				$scope.event.name="";
				displayAlert('danger', ['Error when checking the event mail prefix']);
				return false;
			}
		
			});
		}
		return true;
	}
	
	
	displayAlert = function(type, msg) {
		$scope.alerts = [];
		if(msg.length > 0) {
			for(i=0; i < msg.length; i++) {
				$scope.alerts[i] = { 'type': type, 'msg': msg[i] };
			}
		} else {
			$scope.alerts[0] = { 'type': type, 'msg': msg };
		}
	}
	
	alreadyInTable = function(userMail) {
		var found = false;
		for(i=0; !found && i < $scope.users.length; i++) {
			if($scope.users[i].mail == userMail) {
				found = true;
			}
		}
		return found;
	}
});

app.controller('modifyEvt', function ($scope, $filter, services) {
	console.log("groupId = " + $("#groupId").val());
	$scope.originalUsers = {};
	$scope.userRoles = [
	                          	{
	                          		'name': 'Team Member',
		                          	'value': 'teamMember'
		                        },
		                        {
		                        	'name': 'Event Head',
		                          	'value': 'eventHead'
		                        },
		                        {
		                        	'name': 'Pool Head',
		                        	'value': 'poolHead'
		                        }
		                     ];
	services.getAllUsers().success(function(data) {
		console.log(data);
		$scope.allUsers = data;
    });
	
	services.getUserAndEvent($("#groupId").val()).success(function(data) {
		console.log(data);
		$scope.event = data;
		$scope.originalUsers = JSON.parse(JSON.stringify(data.users));
    });
	
	$scope.updEvt = function() {
		console.log($scope.event);
		//$scope.event["users"] = $scope.users;
		console.log($scope.event);
		//$scope.event.push({"users":$scope.users});
		
		services.updEvent(encodeURIComponent(JSON.stringify($scope.event))).success(function(data) {
			console.log(data);
			if(data.status == "failure") {
				displayAlert('danger', data.messages);
			} else if (data.status == "success") {
				displayAlert('info', ['Event correctly updated']);
			}
		});
	}
	
	$scope.addUser = function() {
		console.log($scope.user);
		services.checkUser($scope.user.mail).success(function(data) {
			console.log(data);
			if(data == 'true') {
				if(!alreadyInTable($scope.user.mail)) {
					$scope.event.users.push({'mail':$scope.user.mail,'role':$scope.user.role});
					displayAlert('info', ['User correctly added']);
				}
			} else {
				displayAlert('danger', ['The user does not exist in the group domain']);
			}
			$scope.user={};
		});
	}
	
	$scope.removeUser = function(rowUser, index) {
		$scope.event.users.splice(index, 1);
	}
	
	alreadyInTable = function(userMail) {
		var found = false;
		for(i=0; !found && i < $scope.event.users.length; i++) {
			if($scope.event.users[i].mail == userMail) {
				found = true;
			}
		}
		return found;
	}
	
	displayAlert = function(type, msg) {
		$scope.alerts = [];
		if(msg.length > 0) {
			for(i=0; i < msg.length; i++) {
				$scope.alerts[i] = { 'type': type, 'msg': msg[i] };
			}
		} else {
			$scope.alerts[0] = { 'type': type, 'msg': msg };
		}
	}
});