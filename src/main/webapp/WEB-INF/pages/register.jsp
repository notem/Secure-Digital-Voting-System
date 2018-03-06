<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Voter Registration Page</title>
    <jsp:include page="_styles.jsp"></jsp:include>
</head>
<script language="JavaScript">
    <!--
    function generateKeys()
    {
        /* WebCryptoAPI key generation parameters */
        var algorithmKeyGen = {
            name: "RSASSA-PKCS1-v1_5",
            // RsaHashedKeyGenParams
            modulusLength: 2048,
            publicExponent: new Uint8Array([0x01, 0x00, 0x01]),  // e=65537
            hash: {
                name: "SHA-256"
            }
        };
        var extractable = true;
        var usage = ["sign", "verify"];

        /* generate public-private RSA keypair */
        window.crypto.subtle.generateKey(algorithmKeyGen, extractable, usage)
            .then(function(keys){

                /* export the public key json web key*/
                window.crypto.subtle.exportKey("jwk", keys.publicKey).then(function(data){
                    document.getElementById('modulusRegister').value = data.n; // modulus
                    document.getElementById('publicKeyExport').innerHTML = JSON.stringify(data);
                }).catch(function(err){
                    document.getElementById('publicKeyExport').innerHTML = "Error exporting public key to JsonWebKey:"+err;
                });

                /* export the private key as json web key*/
                window.crypto.subtle.exportKey("jwk", keys.privateKey).then(function(data){
                    document.getElementById('privateKeyExport').innerHTML = JSON.stringify(data);
                }).catch(function(err){
                    document.getElementById('privateKeyExport').innerHTML = "Error exporting private key to JsonWebKey: "+err;
                });
            }).catch(function(err){
                document.getElementById('publicKeyExport').innerHTML = "Error generating RSA keys: "+err;
                document.getElementById('privateKeyExport').innerHTML = "Error generating RSA keys: "+err;
            });
    }
    //-->
</script>

<body>

<jsp:include page="_menu.jsp"></jsp:include>

<div class="container">
    <%-- insert registration success message --%>
    <% if(request.getAttribute("error") != null) {
        if(request.getAttribute("error")=="") {%>
            <h4 class="text-success">Success: Your information has been successfully registered.</h4>
        <% } else { //TODO learn how to insert the error variable into the page's error message %>
            <h4 class="text-danger">Failure: There was an error registering your information!</h4>
        <% }
    } %>

    <h3>Voter Registration</h3>
    <%-- this form registers arbitrary user names and keys --%>
    <form method="POST" action="${pageContext.request.contextPath}/register">
        <table border="0">
            <tr>
                <td>First Name</td>
                <td><input type="text" name="firstName" title="First Name"/> </td>
            </tr>
            <tr>
                <td>Last Name</td>
                <td><input type="text" name="lastName"  title="Last Name"/> </td>
            </tr>
            <tr>
                <td>Public Key</td>
                <td><input type="text" id="modulusRegister" name="publicKey" title="RSA Public Key"/> </td>
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
    <table border="0">
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
            <td><code id="publicKeyExport"></code></td>
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
            <td><code id="privateKeyExport"></code></td>
        </tr>
    </table>
</div>

<jsp:include page="_footer.jsp"></jsp:include>

</body>
</html>
