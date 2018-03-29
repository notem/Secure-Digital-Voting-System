<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Secure Voting System</title>
    <jsp:include page="_styles.jsp"></jsp:include>
</head>
<body>

<jsp:include page="_menu.jsp"></jsp:include>

<div class="container">
    <h1> Your Voter Dashboard</h1>
    <h3> As a voter, it is important for you to have as much information about the
    elections you are participating as possible. This is the wealth of knowledge that is
    available as a voter in the Secure Voting System.</h3>

    <h4>Current Elections</h4>
    <table>
        <tr>
            <td>Election Name </td>
            <td>End Date </td>
        </tr>
        <tr>
            <td>
                <c:forEach var="election" items="${elections}">
                    <li>
                        <h4><c:out value="${election}" /></h4>
                    </li>
                </c:forEach>
            </td>
            <td>
                
            </td>
        </tr>
    </table>

    <h4>Upcoming Elections</h4>
    <table>
        <tr>
            <td>Election Name </td>
            <td>Start Date </td>
            <td>End Date </td>
        </tr>
        <tr>
            <td> </td>
            <td> </td>
            <td> </td>
        </tr>
    </table>
    <h4>Past Elections</h4>
    <table>
        <tr>
            <td>Election Name </td>
            <td>End Date </td>
        </tr>
        <tr>
            <td> </td>
            <td> </td>
        </tr>
    </table>
</div>

<jsp:include page="_footer.jsp"></jsp:include>
</body>
</html>
