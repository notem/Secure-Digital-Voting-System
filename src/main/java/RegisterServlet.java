import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/register"})
public class RegisterServlet extends HttpServlet
{
    public RegisterServlet()
    {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        request.setAttribute("registerActive", "");

        /* forward the request onto the jsp compiler */
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/pages/register.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        /* grab registration information */
        String fname    = request.getParameter("firstName");
        String lname    = request.getParameter("lastName");
        String election = request.getParameter("electionName");
        String pub      = request.getParameter("publicKey");
        String sig      = request.getParameter("signature");

        /* do some (very basic) input validation */
        boolean err = false;
        if (fname.isEmpty() || lname.isEmpty() || fname.length() > 40 || lname.length() > 40)
        {
            request.setAttribute("error", "The registration name is invalid!");
            err = true;
        }
        if (pub.isEmpty() || sig.isEmpty())
        {
            request.setAttribute("error", "The registered public key and signature cannot be empty!");
            err = true;
        }

        // verify that the election name exists
        if (!err && DatabaseUtils.retrievePublicKey(election)==null)
        {
            request.setAttribute("error", "There is no election named "+election+"!");
            err = true;
        }

        // verify public key signature
        PublicKey pubKey = CryptoUtils.createPublicKey(pub);
        if (!err && !CryptoUtils.verifySignature(pub, sig, pubKey))
        {
            request.setAttribute("error", "Your public key signature was invalid.");
            err = true;
        }

        if (!err)
        {
            // register the voter's information
            err = !DatabaseUtils.registerVoter(pub, fname, lname, election);
            if (err)
                request.setAttribute("error", "The provided information is invalid!");
            else
                request.setAttribute("error", "");
        }

        /* refresh the page */
        doGet(request, response);
    }
}
