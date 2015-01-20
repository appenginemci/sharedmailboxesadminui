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

<body ng-controller="listCtrl">
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

	<div>
		<a href="/add">Add Event</a>
		<br /> <br />
	</div>

	<div id="tables">
		<form action="/modify" id="listForm" name="listForm">
		<table class="table table-condensed">
			<thead>
				<tr class="border">
					<th>Event</th>
					<th>Type</th>
					<th>Action</th>
				</tr>			
			</thead>
			<tbody>
     			<tr ng-repeat="row in events" >
					<td>{{row.name}}</td>
					<td>{{row.type}}</td>
					<td>
					<button type="submit" ng-click="submit(row)">Manage</button>
					</td>
				</tr>
			</tbody>
		</table>
		<input type="hidden" name="groupId" id="groupId"/>
		</form>
	</div>

</body>
</html>