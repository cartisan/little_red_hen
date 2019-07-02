package inBloom.test.helper;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import inBloom.ERcycle.AdaptPersonality.OCEANConstraints;
import inBloom.helper.TermParser;
import jason.asSyntax.Literal;
import jason.util.Pair;
import junit.framework.TestCase;

public class TermParserTest extends TestCase {
	
	public void testExtractPersonalityAnnotation() {
		String annotation = "affect(personality(extraversion,positive))";
		List<?> results = TermParser.extractPersonalityAnnotation(annotation);
		List<Pair<String, String>> expected = new LinkedList<>();
		expected.add(new Pair<String, String>("extraversion","positive"));
		
		assertEquals(expected, results);
		
		annotation = "affect(or(and(personality(extraversion,positive),not (mood(dominance,low))),personality(openness,low))),source(self)";
		results = TermParser.extractPersonalityAnnotation(annotation);
		expected = new LinkedList<>();
		expected.add(new Pair<String, String>("extraversion","positive"));
		expected.add(new Pair<String, String>("openness","low"));
		
		assertEquals(expected, results);
	}

	public void testSolutionsForPersonalityAnnotation() {
		Literal annotation = Literal.parseLiteral("affect(personality(extraversion,positive))");
		Set<OCEANConstraints> results = TermParser.solutionsForPersonalityAnnotation(annotation);
		
		assertEquals(2, results.size());
		assertEquals((Integer) 10, new LinkedList<OCEANConstraints>(results).get(1).getTrait("extraversion"));
		assertEquals((Integer) 3, new LinkedList<OCEANConstraints>(results).get(0).getTrait("extraversion"));
		
		annotation = Literal.parseLiteral("affect(or(personality(extraversion,high),personality(openness,low))),source(self)");
		results = TermParser.solutionsForPersonalityAnnotation(annotation);
		assertEquals(7, results.size());
				
		annotation = Literal.parseLiteral("affect(or(personality(extraversion,positive),personality(openness,low))),source(self)");
		results = TermParser.solutionsForPersonalityAnnotation(annotation);
		assertEquals(10, results.size());
		
		// Mood doesn't play a role in personality annotations
		annotation = Literal.parseLiteral("affect(or(and(personality(extraversion,positive),not (mood(dominance,low))),personality(openness,low))),source(self)");
		Set<OCEANConstraints> results2 = TermParser.solutionsForPersonalityAnnotation(annotation);
		assertEquals(ImmutableSet.copyOf(results), ImmutableSet.copyOf(results2));
	}
	
	public void testRemoveAnnots() {
		String noAnnot = "farmwork";
		assertEquals("farmwork", TermParser.removeAnnots(noAnnot));
		
		String atom = "farmwork[source(self)]";
		assertEquals("farmwork", TermParser.removeAnnots(atom));
		
		String multi = "farmwork[source(self),cause(life)]";
		assertEquals("farmwork", TermParser.removeAnnots(multi));
		
		String embedd = "farmwork[source(self),cause(life[location(universe)])]";
		assertEquals("farmwork", TermParser.removeAnnots(embedd));
		
		String predicate = "plant(wheat)[source(self),cause(life[location(universe)])]";
		assertEquals("plant(wheat)", TermParser.removeAnnots(predicate));
		
		String predicate_embedd = "plant(wheat[state(great)])[source(self),cause(life[location(universe)])]";
		assertEquals("plant(wheat[state(great)])", TermParser.removeAnnots(predicate_embedd));
		
		String rec_embedd = "at(loc(tree)[level(top)])[source(self),cause(life[location(universe)])]";
		assertEquals("at(loc(tree)[level(top)])", TermParser.removeAnnots(rec_embedd));
	}
	
	
	public void testGetAnnots() {
		String noAnnot = "farmwork";
		assertEquals("", TermParser.getAnnots(noAnnot));
		
		String atom = "farmwork[source(self)]";
		assertEquals("[source(self)]", TermParser.getAnnots(atom));
		
		String multi = "farmwork[source(self),cause(life)]";
		assertEquals("[source(self),cause(life)]", TermParser.getAnnots(multi));
		
		String embedd = "farmwork[source(self), cause(life[location(universe)])]";
		assertEquals("[source(self), cause(life[location(universe)])]", TermParser.getAnnots(embedd));
		
		String predicate = "plant(wheat)[source(self),cause(life[location(universe)])]";
		assertEquals("[source(self),cause(life[location(universe)])]", TermParser.getAnnots(predicate));
		
		String predicate_embedd = "plant(wheat[state(great)])[source(self),cause(life[location(universe)])]";
		assertEquals("[source(self),cause(life[location(universe)])]", TermParser.getAnnots(predicate_embedd));
		
		String rec_embedd = "at(loc(tree)[level(top)])[source(self),cause(life[location(universe)])]";
		assertEquals("[source(self),cause(life[location(universe)])]", TermParser.getAnnots(rec_embedd));
		
		String rec_embedd_no_brack = "at(loc(tree)[level(top)])[source(self),cause(life[location(universe)])]";
		assertEquals("source(self),cause(life[location(universe)])", TermParser.getAnnots(rec_embedd_no_brack, true));
	}
}
