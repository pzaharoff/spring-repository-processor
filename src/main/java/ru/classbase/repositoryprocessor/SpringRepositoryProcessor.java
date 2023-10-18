package ru.classbase.repositoryprocessor;

import com.google.auto.service.AutoService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import ru.classbase.repositoryprocessor.model.BaseRepositoryClass;
import ru.classbase.repositoryprocessor.model.Options;
import ru.classbase.repositoryprocessor.model.RepositoryClass;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

@AutoService(Processor.class)
@SupportedAnnotationTypes("jakarta.persistence.Entity")
@SupportedOptions({
        SpringRepositoryProcessor.TARGET_PLATFORM,
})

public class SpringRepositoryProcessor extends AbstractExtendedProcessor {
    protected static final String TARGET_PLATFORM = "srp.targetPlatform";
    private String classNameSuffix = "Dao";
    private String baseRepositoryPackage = "ru.classbase";
    private String baseRepositoryClass = "Jpa" + classNameSuffix;
    private String keyClassName = "java.util.UUID";

    private Options options;

    private Configuration cfg = configureFreeMarker();

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        options = configureOptions();
    }

    private Options configureOptions() {
        String targetPlatform = "java".equals(processingEnv.getOptions().get(TARGET_PLATFORM)) ? "java" : "kotlin";

        return new Options(targetPlatform);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        printNote(this.getClass().getSimpleName() + ": start processing");

        if (env.processingOver() || annotations.isEmpty()) {
            return true;
        }

        final var classes = new ArrayList<RepositoryClass>();

        for (TypeElement annotation : annotations) {

            for (Element element : env.getElementsAnnotatedWith(annotation)) {

                final var clazz = new RepositoryClass(
                        elements.getPackageOf(element).getSimpleName().toString(),
                        element.getSimpleName().toString(),
                        classNameSuffix,
                        baseRepositoryPackage,
                        baseRepositoryClass,
                        List.of()
                );

                classes.add(clazz);
            }
        }

        final var baseModel = new BaseRepositoryClass(baseRepositoryPackage, baseRepositoryClass, keyClassName);
        renderClassTemplate("java-base-repository.ftlh", baseRepositoryClass, baseModel);

        for (RepositoryClass classModel : classes) {
            renderClassTemplate("java-repository.ftlh", classModel.className() + classModel.classNameSuffix(), classModel);
        }

        printNote(this.getClass().getSimpleName() + ": end processing");

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void renderClassTemplate(String templateName, String className, Object model) {
        try {

            Template tpl = cfg.getTemplate(templateName);

            final var map = new HashMap<String, Object>();
            map.put("model", model);

            JavaFileObject builderFile = filer.createSourceFile(className);

            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                tpl.process(map, out);
            }

        } catch (Exception e) {
            printError("Render template error: " + e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    private Configuration configureFreeMarker() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);

        //cfg.setDirectoryForTemplateLoading(new File("/where/you/store/templates"));
        cfg.setTemplateLoader(new ClassTemplateLoader(SpringRepositoryProcessor.class, "/templates"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());

        return cfg;
    }
}
