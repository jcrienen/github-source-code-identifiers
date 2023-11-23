package com.juulcrienen.githubsourcecodeidentifiers.models;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SourceFile {

    private File file;

    protected List<String> methodNames;
    protected List<String> parameterNames;
    protected List<String> globalVariableNames;
    protected List<String> localVariableNames;
    protected List<String> classNames;

    private long language;

    public SourceFile(File file, long language) {
        this.file = file;
        methodNames = new ArrayList<>();
        parameterNames = new ArrayList<>();
        globalVariableNames = new ArrayList<>();
        localVariableNames = new ArrayList<>();
        classNames = new ArrayList<>();
        this.language = language;
    }

    public File getFile() {
        return file;
    }

    public long getLanguage() {
        return language;
    }

    public List<String> getMethodNames() {
        return methodNames;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public List<String> getGlobalVariableNames() {
        return globalVariableNames;
    }

    public List<String> getLocalVariableNames() { return localVariableNames; }

    public List<String> getClassNames() { return classNames; }

    public List<String> getIdentifiers() {
        List<String> result = new ArrayList<>();
        result.addAll(methodNames);
        result.addAll(parameterNames);
        result.addAll(globalVariableNames);
        result.addAll(localVariableNames);
        result.addAll(classNames);
        return result;
    }

    public boolean addMethodName(String methodName) {
        return methodNames.add(methodName);
    }
    public boolean addParameterName(String parameterName) {
        return parameterNames.add(parameterName);
    }
    public boolean addGlobalVariableName(String variableName) {
        return globalVariableNames.add(variableName);
    }
    public boolean addLocalVariableName(String variableName) {
        return localVariableNames.add(variableName);
    }
    public boolean addClassName(String className) {
        return classNames.add(className);
    }

    public boolean addMethodNames(List<String> names) {
        return methodNames.addAll(names);
    }
    public boolean addParameterNames(List<String> names) {
        return parameterNames.addAll(names);
    }
    public boolean addGlobalVariableNames(List<String> names) {
        return globalVariableNames.addAll(names);
    }
    public boolean addLocalVariableNames(List<String> names) {
        return localVariableNames.addAll(names);
    }
    public boolean addClassNames(List<String> names) {
        return classNames.addAll(names);
    }
}
