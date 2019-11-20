// Agent islandAgent in project inBloom

/******************************************************************************/
/************ knowledge base  *************************************************/
/******************************************************************************/
// Here I can store my knowledge base externally if I want to (or if I have a knowledge base at all)
// This rn is the farming knowledge base that obviously isn't that important for our agent, but is here for example reasons

{include("agent-knowledge_base.asl")}
	
/********************************************/
/*****     wishes and obligations ***********/
/********************************************/
// This is some imported code that helps managing wishes and obligations in a new way
// The code is project independent, so I also import and use it

{include("agent-desire_wish_management.asl")}

//wish(seeTheWorld).
//+self(farm_animal) <- +obligation(farm_work).

/******************************************************************************/
/********** perception management *********************************************/
/******************************************************************************/





/* Initial beliefs and rules */


/* Initial goals */

!start.

/* Plans */

@go_on_cruise[affect(personality(openness,high))]
+!start <- goOnCruise.

@go_on_cruise_default
+!start <- stayHome.

@food_plan
+!eat <- if(has(food)) {
			eat;
			-wish(eat);
		} else {
			getFood;
		}.
		
+!heal <- +wish(sleep).
		  //-wish(heal).
		  
+!sleep <- if(has(hut)) {
				sleep;
				// TODO - only if they wish to heal?
				// 1. is it necessary?
				// 2. how do I do knowledge abfrage?
				// find out object of belief:    ?belief(X)
				// find out existence of belief: if(belief)
				-wish(heal);
				-wish(sleep);
				
		   } else {
		   		buildHut;
		   }.
		  
+!complain <- findFriend;
		  -wish(complain).



/* React to new Belifes / Percepts */

+hungry[source(Name)] <- +wish(eat).

+sick[source(Name)] <- +wish(heal);
					   +wish(complain).


// I could also react to percepts triggered by Happening directly:
// +poisoned(food)[source(Name)] <- .print("MY FOOD IS FUCKING DISGUSTING").

+stolen(food)[source(Name)] <- +hate(monkey).

// if f.e. friend is eaten, then agent has no friend anymore :(
+eaten(X)[source(Name)] <- -has(X).

//+has(hut)[source(Name)] <- .print("I HAVE A FREAKING HUT!!!").

+homesick[source(Name)] <- +wish(complain).


// ASL Debug mode -> Run Configurations, duplicate Launcher, add -debug
