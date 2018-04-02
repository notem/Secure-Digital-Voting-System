import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns={"/electionmanager"})
public class ElectionManager extends HttpServlet {
	
	public ElectionManager()
	{
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
		req.setAttribute("electionManagerActive", "true");

		// list of upcoming elections
		List<String> upcomingElections = DatabaseUtils.getUpcomingElections();
		List<String> upcomingNames = DatabaseUtils.getUpcomingNames();
		req.setAttribute("upcomingElections", upcomingElections);
		req.setAttribute("upcomingNames", upcomingNames);
		// list of active elections
		List<String> activeElections = DatabaseUtils.getElections();
		List<String> activeNames = DatabaseUtils.getActiveNames();
		req.setAttribute("activeElections", activeElections);
		req.setAttribute("activeNames", activeNames);
		// list of closed elections
		List<String> closedElections = DatabaseUtils.getClosedElections();
		List<String> closedNames = DatabaseUtils.getClosedNames();
		req.setAttribute("closedElections", closedElections);
		req.setAttribute("closedNames", closedNames);

		// for each election, a list of voters
		List<String> voters = DatabaseUtils.getVoters(null);
		req.setAttribute("voters", voters);
		
		// management utilities: view blockchain, close election
		
		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/pages/electionManager.jsp");
		dispatcher.forward(req, resp);
	}
}
