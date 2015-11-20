package com.netdava.katharsis.example.repository;

import com.netdava.katharsis.example.domain.Project;
import com.netdava.katharsis.example.domain.Task;
import io.katharsis.queryParams.QueryParams;
import io.katharsis.repository.RelationshipRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class TaskToProjectRepository implements RelationshipRepository<Task, Long, Project, Long> {

    @Override
    public void setRelation(Task source, Long targetId, String fieldName) {
        log.info("Set relation {} {} {}", source, targetId, fieldName);
    }

    @Override
    public void setRelations(Task source, Iterable<Long> targetIds, String fieldName) {
        log.info("Set relationS {} {} {}", source, targetIds, fieldName);
    }

    @Override
    public void addRelations(Task source, Iterable<Long> targetIds, String fieldName) {
        log.info("Add relations {} {} {}", source, targetIds, fieldName);
    }

    @Override
    public void removeRelations(Task source, Iterable<Long> targetIds, String fieldName) {
        log.info("Remove relations {} {} {}", source, targetIds, fieldName);
    }

    @Override
    public Project findOneTarget(Long sourceId, String fieldName, QueryParams queryParams) {
        log.info("Find one target {} {} {}", sourceId, fieldName, queryParams);
        return Project.builder().id(sourceId).name("find one target " + fieldName).build();
    }

    @Override
    public Iterable<Project> findManyTargets(Long sourceId, String fieldName, QueryParams queryParams) {
        log.info("Find many targets {} {} {}", sourceId, fieldName, queryParams);
        return Arrays.asList(Project.builder().id(sourceId).name("find many targets " + fieldName).build());
    }
}
