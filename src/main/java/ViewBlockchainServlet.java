import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.PublicKey;
import java.util.List;

@WebServlet(urlPatterns = { "/view"})
public class ViewBlockchainServlet extends HttpServlet
{
    public ViewBlockchainServlet()
    {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        request.setAttribute("viewActive", "");

        /* forward the request onto the jsp compiler */
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/pages/viewBlockchain.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        /* get the election's public key using the user supplied name */
        String election = request.getParameter("electionName");
        String encodedKey = DatabaseUtils.retrievePublicKey(election);
        if (encodedKey == null)
        {
            request.setAttribute("error", "The requested election does not exist!"); // no error
        }
        else
        {
            request.setAttribute("blocks", DatabaseUtils.viewBlockchain(encodedKey));
            request.setAttribute("error", ""); // no error
        }

        /* refresh the page */
        doGet(request, response);
    }
}
