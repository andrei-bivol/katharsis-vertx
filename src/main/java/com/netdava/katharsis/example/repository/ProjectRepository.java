package com.netdava.katharsis.example.repository;


import com.netdava.katharsis.example.domain.Project;
import io.katharsis.queryParams.QueryParams;
import io.katharsis.repository.ResourceRepository;

import java.util.Arrays;

public class ProjectRepository implements ResourceRepository<Project, Long> {

    @Override
    public Project findOne(Long aLong, QueryParams queryParams) {
        return null;
    }

    @Override
    public Iterable<Project> findAll(QueryParams queryParams) {
        return findAll(null, queryParams);
    }

    @Override
    public Iterable<Project> findAll(Iterable<Long> longs, QueryParams queryParams) {
        return Arrays.asList(
                Project.builder().id(1L).name("ProfilesRD").build(),
                Project.builder().id(2L).name("Great people inside").build()
        );
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public <S extends Project> S save(S entity) {
        return null;
    }
}
