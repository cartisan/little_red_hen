package plotmas;

import java.util.Collection;

import jason.asSemantics.Personality;

/**
 * Helper class used to encapsulate all parameters needed to initialise ASL Agents from java code.
 * This parameters will be used to create a mas2j file required to start a Jason multi agent system. 
 * @author Leonid Berov
 */
public class LauncherAgent {
	public String name;
	public String beliefs;
	public String goals;
	public Personality personality;
	
	public LauncherAgent() {
		this.name = null;
		this.beliefs = "";
		this.goals = "";
		this.personality = null;
	}
	
	public LauncherAgent(String name) {
		this.beliefs = "";
		this.goals = "";
		this.personality = null;
		
		this.name = name;
	}

	public LauncherAgent(String name, Personality personality) {
		this.beliefs = "";
		this.goals = "";
		
		this.name = name;
		this.personality = personality;
	}
	
	public LauncherAgent(String name, Collection<String> beliefs, Collection<String> goals, Personality personality) {
		this.name = name;
		this.beliefs = createLiteralString(beliefs);
		this.goals = createLiteralString(goals);
		this.personality = personality;
	}
	
	/**
	 * Helper function that takes a collection of strings and concatenates them into a list that can be used to 
	 * generate ASL literal lists.
	 */
	private String createLiteralString(Collection<String> literalList) {
		return String.join(",", literalList);
	}
}
