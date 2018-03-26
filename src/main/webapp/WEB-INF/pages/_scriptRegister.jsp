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

    /**
     * generate keys, create public key signature, set values in form
     */
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

        /*
         * generate new public-private RSA(2048) key pair
         */
        window.crypto.subtle.generateKey(algorithmKeyGen, extractable, usage)
            .then(function(keys){
                /*
                 * export the public key as spki
                 */
                window.crypto.subtle.exportKey("spki", keys.publicKey)
                    .then(function(data){
                        var str = String.fromCharCode.apply(null, new Uint8Array(data)); // key export as string
                        document.getElementById('publicKeyExport').innerHTML = btoa(str); // encode as base64
                    })
                    .catch(function(err) {
                        document.getElementById('publicKeyExport').innerHTML = "Error exporting public key to X.509 SPKI format: "+err;
                    });

                /*
                 * export the private key as pkcs8
                 */
                window.crypto.subtle.exportKey("pkcs8", keys.privateKey)
                    .then(function(data){
                        var str = String.fromCharCode.apply(null, new Uint8Array(data)); // key export as string
                        document.getElementById('privateKeyExport').innerHTML = btoa(str); // encode as base64
                    })
                    .catch(function(err){
                        document.getElementById('privateKeyExport').innerHTML = "Error exporting private key to PKCS#8 format: "+err;
                    });

                /*
                 * export the public key json web key and extract the public modulus
                 */
                window.crypto.subtle.exportKey("jwk", keys.publicKey)
                    .then(function(data){

                        /* adapt the base64url encoding to base64 */
                        var modulus = data.n.replace(/-/g,"+").replace(/_/g,"/");                       // re-map characters
                        modulus = modulus.padRight(modulus.length + (4 - modulus.length % 4) % 4, '='); // fix padding

                        /* fill the public modulus form entry */
                        document.getElementById('modulusRegister').value = modulus;  // encoded modulus to be sent to the server

                        // convert base64 encoded modulus to uint8 array for signing
                        var bytes = Uint8Array.from(window.atob(modulus), function(c) {return c.charCodeAt(0)});

                        /*
                         * sign the public modulus using the private key
                         */
                        window.crypto.subtle.sign(
                            {
                                name: "RSASSA-PKCS1-v1_5"
                            },
                            keys.privateKey,
                            bytes.buffer
                        )
                            .then(function(signature){
                                /* encode the signature under base64 */
                                var str = String.fromCharCode.apply(null, new Uint8Array(signature)); // sig as string
                                document.getElementById('signature').value = btoa(str); // encoded signature to be sent to server
                            })
                            .catch(function(err){
                                document.getElementById('signature').value = "Error signing modulus: "+err;
                            });
                    })
                    .catch(function(err){
                        document.getElementById('signature').innerHTML = "Error exporting public key to JsonWebKey: "+err;
                    });
            })
            .catch(function(err){
                document.getElementById('publicKeyExport').innerHTML = "Error generating RSA keys: "+err;
                document.getElementById('privateKeyExport').innerHTML = "Error generating RSA keys: "+err;
            });
    }
    //-->
</script>

