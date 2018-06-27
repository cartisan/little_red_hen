package plotmas.little_red_hen;

import com.google.common.collect.ImmutableList;

import jason.JasonException;
import jason.asSemantics.Personality;
import plotmas.PlotLauncher;


/**
 * Responsible for setting up and starting the narrative system of "The Tale of The Little Red Hen".
 * Different personality settings of the agents can be used to explore a space of possible plots.
 * @author Leonid Berov
 */
public class RedHenLauncher extends PlotLauncher {

	public RedHenLauncher() {
		ENV_CLASS = FarmEnvironment.class;
		runner = this;
	}
	
	public static void main(String[] args) throws JasonException {
        logger.info("Starting up from Launcher!");
        ENV_CLASS = FarmEnvironment.class;
        
        runner = new RedHenLauncher();
        
        ImmutableList<LauncherAgent> agents = ImmutableList.of(
        		runner.new LauncherAgent("hen", new Personality(0.291, -0.236, 0.950, -0.132, 0.6)),
        		runner.new LauncherAgent("dog", new Personality(-0.842, -0.113, 0.745, 0.071, 0.105)),
        		runner.new LauncherAgent("cow", new Personality(-0.416, -0.660, -0.686, 0.366, 0.091)),
        		runner.new LauncherAgent("pig", new Personality(-0.021, -0.567, 0.903, 0.389, -0.125)));
       /* ImmutableList<LauncherAgent> agents = ImmutableList.of(
							runner.new LauncherAgent("hen",					  // works with Mood.MAX_DECAY_TIME = 50 and MAX_UPDATE_TIME = 5
//									new Personality(0,  1,  0.7,  0.3, 0.3)    //punishment
									new Personality(0,  1,  0.7,  0.3, -0.8)    //low neurot --> no punishment
//									new Personality(0,  1,  0.7,  0.7, -0.8)    //high aggrea --> sharing
									
//									new Personality(0, -1,  0.7,  0.3, 0.15)    //low consc --> no plot
									
//									new Personality(0,  1, -0.3,   0.3, 0.15)    //low extra --> no help requests, no punishment, no sharing <-- not after update!
//									new Personality(0,  1, -0.3,  0.7, 0.15)    //low extra, high aggrea --> no help requests, no punishment, sharing
							),
							runner.new LauncherAgent("dog",
									new Personality(0, -1, 0, -0.7, -0.8)
//									new Personality(0, 1, 0, -0.7, -0.8)	// doggie helps hen v1
							),
							runner.new LauncherAgent("cow",
									new Personality(0, -1, 0, -0.7, -0.8)
//									new Personality(0, 1, 0, -0.7, -0.8)	// cow helps hen v1
							),
							runner.new LauncherAgent("pig",
									new Personality(0, -1, 0, -0.7, -0.8)
							)
						);
        */
		runner.run(args, agents, "agent");
	}
}
