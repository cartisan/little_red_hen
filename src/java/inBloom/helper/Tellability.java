package inBloom.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;

import inBloom.framing.ConnectivityGraph;
import inBloom.graph.CountingVisitor;
import inBloom.graph.PlotDirectedSparseGraph;
import inBloom.graph.PlotGraphController;
import inBloom.graph.Vertex;
import inBloom.graph.Vertex.Type;
import inBloom.graph.isomorphism.FunctionalUnit;
import inBloom.graph.isomorphism.FunctionalUnits;
import inBloom.graph.isomorphism.UnitFinder;
import jason.util.Pair;

public class Tellability {
	
	public PlotDirectedSparseGraph graph;
	
	protected static Logger logger = Logger.getLogger(Tellability.class.getName());
	
	// plot preconditions
	public CountingVisitor counter;
	public int productiveConflicts = 0;
	
	// Functional Polyvalence
	public List<FunctionalUnit> plotUnitTypes;
	public int numFunctionalUnits;
	public int numPolyvalentVertices;
	public int numAllVertices;
	public Map<FunctionalUnit, Integer> functionalUnitCount = new HashMap<>();
	public ConnectivityGraph connectivityGraph;
	
	// Semantic Symmetry
	public double symmetry;
	
	
	// Semantic Opposition
	
	// Suspense
	public int suspense;
	public int plotLength;

	
	// Dynamic Points

	
	
	/**
	 * Takes an analyzed graph and computes all necessary statistics of the plot to compute tellability.
	 * @param graph a graph that has been processed by both FullGraphPPVisitor and CompactGraphPPVisitor
	 */
	public Tellability(PlotDirectedSparseGraph graph) {
		counter = new CountingVisitor();
		this.plotUnitTypes = new LinkedList<FunctionalUnit>();
		this.graph = graph; 

		// Find Functional Units and polyvalent Vertices
		detectPolyvalence();

		// TODO: calculate symmetry
		calculateSymmetry();
		
		// Perform quantitative analysis of plot
		computeSimpleStatistics();
	}

	/**
	 * Computes all statistics that can be determined by counting in a single pass
	 * @param graph a graph that has been processed by both FullGraphPPVisitor and CompactGraphPPVisitor
	 */
	private void computeSimpleStatistics() {
		counter.apply(this.graph);
		this.productiveConflicts = counter.getProductiveConflictNumber();
		this.suspense = counter.getSuspense();
		this.plotLength = counter.getPlotLength();
		this.numAllVertices = counter.getVertexNum();
	}

	/**
	 * Identifies all instances of functional units in the plot graph and detects polyvalent vertices
	 * at their overlap.
	 * @param graph a graph that has been processed by both FullGraphPPVisitor and CompactGraphPPVisitor
	 */
	private void detectPolyvalence() {
		Map<Vertex, Integer> vertexUnitCount = new HashMap<>();
		
		UnitFinder finder = new UnitFinder();
		int polyvalentVertices = 0;
		int unitInstances = 0;
		Set<Vertex> polyvalentVertexSet = new HashSet<Vertex>();
		
		connectivityGraph = new ConnectivityGraph(this.graph);
		
		for(FunctionalUnit unit : FunctionalUnits.ALL) {
			Set<Map<Vertex, Vertex>> mappings = finder.findUnits(this.graph, unit.getGraph());
			unitInstances += mappings.size();
			this.functionalUnitCount.put(unit, mappings.size());
			logger.log(Level.INFO, "Found '" + unit.getName() + "' " + mappings.size() + " times.");
			
			if (mappings.size() > 0 ) {
				PlotGraphController.getPlotListener().addDetectedPlotUnitType(unit);
				this.plotUnitTypes.add(unit);
			}
			
			for(Map<Vertex, Vertex> map : mappings) {
				FunctionalUnit.Instance instance = unit.new Instance(this.graph, map.keySet(), unit.getName());
				instance.identifySubject(map);
				connectivityGraph.addVertex(instance);
				
				for(Vertex v : map.keySet()) {
					
					this.graph.markVertexAsUnit(v, unit);
					if(!vertexUnitCount.containsKey(v)) {
						vertexUnitCount.put(v, 1);
					} else {
						int count = vertexUnitCount.get(v);
						count++;
						if(count == 2) {
							polyvalentVertices++;
							polyvalentVertexSet.add(v);
						}
						vertexUnitCount.put(v, count);
					}
				}
			}
		}
		
		for(FunctionalUnit primitiveUnit : FunctionalUnits.PRIMITIVES) {
			Set<Map<Vertex, Vertex>> mappings = finder.findUnits(this.graph, primitiveUnit.getGraph());
			for(Map<Vertex, Vertex> map : mappings) 
			{
				FunctionalUnit.Instance instance = primitiveUnit.new Instance(this.graph, map.keySet(), primitiveUnit.getName());
				connectivityGraph.addVertex(instance);
			}
		}
		
		this.numFunctionalUnits = unitInstances;
		this.numPolyvalentVertices = polyvalentVertices;
	
		// Mark polyvalent vertices with asterisk
		for(Vertex v : polyvalentVertexSet) {
			v.setPolyvalent();
		}
	}

	
	/**
	 * Calculates the story's overall symmetry based on the characters' beliefs, intentions, actions and emotions
	 */
	private void calculateSymmetry()
	{
		// get the actions, emotions, intentions and beliefs of a character
		double[] characterSymmetries = new double[this.graph.getRoots().size()];
		
		// for each character in the story
		for (Vertex root : this.graph.getRoots()) 
		{
			List<String> emotionSequences = new ArrayList<String>();
			List<String> intentionSequences = new ArrayList<String>();
			List<String> beliefSequences = new ArrayList<String>();
			List<String> actionSequences = new ArrayList<String>();
			
			int charCounter = 0;
			// get its story graph
			for(Vertex v : this.graph.getCharSubgraph(root))
			{
				// get the emotions of the character
				if (!v.getEmotions().isEmpty())
				{
					for (String emotion : v.getEmotions())
					{
						emotionSequences.add(emotion);
					}
				}
				
				// get the intentions of character
				if (!v.getIntention().isEmpty())
				{
					intentionSequences.add(v.getIntention());
				}
				
				// get the intentions of character
				if (v.getType() == Type.PERCEPT)
				{
					beliefSequences.add(v.getFunctor());
				}
				
				// get the actions of the character
				if(v.getType() == Type.ACTION)
				{
					actionSequences.add(v.getFunctor());
				}
			}
			
			
			logger.info("Emotions:");
			double emotionSym = sequenceAnalyser(emotionSequences);
			logger.info("Intentions:");
			double intentionSym = sequenceAnalyser(intentionSequences);
			logger.info("Beliefs:");
			double beliefSym = sequenceAnalyser(beliefSequences);
			logger.info("Actions:");
			double actionSym = sequenceAnalyser(actionSequences);
			
			logger.info("Character: " + root.toString());
			
//			characterSymmetries[charCounter] = (emotionSym + intentionSym + beliefSym + actionSym) / 4;
			
//			logger.info("\n"+root.toString() + " Average Symmetry: "+ characterSymmetries[charCounter] +
//					"\nWith:\n" + emotionSym + "(Emotions),\n" + 
//					intentionSym + "(Intentions),\n" + 
//					beliefSym + "(Beliefs),\n" + 
//					actionSym + "(Actions)");
//			charCounter++;
		}
		
		
		this.symmetry = ( Arrays.stream(characterSymmetries).sum() / this.graph.getRoots().size());
		logger.info("Overall symmetry: " + this.symmetry);
	}
	
	/**
	 * Calculates the story's overall symmetry based on the characters' beliefs, intentions, actions and emotions
	 * @param a graph containing the respective character's events (beliefs, intentions, actions, emotions) as a list of strings
	 * @return symmetry for the input graph 
	 */
	private double sequenceAnalyser(List<String> graphSequence)
	{		
		// saves a sequences as key with corresponding values [counter, List of Start Indices]
		Map<List<String>, List<Integer>> sequenceMap = new HashMap<>();

		// loop over the graph and create (sub)sequences
		for (int start = 0; start < graphSequence.size(); start++)
		{
			for (int end = graphSequence.size() - 1; end > start + 1; end--)
			{
				List<String> currentSeq = graphSequence.subList(start, end);
				
				// if sequences already in list, increase the counter
				if (sequenceMap.containsKey(currentSeq))
				{
					List<Integer> newSeq = sequenceMap.get(currentSeq);
					newSeq.add(start);
					
					sequenceMap.put(currentSeq, newSeq);
				}
				else
				{					
					List<Integer> newSeq = new ArrayList<Integer>();
					newSeq.add(start);
					
					sequenceMap.put(currentSeq, newSeq);
				}
			}
		}

		// sort out the sequences that only appear once -> symmetry increases with repetition
//		List<Double> multiplications = new ArrayList<Double>();
		
		int mNeg = 0;
		int mElena = 0;
		int mNormal = 0;
		
		for (Map.Entry<List<String>, List<Integer>> entry : sequenceMap.entrySet()) 
		{
			// if a sequence appears more than once, weight them by their 
			// number of appearance and save the values in a new list
			if (entry.getValue().size() > 1)
			{
//				Integer[] startValues = entry.getValue().toArray(new Integer[0]);
				int sumDNeg = 0;
				int sumElena = 0;
				int sumDNormal = 0;
				for (int i = 1; i < entry.getValue().size(); i++)
				{
					// get distance between prev and current occurrence of sequence
					sumDNeg += entry.getValue().get(i) - (entry.getValue().get(i-1) + entry.getKey().size());
					sumElena += entry.getValue().get(i) - (entry.getValue().get(i-1) + entry.getKey().size()) < 0 ? 1 : -1; 
					sumDNormal += entry.getValue().get(i) - entry.getValue().get(i-1);
				}
				
				mNeg += sumDNeg * entry.getValue().size();
				mElena += sumElena * entry.getValue().size();
				mNormal += sumDNormal * entry.getValue().size();
//				logger.info("Map" + entry.toString());
//				multiplications.add((double)entry.getKey().size() * entry.getValue().size());
			}
		}
		
		logger.info("Negative Ds: " + mNeg + ". Elenas Ds: " + mElena + ". Normal Ds: " + mNormal);
		
		// return the maximum of these weighted sequences as an indicator of the symmetry of the character's state
		return 0;
	}	

	/**
	 * Computes the overall tellability score, by normalizing all features into a range of (0,1) and adding them,
	 * which amounts to assigning each feature equal weight.
	 * @return
	 */
	public double compute() {
		if (productiveConflicts < 1) {
			// need at least one conflict and one attempt at resolution, for this to be a plot
			return 0;
		}
		
		double tellability = (double) this.numPolyvalentVertices / this.numAllVertices + 
							 (double) this.suspense / this.plotLength;
		
		logger.info("Overall tellability: " + tellability);
		return tellability;
	}
}
