<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<h3>Registered Voters</h3>
<%-- show list of registered voters --%>
<ul>
    <c:forEach var="voter" items="${voters}">
        <li>
            <c:out value="${voter}" />
        </li>
    </c:forEach>
</ul>

<jsp:include page="_footer.jsp"></jsp:include>

</body>
</html>
