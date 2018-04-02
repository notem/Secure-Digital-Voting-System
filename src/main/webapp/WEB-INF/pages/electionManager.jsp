<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Election Manager Dashboard</title>
    <jsp:include page="_styles.jsp"></jsp:include>
</head>
<body>

<jsp:include page="_menu.jsp"></jsp:include>

<div class="container">
	<h3>Upcoming Elections</h3>
	<ul>
		<c:forEach var="upcomingElection" items="${upcomingElections}">
			<li>
				<h4><c:out value="${upcomingElection}" /></h4>
				<ul>
					<c:forEach var="voter" items="${voters}">
						<li>
							<c:out value="${voter}" />
						</li>
					</c:forEach>
				</ul>
			</li>
		</c:forEach>
	</ul>

    <h3>Active Elections</h3>
    <ul>
    	<c:forEach var="activeElection" items="${activeElections}">
    		<li>
    			<h4><c:out value="${activeElection}" /></h4>
    			<ul>
			        <c:forEach var="voter" items="${voters}">
						<li>
							<c:out value="${voter}" />
			        	</li>
			    	</c:forEach>
			    </ul>
			</li>
		</c:forEach>
    </ul>
    
    <!-- additional utilities go here -->
    
    <h3>Closed Elections</h3>
	<ul>
		<c:forEach var="closedElection" items="${closedElections}">
			<li>
				<h4><c:out value="${closedElection}" /></h4>
				<ul>
					<c:forEach var="voter" items="${voters}">
						<li>
							<c:out value="${voter}" />
						</li>
					</c:forEach>
				</ul>
			</li>
		</c:forEach>
	</ul>
</div>

<jsp:include page="_footer.jsp"></jsp:include>

</body>
</html>
