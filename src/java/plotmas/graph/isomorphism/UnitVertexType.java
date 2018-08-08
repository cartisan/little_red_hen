package plotmas.graph.isomorphism;

import jason.asSemantics.Emotion;
import plotmas.graph.Vertex;

enum UnitVertexType {
	NONE, INTENTION, POSITIVE, NEGATIVE, WILDCARD;
	
	static UnitVertexType typeOf(Vertex v) {
		if(!v.getIntention().isEmpty()) {
			return UnitVertexType.INTENTION;
		}
		boolean hasPositive = false;
		boolean hasNegative = false;
		for(String emotion : Emotion.getAllEmotions()) {
			if(v.hasEmotion(emotion)) {
				hasPositive |= Emotion.getEmotion(emotion).getP() > 0;
				hasNegative |= Emotion.getEmotion(emotion).getP() < 0;
			}
		}
		if(hasPositive && hasNegative) {
			return UnitVertexType.WILDCARD;
		}
		if(hasPositive) {
			return UnitVertexType.POSITIVE;
		}
		if(hasNegative) {
			return UnitVertexType.NEGATIVE;
		}
		return UnitVertexType.NONE;
	}
}
