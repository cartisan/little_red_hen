// Agent hen in project little_red_hen

/*********** General beliefs  ***********/
is_work(plant(_)).
is_work(tend(_)).
is_work(harvest(_)).
is_work(grind(_)).
is_work(bake(_)).

is_pleasant(eat(bread)).

animals([dog, cow, pig]).

/*********** Self-specifications  ***********/
indignation(0).

is_communal(self).

!make_great_again(farm).

/*********** Initial rules ***********/
/* Emotion management 				 */	
+indignation(X) : X > 10 <-
	+is_angry(self)[reason(indignation)].

+indignation(0) : is_angry(self)[reason(indignation)] <-
	-is_angry(self)[reason(indignation)].

+is_angry(self) <-
	.print("I am angry!");
	.remove_plan(helpfullness).

-is_angry(self) <-
	.print("I am not angry anymore!");
	.add_plan(helpfullness).

// TODO: Is it a good idea to remove this
//belief after processing it into indignation?
//or rather keep it as memory?
+rejected_help_request(Req) <-
	?indignation(X);
	-rejected_help_request(Req);
	-+indignation(X+1).
	
/* Goal management 				 */
+has(wheat(seed)) <- 
	.suspend(make_great_again(farm));
	!create_bread.

// TODO: How to make Java side find sender on itself? 
@helpfullness
+has(X) : is_communal(self) & is_pleasant(eat(X)) <-
	.print("Sharing: ", X, " with the others");
	.my_name(MyName);
	?animals(Anims);
	!share(MyName, X, Anims);
	!eat(X).
	
+has(X) : is_pleasant(eat(X)) <-
	!eat(X).
	
/*********** Plans ***********/

// TODO: Wait for response before do yourself?
@communality
+!X[_] : is_communal(self) & is_work(X) & not asked(X) <-
	.print("Asking farm animals to help with ", X)
	?animals(A);
	.send(A, achieve, help_with(X));
	+asked(X);
	!X;
	-asked(X).

@vindication	
+!X[_] : is_angry(self) & is_pleasant(X) & not asked(X) <-
	.print("Offering farm animals to: ", X);
	?animals(A);
	.send(A, achieve, X);
	.print("But not shareing necessary ressources.");
	+asked(X);
	!X;
	-asked(X).
	

+!make_great_again(farm) : true	<- 
	!randomFarming;
	!make_great_again(farm).
	
+!create_bread : has(wheat(seed)) <- 	
	!plant(wheat);
	!tend(wheat);
	!harvest(wheat);
	!grind(wheat);
	!bake(bread).   


// action-execution goals
+!randomFarming <-
	.my_name(MyName);
	randomFarming(MyName).

+!plant(wheat) <-
	.my_name(MyName);
	plant(MyName, wheat).
	
+!tend(wheat) <-
	tend(wheat).
	
+!harvest(wheat) <-
	harvest(wheat).

+!grind(wheat) <-
	grind(wheat).

+!bake(bread) <-
	.my_name(MyName);
	bake(MyName, bread).
	
+!eat(X) : has(X) <- 
	.my_name(Name);
	eat(Name, X).
	
+!share(MyName, X, [H|T]) : .length(T) > 0 <-
	share(MyName, X, H);
	!share(MyName, X, T).	
	
+!share(MyName, X, [H|T]) : .length(T) == 0 <-
	share(MyName, X, H).	
	
+!share(MyName, X, Anim) <-
	share(MyName, X, Anim).	