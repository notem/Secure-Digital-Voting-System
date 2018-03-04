<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Simple Web Application</title>
</head>

<body>
<jsp:include page="/WEB-INF/pages/_header.jsp"></jsp:include>
<jsp:include page="/WEB-INF/pages/_menu.jsp"></jsp:include>

<h2>Simple Web Application using JSP, Servlet & PostGreSQL</h2>

<ul>
    <li><a href="register">register</a></li>
    <li><a href="voters">voters</a></li>
</ul>

<jsp:include page="/WEB-INF/pages/_footer.jsp"></jsp:include>
</body>
</html>
