package io.katharsis.vertx.examples.repository;

import io.katharsis.vertx.examples.domain.Task;
import io.katharsis.vertx.examples.services.Generator;
import io.katharsis.queryParams.QueryParams;
import io.katharsis.repository.annotations.JsonApiDelete;
import io.katharsis.repository.annotations.JsonApiFindAll;
import io.katharsis.repository.annotations.JsonApiFindAllWithIds;
import io.katharsis.repository.annotations.JsonApiFindOne;
import io.katharsis.repository.annotations.JsonApiResourceRepository;
import io.katharsis.repository.annotations.JsonApiSave;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@Slf4j
@JsonApiResourceRepository(value = Task.class)
public class TaskRepository {

    @JsonApiFindOne
    public Task findOne(Long aLong, QueryParams queryParams, ApplicationContext context) {
        log.info("Find one {} {}", aLong, queryParams);
        Generator generator = context.getBean(Generator.class);

        return generator.generateTask(aLong);
    }

    @JsonApiFindAll
    public Iterable<Task> findAll(QueryParams queryParams, ApplicationContext context) {
        log.info("find all {}", queryParams);
        Generator generator = context.getBean(Generator.class);
        return allTasks(generator);
    }

    @JsonApiFindAllWithIds
    public Iterable<Task> findAll(Iterable<Long> longs, QueryParams queryParams, ApplicationContext context) {
        log.info("find all {} {}", longs, queryParams);
        Generator generator = context.getBean(Generator.class);
        return allTasks(generator);
    }

    public Iterable<Task> allTasks(Generator generator) {
        return Arrays.asList(generator.generateTask(1L),
                generator.generateTask(10L),
                generator.generateTask(2L),
                generator.generateTask(14L)
        );
    }

    @JsonApiDelete
    public void delete(Long aLong) {
        log.info("Delete Task {}", aLong);
    }

    @JsonApiSave
    public <S extends Task> S save(S entity) {
        log.info("Save task {}", entity);
        return entity;
    }
}
