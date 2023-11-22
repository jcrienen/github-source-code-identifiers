package com.juulcrienen.GitHubSourceCodeIdentifiers.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class ClassFile {

    private File file;
    protected List<String> methodNames;
    protected List<String> parameterNames;
    protected List<String> variableNames;

    public ClassFile(File file, String extension) {
        if(!file.getPath().endsWith(extension)) throw new IllegalArgumentException("File does not have " + extension + " extension!");
        this.file = file;
        methodNames = new ArrayList<>();
        parameterNames = new ArrayList<>();
        variableNames = new ArrayList<>();
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

    public List<String> getIdentifiers() {
        List<String> result = new ArrayList<>();
        result.addAll(methodNames);
        result.addAll(parameterNames);
        result.addAll(variableNames);
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

    public boolean addVariableNames(List<String> names) {
        return variableNames.addAll(names);
    }
}
