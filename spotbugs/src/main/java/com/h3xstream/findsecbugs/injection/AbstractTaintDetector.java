/**
 * Find Security Bugs
 * Copyright (c) Philippe Arteau, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.h3xstream.findsecbugs.injection;


import com.h3xstream.findsecbugs.taintanalysis.TaintDataflow;
import com.h3xstream.findsecbugs.taintanalysis.TaintFrame;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.DataflowAnalysisException;
import edu.umd.cs.findbugs.ba.Location;
import edu.umd.cs.findbugs.bcel.BCELUtil;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.*;

/**
 * Detector designed for extension to allow usage of taint analysis
 *
 * @author David Formanek (Y Soft Corporation, a.s.)
 */
public abstract class AbstractTaintDetector implements Detector {
    protected final BugReporter bugReporter;
//    public static Method sMethod = null;

    protected AbstractTaintDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    /**
     * Allow any concrete implementation of taint detector to skip the analysis of certain files.
     * The purpose can be for optimisation or to trigger bug in specific context.
     *
     * The default implementation returns true to all classes visited.
     *
     * @param classContext Information about the class that is about to be analyzed
     * @return If the given class should be analyze.
     */
    public boolean shouldAnalyzeClass(ClassContext classContext) {
        return true;
    }

    @Override
    public void visitClassContext(ClassContext classContext) {
        if(!shouldAnalyzeClass(classContext)) {
            return;
        }
        for (Method method : classContext.getMethodsInCallOrder()) {
            if (classContext.getMethodGen(method) == null) {
                continue;
            }
            try {
//                sMethod = method;
                analyzeMethod(classContext, method);
            } catch (CheckedAnalysisException e) {
                logException(classContext, method, e);
            } catch (RuntimeException e) {
                logException(classContext, method, e);
            }
        }
    }

    @Override
    public void report() {
    }
    
    protected void analyzeMethod(ClassContext classContext, Method method)
            throws CheckedAnalysisException {
        TaintDataflow dataflow = getTaintDataFlow(classContext, method);
        ConstantPoolGen cpg = classContext.getConstantPoolGen();
        //method
        String currentMethod = getFullMethodName(classContext.getMethodGen(method));

        for (Iterator<Location> i = getLocationIterator(classContext, method); i.hasNext();) {
            Location location = i.next();
            InstructionHandle handle = location.getHandle();
            TaintFrame fact = dataflow.getFactAtLocation(location);
            Instruction instruction = handle.getInstruction();

             if (!(instruction instanceof InvokeInstruction)) {
                continue;
            }
            InvokeInstruction invoke = (InvokeInstruction) instruction;

            assert fact != null;
            if (!fact.isValid()) {
                continue;
            }
            analyzeLocation(classContext, method, handle, cpg, invoke, fact, currentMethod);
        }

    }
    
    private static Iterator<Location> getLocationIterator(ClassContext classContext, Method method)
            throws CheckedAnalysisException {
        try {
            return classContext.getCFG(method).locationIterator();
        } catch (CFGBuilderException ex) {
            throw new CheckedAnalysisException("cannot get control flow graph", ex);
        }
    }
    
    private static TaintDataflow getTaintDataFlow(ClassContext classContext, Method method)
            throws CheckedAnalysisException {
        MethodDescriptor descriptor = BCELUtil.getMethodDescriptor(classContext.getJavaClass(), method);
        return Global.getAnalysisCache().getMethodAnalysis(TaintDataflow.class, descriptor);
    }

    private static String getFullMethodName(MethodGen methodGen) {
        String methodNameWithSignature = methodGen.getName() + methodGen.getSignature();
        String slashedClassName = methodGen.getClassName().replace('.', '/');
        return slashedClassName + "." + methodNameWithSignature;
    }
    
    private void logException(ClassContext classContext, Method method, Exception ex) {
        bugReporter.logError("Exception while analyzing "
                + classContext.getFullyQualifiedMethodName(method), ex);
    }
    
    abstract protected void analyzeLocation(ClassContext classContext, Method method, InstructionHandle handle,
                                            ConstantPoolGen cpg, InvokeInstruction invoke, TaintFrame fact, String currentMethod)
            throws DataflowAnalysisException;
}
