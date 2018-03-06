import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = { "/voters"})
public class VotersServlet extends HttpServlet
{
    public VotersServlet()
    {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        request.setAttribute("votersActive", "true");

        /* grab voter information and set attribs*/
        List<String> voters = DatabaseUtils.getVoters();   // voter names from database
        List<String> keys = DatabaseUtils.getPublicKeys(); // voter names from database
        request.setAttribute("voters", voters); // add list to request
        request.setAttribute("keys", keys);     // add list to request

        /* forward the request onto the jsp compiler */
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/pages/voters.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        doGet(request, response);
    }
}
