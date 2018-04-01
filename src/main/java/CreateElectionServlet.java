import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    	String len = request.getParameter("length");
        String startDate = "";
    	String pubKey = request.getParameter("pubKey");

    	if(electionName == null || startDate == null || len == null)
    	{
    		err = true;
    		request.setAttribute("err", "Missing value, cannot create election.");
    	}

    	if(!err)
    	{
    		err = DatabaseUtils.createElection(electionName, startDate, len, null);
    		if(err)
    		{
    			request.setAttribute("err", "Failed to create election.");
    		}
    	}
    	

        /* refresh the page */
        doGet(request, response);
    }
}
