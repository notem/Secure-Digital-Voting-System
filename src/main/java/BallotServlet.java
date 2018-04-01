import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

import static java.lang.Math.abs;

@WebServlet(urlPatterns = { "/ballot"})
public class BallotServlet extends HttpServlet
{
    private static KeyPair keys = CryptoUtils.generateKeys(); // TODO: temporary for testing until functionality for retrieving real election keys exist
    public BallotServlet()
    {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        request.setAttribute("ballotActive", "");

        String encodedPub = CryptoUtils.exportKey(keys.getPublic());
        request.setAttribute("encodedPublicKey", encodedPub);

        /* forward the request onto the jsp compiler */
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/pages/submitBallot.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        boolean err = false;
        request.setAttribute("error", "");

        /* accept the ballot */
        String data = request.getParameter("ballot");
        if (data == null || data.isEmpty())
        {
            request.setAttribute("error", "No ballot was sent to the server!");
            err = true;
        }

        /* parse the encrypted data into a decrypted & verified ballot object */
        PrivateKey decryptionKey = keys.getPrivate(); // TODO: grab election's private key before decrypting the ballot
        DecryptedBallot ballot = null;
        if (!err)
        {
            try /* DecryptedBallot constructor will through illegal argument errors if ballot is invalid */
            {
                ballot = new DecryptedBallot(data, decryptionKey);
                System.out.println(ballot.toString());
            }
            catch (IllegalArgumentException e)
            {
                request.setAttribute("error", e.getMessage());
                err = true;
            }
        }


        /* verify that the ballot's modulus (key) is registered */
        if (!err)
        {
            Boolean registered = DatabaseUtils.getVoterPublicKeys().contains(ballot.modulus);
            if (!registered)
            {
                request.setAttribute("error", "Ballot contained an unknown voter key!");
                err = true;
            }
        }

        /* verify that timestamp is reasonable */
        if (!err)
        {
            long dif = abs(ballot.timestamp - Instant.now().getEpochSecond());
            if (dif > 5*60)
            {
                request.setAttribute("error", "Ballot timestamp is expired!");
                err = true;
            }
        }

        /* verify candidate choice is valid */
        if (!err)
        {
            // TODO: verify that the candidate's choice is an option?
        }

        /* send the encrypted ballot to be added to the block-chain */
        if (!err)
        {
            // TODO: read public key from request or database
        	PublicKey encryptionKey = keys.getPublic();
        	String pk = CryptoUtils.exportKey(encryptionKey);
        	
        	boolean a,b,c;
        	
        	//TODO: Testing, remove when interfaces with legit elections are created
        	if(DatabaseUtils.retrievePrivateKey(pk) == null)
        	{
        		a = DatabaseUtils.createElection("SubmitTest" + String.format("%4f", 1000 * Math.random()), keys);
        		b = DatabaseUtils.initializeElectionBlockchain(pk);
        	}
        	else
        	{
        		a = true;
        		b = true;
        	}
        	
            c = DatabaseUtils.addToBlockchain(data, pk);
            
            if (!c){
            	request.setAttribute("error", "Failed to add ballot to election blockchain!");
            	err = true;
            }
            else if (!a && !b && c){
            	request.setAttribute("error", "Failed to initialize new blockchain. Does this blockchain already exist?");
            }
        }

        /* refresh the page */
        doGet(request, response);
    }

    /**
     */
    public class DecryptedBallot
    {
        String encodedBallot; // full ballot (base64 encoded)
        byte[] encrypted;     // 512 bytes (4096 bits)
        byte[] signature;     // 256 bytes (2048 bits)
        byte[] decrypted;     // variable length, 265-512 bytes

        public String modulus;         // voter's public modulus (key)
        public PublicKey verifyingKey; // key constructed from the recovered modulus
        public long timestamp;         // unix epoch time (seconds)
        public String candidate;       // candidate name

        /**
         * A simple Java object to abstract the process of decrypting and interpreting an encrypted ballot
         * @param b64 the base64 encoded ballot
         * @param decryptionKey the election's private key
         * @throws IllegalArgumentException
         */
        public DecryptedBallot(String b64, PrivateKey decryptionKey) throws IllegalArgumentException
        {
            encodedBallot = b64;
            byte[] out;
            try /* decode the base64 encoded data */
            {
                out = Base64.getDecoder().decode(b64);
            }
            catch(Exception e)
            {
                throw new IllegalArgumentException("Ballot could not be decoded from base64!");
            }

            /* split the ballot into encrypted bytes and signature bytes*/
            if (out.length != (512 + 256) /* length of (enc||sig) */)
            {
                throw new IllegalArgumentException("Ballot is not correct length!");
            }
            encrypted = Arrays.copyOfRange(out, 0, 512);
            signature = Arrays.copyOfRange(out, 512, out.length);

            /* retrieve the election's private key & decrypt the ballot */
            String plaintext = CryptoUtils.decryptData(Base64.getEncoder().encodeToString(encrypted), decryptionKey);
            if (plaintext == null)
            {
                throw new IllegalArgumentException("Ballot failed to decrypt!");
            }
            else
            {
                decrypted = Base64.getDecoder().decode(plaintext);
                if (decrypted.length < 265)
                {
                    throw new IllegalArgumentException("Ballot candidate are not of a correct length!");
                }
                else
                {   // voter's public key modulus (bytes 0-256)
                    modulus = Base64.getEncoder().encodeToString(Arrays.copyOfRange(decrypted, 0, 256));

                    // unix epoch timestamp (bytes 256-264)
                    byte[] bytes = Arrays.copyOfRange(decrypted, 256, 264);
                    for (int i=7; i>=0; i--)
                    {
                        timestamp = (timestamp*256) + ((int)bytes[i] < 0 ? 256+(int)bytes[i] : (int)bytes[i]);
                    }

                    // voter's candidate choice (bytes 264~512)
                    candidate = new String(Arrays.copyOfRange(decrypted, 264, decrypted.length)); // everything else
                }
            }

            /* verify the signature on the ballot */
            verifyingKey = CryptoUtils.createPublicKey(modulus);
            boolean verified = CryptoUtils.verifySignature(Base64.getEncoder().encodeToString(encrypted),
                    Base64.getEncoder().encodeToString(signature), verifyingKey);
            if (!verified)
            {
                throw new IllegalArgumentException("Ballot has an invalid signature!");
            }
        }

        @Override
        public String toString()
        {
            return "("+candidate+") @ [" + Instant.ofEpochSecond(timestamp).toString() + "] by " + modulus;
        }
    }
}
