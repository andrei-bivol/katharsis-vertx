package io.katharsis.vertx.examples.repository;


import io.katharsis.vertx.examples.domain.Project;
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
@JsonApiResourceRepository(value = Project.class)
public class ProjectRepository {

    @JsonApiFindOne
    public Project findOne(Long aLong, QueryParams queryParams, ApplicationContext context) {
        log.info("Find all {} {}", aLong, queryParams);
        Generator generator = context.getBean(Generator.class);
        return generator.generateProject(aLong);
    }

    @JsonApiFindAll
    public Iterable<Project> findAll(QueryParams queryParams, ApplicationContext context) {
        log.info("Find all {}", queryParams);
        Generator generator = context.getBean(Generator.class);

        return Arrays.asList(
                generator.generateProject(1L),
                generator.generateProject(2L),
                generator.generateProject(3L),
                generator.generateProject(4L),
                generator.generateProject(10L)
        );
    }

    @JsonApiFindAllWithIds
    public Iterable<Project> findAll(Iterable<Long> longs, QueryParams queryParams, ApplicationContext context) {
        return findAll(queryParams, context);
    }

    @JsonApiDelete
    public void delete(Long aLong) {
        log.info("Delete {}", aLong);
    }

    @JsonApiSave
    public <S extends Project> S save(S entity) {
        log.info("Save project {}", entity);
        return entity;
    }
}
