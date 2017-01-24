package little_red_hen;

import java.util.HashMap;
import java.util.logging.Logger;

/* Model class for Farm Environment */

public class FarmModel {
	private int actionCount;

	public Wheat wheat;
	public HashMap<String, Agent> agents;
	public static enum WHEAT_STATE {SEED, GROWING, RIPE, HARVESTED, FLOUR;}
	
    static Logger logger = Logger.getLogger(FarmModel.class.getName());
	
	public FarmModel(HashMap<String, Agent> agents) {
		this.actionCount = 0;
		this.agents = agents;
		this.wheat = null;
	}
	
	Agent getAgent(String name) {
		return this.agents.get(name);
	}
	
	boolean randomFarming(Agent agent) {
		this.actionCount += 1;
		logger.info("Some farming activity was performed");
		
		if (this.actionCount == 3) {
			this.wheat = new Wheat();
			
			agent.addToInventory(this.wheat);
			
			logger.info("LOOK, " +
						agent.name + 
						"! There are some wheat grains on the floor.");
		}
		
		return true;
	}
	
	boolean plantWheat(Agent agent) {
		Wheat wheatItem = (Wheat) agent.get(Wheat.class);
		if (!(wheatItem == null)) {
				if (wheatItem.state == WHEAT_STATE.SEED) {
					this.wheat.state = WHEAT_STATE.GROWING;
					logger.info("Wheat planted");
					return true;
				}
		}
		
		return false;
	}
	
	boolean tendWheat() {
		if ((this.wheat.state == WHEAT_STATE.GROWING)){
			this.wheat.state = WHEAT_STATE.RIPE;
			logger.info("Wheat has grown and is ripe now");
			return true;
		}
		
		return false;
	}
	
	boolean harvestWheat() {
		if ((this.wheat.state == WHEAT_STATE.RIPE)){
			this.wheat.state = WHEAT_STATE.HARVESTED;
			logger.info("Wheat was harvested");
			return true;
		}
		
		return false;
	}
	
	boolean grindWheat() {
		if ((this.wheat.state == WHEAT_STATE.HARVESTED)){
			this.wheat.state = WHEAT_STATE.FLOUR;
			logger.info("Wheat was ground to flour");
			this.wheat = null;
			return true;
		}
		return false;
	}

	boolean bakeBread(Agent agent) {
		Wheat wheatItem = (Wheat) agent.get(Wheat.class);
		if((!(wheatItem == null)) & (wheatItem.state == WHEAT_STATE.FLOUR)) {
			agent.addToInventory(new Bread());
			agent.removeFromInventory(wheatItem);
			
			logger.info(agent.name + ": baked some bread.");
			return true;
		}
		
		return false;
	}
	

	
	/****** helper classes *******/
	
	class Wheat implements Item {
		public WHEAT_STATE state = WHEAT_STATE.SEED;
		
		public String literal() {
			if (state == WHEAT_STATE.SEED) {
				return "wheat(seed)";
			}
			
			if (state == WHEAT_STATE.GROWING) {
				return "wheat(growing)";
			}

			if (state == WHEAT_STATE.RIPE) {
				return "wheat(ripe)";
			}
			
			if (state == WHEAT_STATE.HARVESTED) {
				return "wheat(harvested)";
			}
			
			if (state == WHEAT_STATE.FLOUR) {
				return "wheat(flour)";
			}
		
			return null;
		}
		
		public boolean isEdible() {
			return false;
		}
	}
	
	class Bread implements Item {
		public String literal() {
			return "bread";
		}
		
		public boolean isEdible() {
			return true;
		}
		
	}
	
}
