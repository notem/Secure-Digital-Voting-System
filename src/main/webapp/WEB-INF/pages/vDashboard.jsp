<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Secure Voting System</title>
    <jsp:include page="_styles.jsp"></jsp:include>
</head>
<body>

<jsp:include page="_menu.jsp"></jsp:include>

<div class="container">
    <h3>Voter Dashboard</h3>
    <p>This is where all of your election needs can be met. You can find election names, public keys, and see how many
        votes have been cast here.</p>

    <br />

    <h3>Upcoming Elections</h3>
    <ul>
        <c:forEach var="upcomingElection" items="${upcomingElections}">
            <li>
                <div style="max-width:600px; overflow-wrap:break-word;">
                    <h4><c:out value="${upcomingElection}" /></h4>
                </div>
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
                <div style="max-width:600px; overflow-wrap:break-word;">
                    <h4><c:out value="${activeElection}" /></h4>
                </div>
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
                <div style="max-width:600px; overflow-wrap:break-word;">
                    <h4><c:out value="${closedElection}" /></h4>
                </div>
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
