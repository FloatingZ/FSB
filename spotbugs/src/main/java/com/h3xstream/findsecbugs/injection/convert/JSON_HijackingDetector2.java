package com.h3xstream.findsecbugs.injection.convert;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

public class JSON_HijackingDetector2 extends OpcodeStackDetector {
    private BugReporter bugReporter;

    public  JSON_HijackingDetector2(BugReporter bugReporter){this.bugReporter = bugReporter;}
    @Override
    public void sawOpcode(int seen) {

    }
}
