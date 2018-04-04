<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Secure Voting System</title>
    <jsp:include page="_styles.jsp"></jsp:include>
</head>
<body>

<jsp:include page="_menu.jsp"></jsp:include>

<div class="container">
    <% if(request.getAttribute("err") != null) {
        if(request.getAttribute("err").equals("")) {%>
    <h4 class="text-success">Success: Your election has been successfully created.</h4>
    <% } else { %>
    <h4 class="text-danger">Failure: ${err}</h4>
    <% }
    } %>

    <h3> Create an Election</h3>
    <p> Looking to create your own election? This is the page for you. From this
        dashboard you will be able to create an election by providing us a little bit of
        information about it. You will then be able to access it from your own Election
        Manager Dashboard!</p>

    <br />

    <h4>Create Election</h4>
    <form method="POST" action="${pageContext.request.contextPath}/create-election">
        <table border="0" class="table">
            <tr>
                <td>Election Name</td>
                <td><input type="text" name="electionName" title="Election Name"/> </td>
            </tr>
            <tr>
                <td colspan ="2">
                    <input type="submit" value= "Submit" />
                </td>
            </tr>
        </table>
    </form>
</div>

</body>
</html>