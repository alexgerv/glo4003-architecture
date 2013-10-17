package ca.ulaval.glo4003.web.converters;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import ca.ulaval.glo4003.model.Match;
import ca.ulaval.glo4003.web.viewmodels.MatchViewModel;

public class MatchViewConverter {
	
	public Collection<MatchViewModel> convert(Map<String, Match> entries) {
		Collection<MatchViewModel> viewModels = new LinkedList<MatchViewModel>();
		for (Entry<String, Match> entry : entries.entrySet()) {
			MatchViewModel viewModel = convert(entry.getValue());
			viewModel.matchIdentifier = entry.getKey();
			viewModels.add(viewModel);
		}
		return viewModels;
	}
	
	public MatchViewModel convert(Match entry) {
		MatchViewModel viewModel = new MatchViewModel();
		
	    viewModel.sport = entry.getSport();
	    viewModel.venue = entry.getVenue();
	    viewModel.date = entry.getDate();
		viewModel.homeTeam = entry.getHomeTeam();
		viewModel.visitorTeam = entry.getVisitorTeam();
		viewModel.sex = entry.getSex();
		viewModel.availableTicketsBySection = entry.getAvailableTicketsBySection();
		viewModel.totalNumberOfAvailableTickets = entry.getTotatNumberOfAvailableTickets();
		
		return viewModel;
	}
}
