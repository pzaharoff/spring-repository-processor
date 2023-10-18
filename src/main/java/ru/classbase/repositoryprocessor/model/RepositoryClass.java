package ru.classbase.repositoryprocessor.model;

import java.util.List;

public record RepositoryClass(
        String packageName,
        String className,
        String classNameSuffix,
        String baseRepositoryPackage,
        String baseRepositoryClass,
        List<RepositoryMethod> methods
) {
}
