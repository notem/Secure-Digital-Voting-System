import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet
{
    public DashboardServlet()
    {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        req.setAttribute("dashboardActive", "true");

        List<String> upcomingElections = DatabaseUtils.getUpcomingElections();
        req.setAttribute("upcomingElections", upcomingElections);
        // management utilities: view blockchain, close election

        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/pages/vDashboard.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        //Refresh the page
        doGet(request, response);
    }
}
