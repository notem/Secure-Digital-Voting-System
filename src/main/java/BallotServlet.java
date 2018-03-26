import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
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

        /* split the ballot into encrypted bytes and signature bytes*/
        byte[] encrypted = new byte[512], signature = new byte[32];
        if (!err)
        {
            try
            {
                byte[] out = Base64.getDecoder().decode(data);
                encrypted = Arrays.copyOfRange(out, 0, 512);
                signature = Arrays.copyOfRange(out, 512, out.length);
            }
            catch (Exception e)
            {
                request.setAttribute("error", "Ballot was not Base64 encoded!");
                err = true;
            }
        }

        /* retrieve the election's private key & decrypt the ballot */
        String modulus = null;
        long unixEpoch = 0;
        byte[] contents;
        if (!err)
        {
            PrivateKey decryptionKey = keys.getPrivate(); // TODO: get election private key and decrypt
            String decrypted = CryptoUtils.decryptData(Base64.getEncoder().encodeToString(encrypted), decryptionKey);
            if (decrypted == null)
            {
                request.setAttribute("error", "Ballot was not properly encrypted!");
                err = true;
            }
            else
            {
                byte[] raw = Base64.getDecoder().decode(data);
                if (raw.length < 265)
                {
                    request.setAttribute("error", "Ballot was not properly encrypted!");
                    err = true;
                }
                else
                {   // voter's public key modulus
                    modulus = Base64.getEncoder().encodeToString(Arrays.copyOfRange(raw, 0, 256));

                    // unix epoch timestamp
                    byte[] bytes = Arrays.copyOfRange(raw, 256, 264);
                    System.out.print("\n");
                    for (int i=7; i>=0; i--)
                    {
                        unixEpoch = (unixEpoch*256) + ((int)bytes[i] < 0 ? 256+(int)bytes[i] : (int)bytes[i]);
                    }

                    // voter's candidate choice
                    contents = Arrays.copyOfRange(raw, 264, raw.length);
                }
            }
        }

        /* create the voter's public key & verify registration */
        if (!err)
        {
            Boolean exists = false;
            for (String key : DatabaseUtils.getPublicKeys())
            {
                if (key.equals(modulus))
                {
                    exists = true;
                    break;
                }
            }
            if (!exists)
            {
                request.setAttribute("error", "Ballot contained an unknown voter key!");
                err = true;
            }
            else
            {
                boolean verified = CryptoUtils.verifySignature(Base64.getEncoder().encodeToString(encrypted),
                        Base64.getEncoder().encodeToString(signature), CryptoUtils.createPublicKey(modulus));
                if (!verified)
                {
                    request.setAttribute("error", "Ballot signature was invalid!");
                    err = true;
                }
            }
        }

        /* verify that timestamp is reasonable */
        if (!err)
        {
            long dif = abs(unixEpoch - Instant.now().getEpochSecond());
            if (dif > 5*60)
            {
                request.setAttribute("error", "Ballot timestamp is expired!");
                err = true;
            }
        }

        /* verify candidate choice is valid */
        if (!err)
        {
            // todo
        }

        /* send the encrypted ballot to be added to the block-chain */
        if (!err)
        {
            // todo
        }

        /* refresh the page */
        doGet(request, response);
    }
}
