package de.visuflow.ex2.updated;

import java.util.List;
import java.util.Map;

import de.visuflow.reporting.EmptyReporter;
import de.visuflow.reporting.IReporter;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Main;
import soot.PackManager;
import soot.Transform;
import soot.jimple.changeset.FlowAbstraction;

public class MainClassUpdated {

    public static void main(Map<String,Object> cache, Map<String, List<FlowAbstraction>> changeSet) {
        runAnalysis(new EmptyReporter(), 3,cache,changeSet);
    }

    public static void runAnalysis(final IReporter reporter, final int exercisenumber,Map<String,Object> cache, Map<String, List<FlowAbstraction>> changeSet) {
        G.reset();

        // Register the transform
        Transform transform = new Transform("jtp.analysis", new BodyTransformer() {
            @Override
            protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
                IntraproceduralAnalysisUpdated ipa = new IntraproceduralAnalysisUpdated(b, reporter);
                ipa.doAnalyis(cache,changeSet);
            }
        });
        PackManager.v().getPack("jtp").add(transform);
        String targetDir = "C:\\Users\\karth\\git\\visuflow-uitests-target\\bin";
        Main.main(new String[] { "-pp", "-process-dir", targetDir, "-w", "-exclude", "javax", "-allow-phantom-refs", "-no-bodies-for-excluded", "-src-prec", "class", "-output-format", "none" });
    }

}
