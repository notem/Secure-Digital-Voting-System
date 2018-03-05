import java.io.IOException;

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
        String fname = request.getParameter("firstName");
        String lname = request.getParameter("lastName");
        String pub  = request.getParameter("publicKey");

        /* register the voter's information */
        if (DatabaseUtils.registerVoter(pub, fname, lname))
            request.setAttribute("success", "true");
        else // TODO improve error details
            request.setAttribute("success", "false");

        /* refresh the page */
        doGet(request, response);
    }
}
