package lessons.maze.pledge;

import java.awt.Color;

import jlm.core.model.lesson.ExecutionProgress;
import jlm.core.model.lesson.ExerciseTemplated;
import jlm.core.model.lesson.Lesson;
import jlm.universe.Direction;
import jlm.universe.Entity;
import jlm.universe.World;
import jlm.universe.bugglequest.AbstractBuggle;
import jlm.universe.bugglequest.Buggle;
import jlm.universe.bugglequest.BuggleWorld;
import jlm.universe.bugglequest.exception.AlreadyHaveBaggleException;
import jlm.universe.bugglequest.exception.NoBaggleUnderBuggleException;

public class PledgeMaze extends ExerciseTemplated {

	public PledgeMaze(Lesson lesson) {
		super(lesson);
		tabName = "Escaper";
				
		/* Create initial situation */
		BuggleWorld myWorlds[] = new BuggleWorld[2];
		myWorlds[0] = new BuggleWorld("Labyrinth", 4, 4); 
		loadMap(myWorlds[0],"lessons/maze/pledge/PledgeMaze");
		new Buggle(myWorlds[0], "Thésée", 12, 14, Direction.NORTH, Color.black, Color.lightGray);
		
		myWorlds[1] = new BuggleWorld("Trapception", 4, 4); 
		loadMap(myWorlds[1],"lessons/maze/pledge/PledgeMaze2");
		new Buggle(myWorlds[1], "Trapception", 9, 10, Direction.NORTH, Color.black, Color.lightGray);
		
		setup(myWorlds);
	}

	// to shorten loading time	
	@Override
	protected void computeAnswer(){
		AbstractBuggle b = (AbstractBuggle)answerWorld.get(0).getEntities().get(0);
		b.setPosFromLesson(19, 19);
		b.setDirection(Direction.EAST);
		
		AbstractBuggle b2 = (AbstractBuggle)answerWorld.get(1).getEntities().get(0);
		b2.setPosFromLesson(19, 19);
		b2.setDirection(Direction.EAST);
		
		try {
			b.pickUpBaggle();
			b2.pickUpBaggle();
		} catch (NoBaggleUnderBuggleException e) {
			e.printStackTrace();
		} catch (AlreadyHaveBaggleException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public void check() {
		lastResult = new ExecutionProgress();
		for (World w: this.getWorlds(WorldKind.CURRENT)) 
			for (Entity e:w.getEntities()) {
				lastResult.totalTests++;
				if (!((AbstractBuggle) e).isCarryingBaggle())
					lastResult.details += "Buggle "+e.getName()+" didn't find the baggle";
				else
					lastResult.passedTests++;
			}
	}

}
