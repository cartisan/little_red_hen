package plotmas.storyworld;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import jason.asSemantics.Emotion;
import plotmas.LauncherAgent;
import plotmas.PlotEnvironment;


/**
 * Responsible for modeling the storyworld. Subclasses should implement methods to handle each action 
 * that is available to ASL agents. Their action requests are relayed to your model by your 
 * {@link plotmas.PlotEnvironment environment subclass}. <br>
 * Your subclass should maintain the current state of all the objects and agents in the story world. This class
 * provides you with domain-independent model functionality. For now this is just a collection of
 * {@link StoryworldAgent agent models}.
 * 
 * 
 * @see plotmas.stories.little_red_hen.FarmModel
 * @author Leonid Berov
 */
public abstract class Model<EType extends PlotEnvironment<?>> {
	static protected Logger logger = Logger.getLogger(Model.class.getName());
	
	public HashMap<String, StoryworldAgent> agents;
	protected EType environment;
	protected List<Happening<Model<EType>>> scheduledHappenings;
	
	public static String addEmotion(String... ems) {
    	String result = "[";
    	for(String em: ems) {
    		if (Emotion.getAllEmotions().contains(em)) {
    			result += Emotion.ANNOTATION_FUNCTOR + "(" + em + "),";
    		}
    		else{
    			logger.warning("Error: Trying to add an invalid emotion to a percept: " + em);
    			throw new RuntimeException("Trying to add an invalid emotion to a percept: " + em);
    		}
    	}
    	
    	// remove comma after last emotion
    	result = result.substring(0, result.length() - 1);
    	result += "]";
    	
    	return result;
    }
	
	public static String addTargetedEmotion(String em, String target) {
    	String result = "[";
		
    	if (Emotion.getAllEmotions().contains(em)) {
			result += Emotion.ANNOTATION_FUNCTOR + "(" + em + "," + target + ")";
		}
		else {
			logger.warning("Error: Trying to add an invalid emotion to a percept: " + em);
			throw new RuntimeException("Trying to add an invalid emotion to a percept: " + em);
		}

		result += "]";
    	
    	return result;
    }
	
	public Model(List<LauncherAgent> agentList, EType env) {
		this.environment = env;
        this.agents = new HashMap<String, StoryworldAgent>();
        this.scheduledHappenings = new LinkedList<>();
        
        // add all instantiated agents to world model
        for (LauncherAgent agentSetup : agentList) {
        	this.addAgent(agentSetup.name);
        }
	}
	
	public StoryworldAgent getAgent(String name) {
		return this.agents.get(name);
	}
	
	public void addAgent(String agName) {
		// set up connections between agents, model and environment
    	StoryworldAgent ag = new StoryworldAgent(agName) ;
    	agents.put(agName, ag);
    	ag.setEnvironment(this.environment);		
	}
	
	public void removeAgent(String agName) {
		this.agents.remove(agName);
	}
	
	public void executeHappenings() {
		for (Happening<Model<EType>> h : this.scheduledHappenings ) {
			if (h.triggered(this)) {
				h.execute(this);
			}
		}
	}
	
	public void scheduleHappening(Happening<Model<EType>> h) {
		this.scheduledHappenings.add(h);
	}

	public Logger getLogger() {
		return logger;
	}

	public EType getEnvironment() {
		return this.environment;
	}
}
