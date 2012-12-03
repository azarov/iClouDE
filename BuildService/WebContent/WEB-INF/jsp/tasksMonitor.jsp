<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<h1>Tasks queue :</h1>
	<table border="1">
		<tr>
			<th>Id</th>
			<th>Path</th>
			<th>Status</th>
			<th>Queuing time</th>
			<th>Compilation start time</th>
			<th>Show build logs</th>
		</tr>

		<c:forEach var="task" items="${it.tasks}">
			<tr>
				<th> <c:out value="${task.id}" /> </th>
				<th> <c:out value="${task.fullPathToZip}" /> </th>
				<th> <c:out value="${task.status}" /> </th>
				<th> <c:out value="${task.queuingTime}" /> </th>
				<th> <c:out value="${task.complitaionStartTime}" /> </th>
				<th> <a href="buildLogs?id=${task.id}" target="_new">show logs</a> </th>				
			</tr>
		</c:forEach>
	</table>
</body>
</html>