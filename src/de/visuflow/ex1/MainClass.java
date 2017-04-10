package de.visuflow.ex1;

import java.io.File;
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
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.StringConstant;
import soot.jimple.internal.InvokeExprBox;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.util.Chain;

public class MainClass {

    public static void main(String[] args) {
        runAnalysis(new EmptyReporter());
    }

    public static void runAnalysis(final IReporter reporter) {
        G.reset();

        Transform transform = new Transform("jtp.myTransform", new BodyTransformer() {
            @Override
            protected void internalTransform(Body b, String phase, Map<String, String> options) {
                System.out.println(b);
                Chain<Unit> units = b.getUnits();
                for (Unit unit : units) {
                    List<ValueBox> useBoxes = unit.getUseBoxes();
                    for (ValueBox box : useBoxes) {
                        if (box instanceof InvokeExprBox) {
                            InvokeExprBox invoke = (InvokeExprBox) box;
                            if (invoke.getValue() instanceof JStaticInvokeExpr) {
                                JStaticInvokeExpr staticCall = (JStaticInvokeExpr) invoke.getValue();
                                if (!"javax.crypto.Cipher".equals(staticCall.getMethodRef().declaringClass().getName())) {
                                    continue;
                                }
                                if (!"getInstance".equals(staticCall.getMethodRef().name())) {
                                    continue;
                                }

                                Value arg = staticCall.getArg(0);
                                String algorithmName = "";
                                if(arg instanceof StringConstant) {
                                    algorithmName = ((StringConstant)arg).value;
                                }
                                if (algorithmName.equals("DES")) {
                                    reporter.report(b.getMethod(), unit);
                                }
                            }

                        }
                    }
                }

                //			    for (Unit unit : units) {
                //			        if(unit.toString().equals("staticinvoke <javax.crypto.Cipher: javax.crypto.Cipher getInstance(java.lang.String)>(\"DES\")")) {
                //			            IReporter reporter = new EmptyReporter();
                //			            reporter.report(b.getMethod(), unit);
                //			        }
                //                }
            }
        });

        PackManager.v().getPack("jtp").add(transform);

        String rtJar = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";

        // Run Soot
        //        Main.main(new String[] { "-cp", "./bin" + File.pathSeparator + rtJar, "-exclude", "javax",
        //                "-allow-phantom-refs", "-no-bodies-for-excluded", "-process-dir", "./targetBin1", "-src-prec",
        //                "only-class", "-output-format", "none", "de.visuflow.analyzeMe.ex1.TargetClass1" });
        Main.main(new String[] { "-cp", "./bin" + File.pathSeparator + rtJar, "-exclude", "javax",
                "-allow-phantom-refs", "-no-bodies-for-excluded", "-process-dir", "./targetBin1", "-src-prec",
                "only-class", "-output-format", "none", "de.visuflow.analyzeMe.ex1.MockupClass" });
    }

}
