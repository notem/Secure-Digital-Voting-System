<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Voter Registration Page</title>
    <jsp:include page="_styles.jsp"></jsp:include>
</head>

<jsp:include page="_scriptRegister.jsp"></jsp:include>

<body>
<jsp:include page="_menu.jsp"></jsp:include>

<div class="container">
    <%-- insert registration success message --%>
    <% if(request.getAttribute("error") != null) {
        if(request.getAttribute("error").equals("")) {%>
            <h4 class="text-success">Success: The current state of the block-chain is listed below.</h4>
        <% } else { %>
            <h4 class="text-danger">Failure: ${error}</h4>
        <% }
    } %>

    <h3>View an Election's Blockchain</h3>
    <p>Election block-chains are publicly viewable (but not writeable) so as to allow voters to verify their vote has
        been counted. You can use the form below to request to view an election's public block-chain.</p>
    <%-- form allows users to request to view election block-chain --%>
    <form method="POST" action="${pageContext.request.contextPath}/view">
        <table border="0" class="table">
            <tr>
                <td>Election Name</td>
                <td><input type="text" id="electionName" name="electionName" title="The name of the election to query for."/> </td>
            </tr>
            <tr>
                <td colspan ="2">
                    <input type="submit" value= "Submit" />
                </td>
            </tr>
        </table>
    </form>

    <br />


    <%-- table to summarized results --%>
    <c:if test="${results.size() > 0}">
        <h4>Results Summary</h4>
        <p>Below is a summary of the vote count seen in the block-chain.</p>
        <table border="0" class="table">
            <tr>
                <th>Candidate</th><th>Count</th>
            </tr>
            <c:forEach var="entry" items="${results}">
                <tr>
                    <td>${entry.key}</td>
                    <td>${entry.value}</td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    <br />

    <%-- table to display blockchain --%>
    <h4>Election Block-chain</h4>
    <p>In a real-world application of this system, the voter's client application should be capable of decrypt the
        block-chain and verify the election results. Unfortunately, our proof-of-concept does not support this.
        Instead, this prototype decrypts on the server-side and sends decrypted information to the client for display.</p>
    <table border="0" class="table table-responsive">
        <tr>
            <th>#</th><th>Timestamp</th><th>Block</th><th>Hash</th>
        </tr>
        <c:forEach var="block" items="${blocks}">
            <tr>
                <td><c:out value="${block.no}" /></td>
                <td><c:out value="${block.time}" /></td>
                <%-- if block object has a ballot, show the ballot contents (human readable) --%>
                <c:choose>
                    <c:when test="${block.getBallot() != null}">

                        <c:set var="ballot" value="${block.getBallot()}" />
                        <td>
                            <table border="0" class="table">
                                <tr>
                                    <td>Voter:</td>
                                    <td>
                                        <div style="max-width: 250px; overflow: auto">
                                            <c:out value="${ballot.modulus}" />
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Candidate:</td>
                                    <td><c:out value="${ballot.candidate}" /></td>
                                </tr>
                                <tr>
                                    <td>Timestamp:</td>
                                    <td><c:out value="${ballot.timestamp}" /></td>
                                </tr>
                            </table>
                        </td>
                    </c:when>
                    <%-- otherwise, just show raw contents --%>
                    <c:otherwise>
                        <td>
                            <div style="max-width: 350px; overflow: auto">
                                <c:out value="${block.content}" />
                            </div>
                        </td>
                    </c:otherwise>
                </c:choose>
                <td>
                    <div style="max-width: 350px; overflow: auto">
                        <c:out value="${block.hash}" />
                    </div>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

<jsp:include page="_footer.jsp"></jsp:include>

</body>
</html>
