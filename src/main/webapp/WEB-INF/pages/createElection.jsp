<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Secure Voting System</title>
    <jsp:include page="_styles.jsp"></jsp:include>
</head>
<body>

<jsp:include page="_menu.jsp"></jsp:include>

<div class="container">
    <h1> Create an Election</h1>
    <h3> Looking to create your own election? This is the page for you. From this
    dashboard you will be able to create an election by providing us a little bit of
    information about it. You will then be able to access it from your own Election
    Manager Dashboard!</h3>

    <h4>Create Election</h4>
    <form method="POST">
        <table border="0">
            <tr>
                <td>Election Name</td>
                <td><input type="text" name="electionName" title="Election Name"/> </td>
            </tr>
            <tr>
                <td>Start Date</td>
                <td><input type="text" name="startDate"  title="Start Date"/> </td>
            </tr>
            <tr>
                <td>End Date</td>
                <td><input type="text" id="endDate" name="endDate" title="End Date"/> </td>
            </tr>
            <tr>
                <td>Description</td>
                <td><input type="text" id="description" name="description" title="Description" /></td>
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
