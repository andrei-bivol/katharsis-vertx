package io.katharsis.vertx.examples.services;

import io.katharsis.vertx.examples.domain.Project;
import io.katharsis.vertx.examples.domain.Task;
import lombok.NonNull;

public class Generator {

    public Task generateTask(@NonNull Long id) {
        return Task.builder()
                .name("Generated task")
                .id(id)
                .build();
    }

    public Project generateProject(@NonNull Long id) {
        return Project.builder()
                .name("Generated project")
                .id(id)
                .build();
    }
}
