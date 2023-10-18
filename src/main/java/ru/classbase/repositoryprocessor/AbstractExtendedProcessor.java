package ru.classbase.repositoryprocessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public abstract class AbstractExtendedProcessor extends AbstractProcessor {
    protected Types types;
    protected Elements elements;
    protected Messager messager;
    protected Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        types = env.getTypeUtils();
        elements = env.getElementUtils();
        messager = env.getMessager();
        filer = env.getFiler();
    }

    protected void printError(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

    protected void printNote(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

}
