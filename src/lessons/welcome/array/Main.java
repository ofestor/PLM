package lessons.welcome.array;

import jlm.core.model.lesson.Lesson;

public class Main extends Lesson {

	@Override
	protected void loadExercises() {
		addExercise(new IndexOfValueOfArray(this));
		addExercise(new OccurrenceOfValueOfArray(this));
		addExercise(new AverageValueOfArray(this));
		addExercise(new MaxValueOfArray(this));
		addExercise(new IndexOfMaxValueOfArray(this));
		exercisesLoaded = true;
	}
	
}