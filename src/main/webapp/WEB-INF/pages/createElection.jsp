<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Secure Voting System</title>
    <jsp:include page="_styles.jsp"></jsp:include>
</head>
<body>

<jsp:include page="_menu.jsp"></jsp:include>
<jsp:include page="_scriptRegister.jsp"></jsp:include>

<div class="container">
    <% if(request.getAttribute("error") != null) {
        if(request.getAttribute("error").equals("")) {%>
    <h4 class="text-success">Success: Your election has been successfully created.</h4>
    <% } else { %>
    <h4 class="text-danger">Failure: ${error}</h4>
    <% }
    } %>
    <h1> Create an Election</h1>
    <h3> Looking to create your own election? This is the page for you. From this
        dashboard you will be able to create an election by providing us a little bit of
        information about it. You will then be able to access it from your own Election
        Manager Dashboard!</h3>

    <h4>Create Election</h4>
    <form method="POST" action="${pageContext.request.contextPath}/create-election">
        <table border="0">
            <tr>
                <td>Election Name</td>
                <td><input type="text" name="electionName" title="Election Name"/> </td>
            </tr>
            <tr>
                <td>Election Public Key</td>
                <td><input type="text" name="pubKey" title="Public Key"/></td>
            </tr>
            <tr>
                <td>Election Private Key</td>
                <td><input type="text" name="privKey" title="Private Key"/></td>
            </tr>
            <tr>
                <td colspan ="2">
                    <input type="submit" value= "Submit" />
                </td>
            </tr>
        </table>
    </form>

    <%-- generates and displays new RSA key pair on the client --%>
    <input onclick="generateKeys()" type="button" value= "Generate New RSA Keys" />
    <table border="0" style="table-layout:fixed;width:100%">
        <tr>
            <td><h4>Public Key</h4></td>
        </tr>
        <tr>
            <td><p>The voter public key is used by the system to identify a registered voter.
                The public key is also used to verify the integrity of a ballot when the election ends.
                When a public-private key pair is generated, the public key component required for
                registration is automatically inserted into the form.</p></td>
        </tr>
        <tr>
            <%-- generated jwk public key goes here --%>
            <td><code id="publicKeyExport" style="overflow-wrap:break-word;"></code></td>
        </tr>
        <tr>
            <td><h4>Private Key</h4></td>
        </tr>
        <tr>
            <td><p>The voter private key is used to sign the voter's ballot. The private key should
                be known only by the voter, and should never be revealed to others.</p></td>
        </tr>
        <tr>
            <%-- generated jwk private key goes here --%>
            <td><code id="privateKeyExport" style="overflow-wrap:break-word;"></code></td>
        </tr>
    </table>
</div>

</body>
</html>