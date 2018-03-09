import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns={"/vdashboard"})
public class VDashboard extends HttpServlet {

    public VDashboard()
    {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        req.setAttribute("vDashboardActive", "true");

        // list of active elections


        // for each election, a list of voters


        // management utilities: view blockchain, close election

        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/pages/vDashboard.jsp");
        dispatcher.forward(req, resp);
    }
}
