package de.visuflow.ex1;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.visuflow.reporting.EmptyReporter;
import de.visuflow.reporting.IReporter;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Main;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.StringConstant;
import soot.jimple.internal.InvokeExprBox;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.util.Chain;

public class ReachableMainClass {

	public static void main(String[] args) {
		runAnalysis(new EmptyReporter());
	}

	public static void runAnalysis(final IReporter reporter) {
		G.reset();

        Transform removeUnreachableMethods = new Transform("wjtp.removeUnreachableMethods", new SceneTransformer() {
            @SuppressWarnings("rawtypes")
            @Override
            protected void internalTransform(String phase, Map<String, String> arg1) {
                Scene sc = Scene.v();
                Iterator classIt = sc.getApplicationClasses().iterator();

                while (classIt.hasNext()) {
                    List<SootMethod> unreachableMethods = new ArrayList<>();
                    SootClass c = (SootClass) classIt.next();
                    Iterator methodIt = c.methodIterator();
                    while (methodIt.hasNext()) {
                        SootMethod method = (SootMethod) methodIt.next();
                        if (!sc.getReachableMethods().contains(method)) {
                            // method is not reachable and can be ignored by the DES analyzer
                            unreachableMethods.add(method);
                        }
                    }
                    
                    for (SootMethod sootMethod : unreachableMethods) {
                        SootClass declaringClass = sootMethod.getDeclaringClass();
                        declaringClass.removeMethod(sootMethod);
                    }
                }
            }
        });
        
        Transform findDesUses = new Transform("jtp.findDesUses", new BodyTransformer() {
            @Override
            protected void internalTransform(Body b, String phase, Map<String, String> options) {
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
            }
        });

        PackManager.v().getPack("wjtp").add(removeUnreachableMethods);
		PackManager.v().getPack("jtp").add(findDesUses);

		String rtJar = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";

		// Run Soot
		Main.main(new String[] { "-cp", "./bin" + File.pathSeparator + rtJar, "-exclude", "javax",
				"-allow-phantom-refs", "-no-bodies-for-excluded", "-process-dir", "./targetBin1", "-src-prec",
				"only-class", "-w", "-output-format", "none", "de.visuflow.analyzeMe.ex1.TargetClass1" });
	}

}
