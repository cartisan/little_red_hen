package plotmas.graph;

import java.util.UUID;

/**
 * Represents a typed edge in the plot graph.
 * @author Leonid Berov
 */
public class Edge implements Cloneable {
	
	public enum Type { ROOT, TEMPORAL, MOTIVATION, COMMUNICATION, ACTUALIZATION, TERMINATION, EQUIVALENCE }
	
	private String id;
	private Type type;
	
	// This is just used for rendering.
	// Should be seperated into a class
	// responsible for view.
	private int offset;
	
	public Edge() {
		this(Type.TEMPORAL);
	}
	
	public Edge(Type type) {
		this.id = UUID.randomUUID().toString();
		this.type = type;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getOffset() {
		return this.offset;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public Edge clone() {
		return new Edge(this.type);
	}
}
