package ru.classbase.repositoryprocessor;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class SpringRepositoryProcessorTest {
    private static final String resourcePath = "/ru/classbase/repositoryprocessor";

    @Test
    public void process() throws IOException {

        String s = """
                package ru.classbase.repositoryprocessor;        
                        
                import jakarta.persistence.Entity;
                import jakarta.persistence.Id;
                import jakarta.persistence.Table;
                        
                import java.util.UUID;
                        
                @Entity
                @Table(name="user")
                public class User {
                        
                    @Id
                    private UUID id;
                        
                    public UUID getId() {
                        return id;
                    }
                        
                    public void setId(UUID id) {
                        this.id = id;
                    }
                }        
                """;

        Compilation compilation = javac()
                .withProcessors(new SpringRepositoryProcessor())
                .withOptions(ImmutableList.of("-Asrp.targetPlatform=kotlin"))
                .compile(JavaFileObjects.forSourceString("ru.classbase.repositoryprocessor.User", s));

        assertThat(compilation).succeeded();

        var files = compilation.generatedSourceFiles();

        for (JavaFileObject file : files) {
            System.out.println(new String(file.openInputStream().readAllBytes(), StandardCharsets.UTF_8));
        }
    }

}
