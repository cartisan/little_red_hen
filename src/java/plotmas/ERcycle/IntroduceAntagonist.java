package plotmas.ERcycle;

import jason.asSemantics.Personality;
import plotmas.LauncherAgent;
import plotmas.ERcycle.PlotCycle.EngageResult;
import plotmas.stories.little_red_hen.RedHenHappeningCycle;

public class IntroduceAntagonist implements ProblemFixCommand {

	private LauncherAgent antagonist;
	private RedHenHappeningCycle controller;
	
	public IntroduceAntagonist(RedHenHappeningCycle controller) {
		this.controller = controller;
		
		// An antagonist is a very, very bad person ;)
		this.antagonist = new LauncherAgent("antagonist" + this.controller.charCount,
				 new Personality(0, -1, 0, -1, 1));		
	}
	
	@Override
	public void execute(EngageResult er) {
		// Create Agent with low agreeableness
		er.getLastAgents().add(this.antagonist);
		this.controller.updateCharCount(1);		// notify that we added one char
	}

	@Override
	public void undo(EngageResult er) {
		er.getLastAgents().remove(this.antagonist);
		this.controller.updateCharCount(-1);		// notify that we removed one char
	}

	@Override
	public String message() {
		return "Introducing an antagonist";
	}

}
