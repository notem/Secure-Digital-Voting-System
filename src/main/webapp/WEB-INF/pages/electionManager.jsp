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
    <h1>Election Manager Dashboard</h1>
    <h3>The Election Manager Dashboard is where you can see the status of elections, and also start and close elections.</h3>
    <div></div>

    <h3>Voters For Selected Election</h3>
    <ul>
        <c:forEach var="voter" items="${voters}">
            <li>
                <c:out value="${voter}" />
            </li>
        </c:forEach>
    </ul>

	<h3>Upcoming Elections</h3>
	<form method="POST" action="${pageContext.request.contextPath}/electionmanager">
		<ul>
			<c:forEach var="upcomingElection" items="${upcomingElections}" varStatus="loop">
				<c:set var="index" value="${loop.index}"/>
				<li>
					<h4><c:out value="${upcomingElection}" /></h4>
				</li>
				<input type="submit" name="buttonPressed" value="Activate ${upcomingNames[index]}" />
                <input type="submit" name="buttonPressed" value="View Voters ${upcomingNames[index]}" />
			</c:forEach>
		</ul>
	</form>

    <h3>Active Elections</h3>
    <form method="POST" action="${pageContext.request.contextPath}/electionmanager">
        <ul>
            <c:forEach var="activeElection" items="${activeElections}" varStatus="loop">
                <c:set var="index" value="${loop.index}"/>
                <li>
                    <h4><c:out value="${activeElection}" /></h4>
                </li>
                <input type="submit" name="buttonPressed" value="Terminate ${activeNames[index]}"/>
                <input type="submit" name="buttonPressed" value="View Voters ${activeNames[index]}"/>
            </c:forEach>
        </ul>
    </form>
    
    <!-- additional utilities go here -->
    
    <h3>Closed Elections</h3>
    <form method="POST" action="${pageContext.request.contextPath}/electionmanager">
        <ul>
            <c:forEach var="closedElection" items="${closedElections}" varStatus="loop">
                <c:set var="index" value="${loop.index}"/>
                <li>
                    <h4><c:out value="${closedElection}" /></h4>
                </li>
                <input type="submit" name="buttonPressed" value="View Voters ${closedNames[index]}" />
            </c:forEach>
        </ul>
    </form>

</div>

<jsp:include page="_footer.jsp"></jsp:include>

</body>
</html>
