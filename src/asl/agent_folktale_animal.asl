/******************************************************************************/
/************ knowledge base  *************************************************/
/******************************************************************************/

{include("agent-knowledge_base.asl")}
	
/********************************************/
/*****     wishes and obligations ***********/
/********************************************/

{include("agent-desire_wish_management.asl")}

wish(relax).
+self(farm_animal) <- +obligation(farm_work).

/******************************************************************************/
/********** perception management *********************************************/
/******************************************************************************/

// Share when very agreeable character, unless in a bad mood
@share_food_plan2[atomic, affect(and(personality(agreeableness,high), not(mood(pleasure,low))))]
+has(X) : hungry & is_pleasant(eat(X)) & has(X) <- 			// still has X when event selected
	-wish(has(X));
	.appraise_emotion(joy, "has(X)");
	?present(Anims);
	+wish(share_food(X, Anims));
	.resume(wish(relax)).

+has(X) : hungry & is_pleasant(eat(X)) & has(X)  <-			// still has X when event selected 
	-wish(has(X));
	.appraise_emotion(joy, "has(X)");
	+wish(eat(X));
	.resume(wish(relax)).



+see(Thing)[location(Loc), owner(Per)] : is_useful(Thing) <-   // crowfox
	+at(Per,Loc);
	+has(Per,Thing);
	.appraise_emotion(hope, "see(Thing)");
	.suspend(wish(relax));
	+wish(has(Thing)).
	
+see(Thing)[owner(Per)] : is_useful(Thing) <-   // crowfox
	+has(Per,Thing);
	.my_name(Name);
	.appraise_emotion(hope, "see(Thing)");
	.suspend(wish(relax));
	+wish(has(Thing)).

//TODO: how to make creatable_from(X,Y) recursive?
+found(X) : creatable_from(X,Y) & is_useful(Y) <-
	.suspend(obligation(farm_work));	// only for brevity of graph
	.suspend(wish(relax));
	+obligation(create(Y)).

+is_dropped(Thing)[owner(Agent)] : .my_name(Agent) <-		//crowfox
	.appraise_emotion(remorse, "has(Thing)", Agent, true).


@is_dropped[atomic]
+is_dropped(Thing)[owner(Agent)] : wish(has(Thing)) <-		//crowfox
	.appraise_emotion(gloating, "is_dropped(Thing)", Agent);
	!collect(Thing);
	-wish(has(X));
	.resume(wish(relax)).

@compliment[atomic]
+complimented : .my_name(Agent)  <-  		//crowfox
	.appraise_emotion(pride, "complimented", Agent);
	!sing.

+threatened(Item)[source(Other)] <- 				 //crowfox
	.print("Oh no, don't hurt me!");
	.appraise_emotion(fear, "threatened(Item)");
	!handOver(Item, Other).
	
@threat_2[affect(personality(conscientiousness,low))]
+threatened(Item)[source(Other)]  : .my_name(Me) <- 				 //crowfox
	.print("No, I will not give you anything!");
	.appraise_emotion(reproach, "threatened(Item)", Other);
	!refuseHandOver(Item, Other).

+refuseHandOver(Thing, Agent) <-
	// TODO: continue story by an attack mechanism?
	.resume(wish(relax)).
		
/***** request answer management **********************************************/
/******************************************************************************/

+request(help_with(Helpee, Plan)) <-
	+obligation(help_with(Helpee, Plan)).

+rejected_request(help_with(Helpee,Req))[source(Name)] <-
	.appraise_emotion(anger, "rejected_request(help_with(Helpee,Req))", Name, false);
	.abolish(rejected_request(help_with(Helpee,Req)));
	-asking(help_with(Req), Name).
	
+accepted_request(help_with(Helpee,Req))[source(Name)] <-
	.appraise_emotion(gratitude, "accepted_request(help_with(Req))[source(Name)]", Name, false);
	.abolish(accepted_request(help_with(Helpee,Req)));
	-asking(help_with(Req), Name).

@reject_request[atomic]
+!reject(Helpee, Plan) <-
	.appraise_emotion(reproach, "request(Plan)", Helpee);
	.print("can't help you! request(", Plan, ") is too much work for me!");
	.send(Helpee, tell, rejected_request(Plan));
	-obligation(Plan).
	
// TODO: How to turn this into an analogous accept without breaking FU structure?	
@accept_request[atomic]
+!help_with(Helpee, Plan) <-
	.print("I'll help you with ", Plan, ", ", Helpee);
	.send(Helpee, tell, accepted_request(Plan));
	help(Helpee);
	.appraise_emotion(happy_for, "request(Plan)", Helpee);
	-obligation(Plan).

/****** Mood  management ******************************************************/
/******************************************************************************/

+mood(hostile) <-
	?affect_target(Anims);
	if (.empty(Anims)) {
		.print("What a foul mood, and no-one to blame for it!");
	} else {
		+wish(punish);
	}.

-mood(hostile) <-
	-wish(punish).
	
/******************************************************************************/
/***** Plans  *****************************************************************/
/******************************************************************************/

// Ask for help if extraverted, unless one feels powerless
@general_help_acquisition_plan[affect(and(personality(extraversion,positive),not(mood(dominance,low))))]
+!X[_] : is_work(X) & not complex_plan(X) & not already_asked(X) <-
	.my_name(Me);
	?present(Animals);
	+already_asked(X);
	for (.member(Animal, Animals)) {
		.print("Asking ", Animal, " to help with ", X)
		.send(Animal, tell, request(help_with(Me,X)));
		+asking(help_with(X), Animal);
	}
	.wait(not asking(help_with(X), _), 1000);
	!X;
	-already_asked(X).

@create_bread_1[affect(personality(conscientiousness,high))]
+!create(bread) : has(wheat[state(seed)]) <-
	!plant(wheat).

@create_bread_2[affect(personality(conscientiousness,high))]
+!create(bread) : at(wheat[state(growing)], farm) <-
	!tend(wheat).

@create_bread_3[affect(personality(conscientiousness,high))]
+!create(bread) : at(wheat[state(ripe)], farm) <-
	!harvest(wheat).

@create_bread_4[affect(personality(conscientiousness,high))]
+!create(bread) : has(wheat[state(harvested)]) <-
	!grind(wheat).

@create_bread_5[affect(personality(conscientiousness,high))]
+!create(bread) : has(wheat[state(flour)]) <-
	!bake(bread);
	.resume(obligation(farm_work));
	.resume(wish(relax));
	-obligation(create(bread)).

+!has(Thing) : has(Agent, Thing) & at(Agent, Loc1) & at(Loc2) & not Loc1==Loc2 <- 	//crowfox
	!approach(Agent).

+!has(Thing) : has(Agent, Thing) & at(Agent, Loc1) & at(Loc2)  & Loc1==Loc2 <- 	//crowfox
	!get(Thing, Agent).

@punish_1[atomic]	
+!punish : mood(hostile) & has(X) & is_pleasant(eat(X)) & hungry <-
//+!punish : mood(hostile) & has(X) & is_pleasant(eat(X)) & not(hungry(false)) <-
	?affect_target(Anims);
	if (.empty(Anims)) {
		true;
	} else {
		.send(Anims, achieve, eat(X));
		.print("Asked ", Anims, " to eat ", X, ". But not shareing necessary ressources. xoxo");
	};
	!eat(X);
	-wish(punish).

+!share_food(Food, Others) <-
	!share(Food, Others);
	!eat(Food);
	-wish(share_food(X, Anims)).
	
/******************************************************************************/
/*****      Action Execution Goals ********************************************/
/******************************************************************************/
+!relax <-
	relax.
	
+!farm_work <-
	farm_work.

+!plant(wheat) <-
	plant(wheat).
	
+!tend(wheat) <-
	tend(wheat).
	
+!harvest(wheat) <-
	harvest(wheat).

+!grind(wheat) <-
	grind(wheat).

+!bake(bread) <-
	bake(bread).

@eat_1[atomic]	
+!eat(X) <- 
	eat(X);
	-hungry;
	-wish(eat(X)).

+!eat(X) : not has(X)<- 
	.appraise_emotion(disappointment, "eat(X)").

+!approach(Agent) : agent(Agent) & at(Agent, Loc) <-  	//crowfox
	goTo(Loc).

+!approach(Loc) : location(Loc) <-  	//crowfox
	goTo(Loc).

@get_threaten[affect(personality(agreeableness,low))]
+!get(Thing, Agent) :	at(Loc)[level(L)] & at(Agent,Loc)[level(L)] <-			//crowfox
	.print("Give ", Thing, " to me or I will take it from you!");
	.send(Agent, tell, threatened(Thing));
	.wait(has(Thing) | refuseHandOver(Thing, Agent)).

@get_flatter[affect(personality(agreeableness,low))]
+!get(Thing, Agent) : at(Loc) & at(Agent,Loc) <-			//crowfox
	.print("So lovely your feathers, so shiny thy beak!");
	.send(Agent, tell, complimented);
	.wait({+is_dropped(Thing)}).
	
+!get(Thing, Agent) : at(Loc) & at(Agent,Loc)  <-
	.print("I am so hungry, would you share your ", Thing," with me please?");
	.my_name(Me);
	.send(Agent, achieve, share_food(Thing, Me));
	.wait(has(Thing)  | refuseHandOver(Thing, Agent)).	

+!collect(Thing) <-
	collect(Thing).

+!sing <-
	sing.

@share1[affect(and(personality(agreeableness,high), not(mood(pleasure,low))))]
+!share(Item, Agent) <-
	.print("Sharing: ", Item, " with", Agent);
	share(Item, Agent).

@share2[affect(personality(agreeableness,low))]
+!share(Item, Agent) <- 
	.print("I'm not sharing with anyone!");
	!refuseHandOver(Item, Agent).

+!handOver(Item, Agent) <-
	handOver(Item, Agent).

+!refuseHandOver(Item, Agent) <-
	refuseHandOver(Item, Agent).