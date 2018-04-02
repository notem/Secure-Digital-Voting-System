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
            <h4 class="text-success">Success: Your information has been successfully registered.</h4>
        <% } else { %>
            <h4 class="text-danger">Failure: ${error}</h4>
        <% }
    } %>

    <h3>Voter Registration</h3>
    <p>Users must register to vote in a particular election. In order to register, the user's personal information must
        be supplied along with a public key to be identified with.</p>
    <%-- this form registers arbitrary user names and keys --%>
    <form method="POST" action="${pageContext.request.contextPath}/register">
        <table border="0">
            <tr>
                <td>First Name</td>
                <td><input type="text" name="firstName" id="fname" title="Your first name"/> </td>
            </tr>
            <tr>
                <td>Last Name</td>
                <td><input type="text" name="lastName" id="lname" title="Your last name"/> </td>
            </tr>
            <tr>
                <td>Public Key</td>
                <td><input type="text" id="modulusRegister" name="publicKey" title="Your voter public key"/> </td>
            </tr>
            <tr>
                <td>Signature</td>
                <td><input type="text" id="signature" name="signature" title="The RSA public key signature is used by the server to verify that you own the public key."/> </td>
            </tr>
            <tr>
                <td>Election</td>
                <td><input type="text" id="electionName" name="electionName" title="The name of the election you are registering for."/> </td>
            </tr>
            <tr>
                <td colspan ="2">
                    <input type="submit" value= "Submit" />
                </td>
            </tr>
        </table>
    </form>

    <br />
    <br />

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

<jsp:include page="_footer.jsp"></jsp:include>

</body>
</html>
