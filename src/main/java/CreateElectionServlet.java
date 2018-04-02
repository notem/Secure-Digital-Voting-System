import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@WebServlet(urlPatterns = { "/create-election"})
public class CreateElectionServlet extends HttpServlet
{
    public CreateElectionServlet()
    {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        request.setAttribute("createElectionActive", "");

        /* forward the request onto the jsp compiler */
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/pages/createElection.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    	boolean err = false;
    	request.setAttribute("err", "");
    	
    	String electionName = request.getParameter("electionName");
		byte[] pub = DatatypeConverter.parseBase64Binary(request.getParameter("pubKey"));
		byte[] priv = DatatypeConverter.parseBase64Binary(request.getParameter("privKey"));
		try {
			PublicKey pubK = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pub));
			PrivateKey privK = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(priv));
			KeyPair keys = new KeyPair(pubK, privK);


    	if(electionName == null)
    	{
    		err = true;
    		request.setAttribute("err", "Missing election name, cannot create election.");
    	}

    	if(keys == null) {
    		err = true;
			request.setAttribute("err", "Missing Public and Private Keys, cannot create election.");
		}
    	if(!err)
    	{
    		err = DatabaseUtils.createElection(electionName, keys);
    		if(err)
    		{
    			request.setAttribute("err", "Failed to create election.");
    		}
    	}

		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        /* refresh the page */
        doGet(request, response);
    }
}
