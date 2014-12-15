<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html ng-app="myApp" ng-app lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

<title>MCI Event Manager Administration</title>
<style type="text/css"> .ui-helper-hidden-accessible{display:none} </style>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>

<link href="/resources/styles/bootstrap.min.css" rel="stylesheet"></link>
<style type="text/css">
    ul>li, a{cursor: pointer;}
    input.ng-invalid {
    border-color: #FA787E;
  }

  input.ng-valid {
    border-color: #78FA89;
  }
  
  .blue {
  	color: #2a6496;
  }
</style>

<script src="/resources/js/angular.min.js"></script>
<script src="/resources/js/jquery-1.11.1.min.js"></script>
<script src="/resources/js/admin.js"></script>        
<script type="text/javascript" src="/resources/js/ui-bootstrap-tpls-0.11.0.min.js"></script>

</head>

<body ng-controller="modifyEvt">
	<div>
  		<alert ng-repeat="alert in alerts" type="{{alert.type}}" close="closeAlert()">{{alert.msg}}</alert>
	</div>
	<input type="hidden" ng-model="groupId" id="groupId" value="<%=request.getParameter("groupId")%>"/>
	<nav class="navbar navbar-inverse navbar-static-top" role="navigation">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<a class="navbar-brand" href="#">MCI Event Manager</a>
			</div>

			<!-- Collect the nav links, forms, and other content for toggling -->
			<div class="collapse navbar-collapse"
				id="bs-example-navbar-collapse-1">
				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown"><c:out value="${session.email}" escapeXml="false"/> <span class="caret"></span></a>
					</li>
				</ul>
			</div>
			<!-- /.navbar-collapse -->
  		</div><!-- /.container-fluid -->
	</nav>
	
	
	<div id="tables">
		<form novalidate>
			<div class="col-md-6">
				<h2>Management of event : {{event.name}}</h2>
				<table class="table table-striped" ng-show="event.users.length > 0">
				<thead>
					<tr>
						<th>User</th>
						<th>Type</th>
						<th>Cancel</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="rowUser in event.users">
						<td>{{rowUser.mail}}</td>
						<td>{{rowUser.role}}</td>
						<td><button class="btn btn-default" ng-click="removeUser(rowUser, $index)"><span class="glyphicon glyphicon-remove" ></span></button></td>
					</tr>
				</tbody>
			</table>
			
			</div>
			<div class="col-md-6">
			<fieldset><legend>Add a member</legend>
			<div class="form-group">
				<label for="userMail">User</label>
				<input type="text" class="form-control" id="userMail" ng-model="user.mail" placeholder="User mail" typeahead="mail for mail in allUsers | filter:$viewValue | limitTo:8"/>
			</div>
			<div class="form-group">
				<label for="userRole">Role</label>
				<select class="form-control" ng-model="user.role" id="userRole">
    				<option value="teamMember" selected>Team Member</option>
    				<option value="eventHead">Event Head</option>
    				<option value="poolHead">Pool Head</option>
    			</select>
				
			</div>
			<div class="form-group">
				<button type="button" class="btn btn-primary" ng-click="addUser(user)">Add User</button>
			</div>
			</fieldset>
			</div>
			
			<div class="col-md-12">
				<button type="submit" class="btn btn-default" ng-click="updEvt(event)">Update Event</button>
			</div>
	  </form>
	</div>

</body>
</html>