<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<script language="JavaScript">
    <!--
    /* used to convert base64url to base64 (plain) */
    String.prototype.padRight = function(n, pad){
        t = this;
        if(n > this.length)
            for(i = 0; i < n-this.length; i++)
                t += pad;
        return t;
    };

    /* generate keys, create public key signature, set values in form */
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

                /* export the public key json web key and create the 'challenge' */
                window.crypto.subtle.exportKey("jwk", keys.publicKey).then(function(data){

                    /* adapt the base64url encoding to base64 */
                    var modulus = data.n.replace(/\-/g,"+").replace(/\_/g,"/"); // translate
                    modulus = modulus.padRight(modulus.length + (4 - modulus.length % 4) % 4, '='); // pad

                    document.getElementById('modulusRegister').value = modulus; // modulus to be sent to server
                    document.getElementById('publicKeyExport').innerHTML = JSON.stringify(data);

                    /* sign the public modulus using the private key */
                    var bytes = Uint8Array.from(window.atob(modulus), function(c) {return c.charCodeAt(0)});
                    window.crypto.subtle.sign(
                        {name: "RSASSA-PKCS1-v1_5"},
                        keys.privateKey,
                        bytes.buffer
                    ).then(function(signature){
                        /* encode the signature under base64 */
                        var str = String.fromCharCode.apply(null, new Uint8Array(signature)); // sig as string
                        document.getElementById('signature').value = btoa(str); // encoded signature to be sent to server
                    }).catch(function(err){
                        document.getElementById('signature').value = err;
                    });
                }).catch(function(err){
                    document.getElementById('publicKeyExport').innerHTML = "Error exporting public key to JsonWebKey: "+err;
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

