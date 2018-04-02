<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Voter Registration Page</title>
    <jsp:include page="_styles.jsp"></jsp:include>
</head>

<jsp:include page="_scriptBallot.jsp"></jsp:include>

<body>
<jsp:include page="_menu.jsp"></jsp:include>

<div class="container">
    <%-- insert registration success message --%>
    <% if(request.getAttribute("error") != null) {
        if(request.getAttribute("error").equals("")) {%>
    <h4 class="text-success">Success: Your ballot has been submitted.</h4>
    <% } else { %>
    <h4 class="text-danger">Failure: ${error}</h4>
    <% }
    } %>
    <br />

    <h3>Submit Ballot</h3>
    <p>In order to submit a ballot container your vote, the ballot must be generated (client-side).
        Your voter private key and the election's public key must be imported before choosing a candidate and creating the ballot (see below).</p>
    <p id="test"></p>
    <form method="POST" action="${pageContext.request.contextPath}/ballot">
        <table border="0">
            <tr>
                <td>Election Name</td>
                <td>
                    <input type="text" name="electionName" id="electionName" title="Election Name" />
                </td>
            </tr>
            <tr>
                <td>Encrypted Ballot</td>
                <td>
                    <input type="text" name="ballot" id="ballot" title="Encrypted Ballot" />
                </td>
            </tr>
            <tr>
                <td colspan ="2">
                    <input type="submit" value= "Submit" />
                </td>
            </tr>
        </table>
    </form>

    <br />

    <h3>Import Private Key for Signing</h3>
    <p>Your voter private key is used to sign your ballot.
        This mechanism insures that your vote is not tampered with also insuring that the ballot comes from an authorized voter.
        Your voter private key was generated when registering.</p>
    <table border="0" style="table-layout:fixed;width:100%">
        <tr>
            <!-- imported signing key goes here -->
            <td id="signingKey_display" colspan="2"></td>
        </tr>
        <tr>
            <td>Voter Signing Key</td>
            <td><input type="text" name="signingKey" id="signingKey" title="Private Key"/> </td>
        </tr>
        <tr>
            <td colspan ="2">
                <input onclick="importSigningKey()" type="button" value="Import" />
            </td>
        </tr>
    </table>

    <br />

    <h3>Import Public Election Key</h3>
    <p>The election's public key is used to encrypt your ballot and confidentiality while the election is ongoing.
        The election key can be found on [wherever it is found].</p>
    <table border="0" style="table-layout:fixed;width:100%">
        <tr>
            <!-- imported election key goes here -->
            <td id="electionKey_display" colspan="2"></td>
        </tr>
        <tr>
            <td>Election Encryption Key</td>
            <td><input type="text" name="electionKey" id="electionKey" title="Election Key" value=""/> </td>
        </tr>
        <tr>
            <td colspan ="2">
                <input onclick="importElectionKey()" type="button" value="Import" />
            </td>
        </tr>
    </table>

    <br />

    <h3>Ballot Creation</h3>
    <p>Your voter public key, your candidate choice, and the current time form the contents of a ballot.
        Use the form below to write-in your candidate choice and create the ballot.
        The created ballot will automatically be filled into the Submit Ballot form.</p>
    <table border="0" style="table-layout:fixed;width:100%">
        <tr>
            <!-- imported election key goes here -->
            <td id="ballot_display" colspan="2"></td>
        </tr>
        <tr>
            <td>Candidate Choice</td>
            <td><input type="text" name="candidate" id="candidate" title="Candidate"/> </td>
        </tr>
        <tr>
            <td colspan ="2">
                <input onclick="createBallot()" type="button" value="Create"/>
            </td>
        </tr>
    </table>
</div>

<jsp:include page="_footer.jsp"></jsp:include>

</body>
</html>
