package com.netdava.khatarsis.example.repository;

import com.netdava.khatarsis.example.domain.Project;
import com.netdava.khatarsis.example.domain.Task;
import io.katharsis.queryParams.QueryParams;
import io.katharsis.repository.RelationshipRepository;

public class TaskToProjectRepository implements RelationshipRepository<Task, Long, Project, Long> {

    @Override
    public void setRelation(Task source, Long targetId, String fieldName) {

    }

    @Override
    public void setRelations(Task source, Iterable<Long> targetIds, String fieldName) {

    }

    @Override
    public void addRelations(Task source, Iterable<Long> targetIds, String fieldName) {

    }

    @Override
    public void removeRelations(Task source, Iterable<Long> targetIds, String fieldName) {

    }

    @Override
    public Project findOneTarget(Long sourceId, String fieldName, QueryParams queryParams) {
        return null;
    }

    @Override
    public Iterable<Project> findManyTargets(Long sourceId, String fieldName, QueryParams queryParams) {
        return null;
    }
}
