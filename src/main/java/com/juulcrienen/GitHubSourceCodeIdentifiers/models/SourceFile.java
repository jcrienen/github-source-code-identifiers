package com.juulcrienen.GitHubSourceCodeIdentifiers.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class SourceFile {

    private File file;
    protected List<String> methodNames;
    protected List<String> parameterNames;
    protected List<String> variableNames;
    protected List<String> classNames;

    public SourceFile(File file, String extension) {
        if(!file.getPath().endsWith(extension)) throw new IllegalArgumentException("File does not have " + extension + " extension!");
        this.file = file;
        methodNames = new ArrayList<>();
        parameterNames = new ArrayList<>();
        variableNames = new ArrayList<>();
        classNames = new ArrayList<>();
    }

    public File getFile() {
        return file;
    }

    public List<String> getMethodNames() {
        return methodNames;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public List<String> getVariableNames() {
        return variableNames;
    }

    public List<String> getClassNames() { return classNames; }

    public List<String> getIdentifiers() {
        List<String> result = new ArrayList<>();
        result.addAll(methodNames);
        result.addAll(parameterNames);
        result.addAll(variableNames);
        result.addAll(classNames);
        return result;
    }

    public boolean addMethodName(String methodName) {
        return methodNames.add(methodName);
    }
    public boolean addParameterName(String parameterName) {
        return parameterNames.add(parameterName);
    }
    public boolean addVariableName(String variableName) {
        return variableNames.add(variableName);
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
    public boolean addVariableNames(List<String> names) {
        return variableNames.addAll(names);
    }
    public boolean addClassNames(List<String> names) {
        return classNames.addAll(names);
    }
}
