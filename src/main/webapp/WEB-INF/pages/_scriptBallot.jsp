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

    /** global variables store imported key information */
    var signingKey, electionKey;

    /**
     * imports the voter's private key to be used to sign the ballot
     */
    function importSigningKey()
    {
        /* key information */
        var b64 = document.getElementById('signingKey').value;
        var keyData = Uint8Array.from(window.atob(b64), function(c) {return c.charCodeAt(0)});

        /* algorithm is RSA-OAEP using SHA-256 */
        var algorithmOps = {
            name: "RSASSA-PKCS1-v1_5",
            hash: {name: "SHA-256"}
        };

        /* import key and set global variable if successful */
        window.crypto.subtle.importKey(
            "pkcs8",        // key format
            keyData,        // key information
            algorithmOps,   // algorithm details
            true,           // is exportable
            ["sign"]        // for signing
        )
            .then(function(key){
                signingKey = key;   // set global
                document.getElementById('signingKey').value = "";   // clear input
                /*
                 * re-export signing key so as to verify key was imported correctly
                 */
                window.crypto.subtle.exportKey("pkcs8", signingKey)
                    .then(function(data){
                        var str = String.fromCharCode.apply(null, new Uint8Array(data)); // key export as string
                        document.getElementById('signingKey_display').innerHTML =
                            "<p style=\"overflow-wrap:break-word;\"><code>"+btoa(str)+"</code></p>";
                    })
                    .catch(function(err) {
                        document.getElementById('signingKey_display').innerHTML = "<p>Failed to re-export key! Error: "+err+"</p>";
                    });
            })
            .catch(function(err){
                signingKey = null;
                document.getElementById('signingKey_display').innerHTML = "<p>Failed to import key! Error: "+err+"</p>";
                document.getElementById('signingKey').value = "";
            });
    }

    /**
     * imports the election's public key to be used to encrypt the ballot
     */
    function importElectionKey()
    {
        /* key information */
        var b64 = document.getElementById('electionKey').value;
        var keyData = Uint8Array.from(window.atob(b64), function(c) {return c.charCodeAt(0)});

        /* algorithm is RSA-OAEP using SHA-256 */
        var algorithmOps = {
            name: "RSA-OAEP",
            hash: {name: "SHA-256"}
        };

        /* import key and set global variable if successful */
        window.crypto.subtle.importKey(
            "spki",        // key format
            keyData,       // key information
            algorithmOps,  // algorithm options
            true,          // is exportable
            ["encrypt"]    // for encryption
        )
            .then(function(key){
                electionKey = key;  // set global
                document.getElementById('electionKey').value = "";  // clear input
                /*
                 * re-export key and display the key as active election key
                 */
                window.crypto.subtle.exportKey("spki", electionKey)
                    .then(function(data){
                        var str = String.fromCharCode.apply(null, new Uint8Array(data)); // key export as string
                        document.getElementById('electionKey_display').innerHTML =
                            "<p style=\"overflow-wrap:break-word;\"><code>"+btoa(str)+"</code></p>";
                    })
                    .catch(function(err){
                        document.getElementById('electionKey_display').innerHTML = "<p>Failed to re-export key! Error: "+err+"</p>";
                    });
            })
            .catch(function(err){
                electionKey = null;
                document.getElementById('electionKey_display').innerHTML = "<p>Failed to import key! Error: "+err+"</p>";
                document.getElementById('electionKey').value = "";
            });
    }

    /**
     * create the ballot using the imported keys -> {public_key||time||candidate}||signature
     */
    function createBallot()
    {
        /*
         * export the signing key as JWK so as to be able to access the key's public modulus
         * (to be used when constructing the ballot contents)
         */
        window.crypto.subtle.exportKey("jwk", signingKey)
            .then(function(privateKey){

                /*
                 * create the ballot contents
                 */
                // convert encoding of the modulus from url to plain
                var b64 = privateKey.n.replace(/-/g,"+").replace(/_/g,"/");
                b64 = b64.padRight(b64.length + (4 - b64.length % 4) % 4, '='); // fix padding

                // public key modulus to byte array
                var modulus = Uint8Array.from(window.atob(b64), function(c) {return c.charCodeAt(0)}); // decode to raw bytes
                if (modulus.length < 256){  // pad the modulus with zeros if necessary
                    var pad = [];
                    for (var i=0; i < 256-modulus.length; i++) pad.push(0);
                    // construct a new typed array
                    var new_modulus = new Uint8Array(256);
                    new_modulus.set(Uint8Array.from(pad));
                    new_modulus.set(modulus, pad.length);
                    modulus = new_modulus
                }

                // create 64bit timestamp (Unix Epoch in Seconds)
                var time = Math.floor(Date.now() / 1000);
                var timestamp = function(long){
                    var byteArray = [0, 0, 0, 0, 0, 0, 0, 0];
                    for (var index = 0; index < byteArray.length; index++){
                        var byte = long&(0xff);
                        byteArray[index] = byte;
                        long = (long-byte)/256;
                    }
                    return byteArray;
                }(time);
                timestamp = new Uint8Array(timestamp);
                console.log("uint8: "+timestamp);

                // convert candidate string to raw byte array
                var candidate = Uint8Array.from(document.getElementById('candidate').value, function(c) {return c.charCodeAt(0)});
                document.getElementById('candidate').value = ""; // clear input

                // create full ballot contents to be encrypted
                var data = new Uint8Array(modulus.length + timestamp.length + candidate.length);
                data.set(modulus);
                data.set(timestamp, modulus.length);
                data.set(candidate, modulus.length + timestamp.length);

                /*
                 * encrypt the ballot contents
                 */
                window.crypto.subtle.encrypt(
                    {
                        name: "RSA-OAEP"
                    },
                    electionKey, //from generateKey or importKey above
                    data //ArrayBuffer of data you want to encrypt
                )
                    .then(function(encrypted){
                        /*
                         * sign the encrypted ballot
                         */
                        window.crypto.subtle.sign(
                            {
                                name: "RSASSA-PKCS1-v1_5"
                            },
                            signingKey,
                            encrypted
                        )
                            .then(function(signature){

                                // convert to Uint8 arrays
                                encrypted = new Uint8Array(encrypted);
                                signature = new Uint8Array(signature);

                                /* append signature to encrypted ballot to form the signed ballot */
                                var ballot = new Uint8Array(encrypted.length + signature.length);
                                ballot.set(encrypted);
                                ballot.set(signature, encrypted.length);

                                /* encode to base64 and fill form entry */
                                var str = btoa(String.fromCharCode.apply(null, ballot)); // as base64 encoded str
                                document.getElementById('ballot_display').innerHTML =
                                    "<p style=\"overflow-wrap:break-word;\"><code>"+str+"</code></p>"; // display encrypted ballot
                                document.getElementById('ballot').value = str; // fill form
                            })
                            .catch(function(err){
                                document.getElementById('ballot_display').innerHTML = "<p>Error signing ballot: "+err+"</p>";
                            });
                    })
                    .catch(function(err){
                        document.getElementById('ballot_display').innerHTML = "<p>Error encrypting ballot: "+err+"</p>";
                    });
            })
            .catch(function(err){
                document.getElementById('ballot_display').innerHTML = "<p>Error exporting to JWK: "+err+"</p>";
            });
    }
    //-->
</script>

