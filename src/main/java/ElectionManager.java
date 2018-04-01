import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		
		// list of active elections
		List<String> elections = DatabaseUtils.getElections();
		req.setAttribute("elections", elections);
		
		// for each election, a list of voters
		List<String> voters = DatabaseUtils.getVoters();
		req.setAttribute("voters", voters);
		
		// management utilities: view blockchain, close election
		
		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/pages/electionManager.jsp");
		dispatcher.forward(req, resp);
	}
}
