<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home Page</title>
</head>
<body>

<jsp:include page="_header.jsp"></jsp:include>
<jsp:include page="_menu.jsp"></jsp:include>

<h3>Register</h3>

<%-- form registers arbitrary users names and keys --%>
<form method="POST" action="${pageContext.request.contextPath}/register">
    <table border="0">
        <tr>
            <td>First Name</td>
            <td><input type="text" name="firstName" /> </td>
        </tr>
        <tr>
            <td>Last Name</td>
            <td><input type="text" name="lastName" /> </td>
        </tr>
        <tr>
            <td>Public Key</td>
            <td><input type="text" name="publicKey" /> </td>
        </tr>
        <tr>
            <td colspan ="2">
                <input type="submit" value= "Submit" />
            </td>
        </tr>
    </table>
</form>

<jsp:include page="_footer.jsp"></jsp:include>

</body>
</html>
