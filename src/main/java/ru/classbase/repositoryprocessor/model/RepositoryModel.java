package ru.classbase.repositoryprocessor.model;

public record RepositoryModel(String packageName, Iterable<RepositoryClass> classes) {
}
