package de.visuflow.ex2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.visuflow.ex2.updated.MainClassUpdated;
import de.visuflow.reporting.EmptyReporter;
import de.visuflow.reporting.IReporter;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Main;
import soot.PackManager;
import soot.Transform;
import soot.jimple.changeset.AnalysisChangeSetFinder;
import soot.jimple.changeset.FlowAbstraction;

public class MainClass {

	public static void main(String[] args) {
		Map<String, Object> cache = new HashMap<String, Object>();
		runAnalysis(new EmptyReporter(), 3, cache);
		
		AnalysisChangeSetFinder finder = new AnalysisChangeSetFinder("C:\\Users\\karth\\git\\visuflow-uitests-analysis-intial\\bin", "C:\\Users\\karth\\git\\visuflow-uitests-analysis-updated\\bin", "de.visuflow.ex2.MainClass");
		Map<String,List<FlowAbstraction>> changeSet = finder.computeChangeSet();
		MainClassUpdated.main(cache,changeSet);
	}

	public static void runAnalysis(final IReporter reporter, final int exercisenumber, Map<String, Object> cache) {
		G.reset();

		// Register the transform
		Transform transform = new Transform("jtp.analysis", new BodyTransformer() {
			@Override
			protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
				IntraproceduralAnalysis ipa = new IntraproceduralAnalysis(b, reporter, cache);
				ipa.doAnalyis(cache);
			}
		});
		PackManager.v().getPack("jtp").add(transform);
		String targetDir = "C:\\Users\\karth\\git\\visuflow-uitests-target\\bin";
		Main.main(new String[] { "-pp", "-process-dir", targetDir, "-w", "-exclude", "javax", "-allow-phantom-refs",
				"-no-bodies-for-excluded", "-src-prec", "class", "-output-format", "none" });
	}

}
