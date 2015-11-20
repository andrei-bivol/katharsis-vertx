package com.netdava.khatarsis.example.repository;


import com.netdava.khatarsis.example.domain.Project;
import io.katharsis.queryParams.QueryParams;
import io.katharsis.repository.ResourceRepository;

public class ProjectRepository implements ResourceRepository<Project, Long> {
    
    @Override
    public Project findOne(Long aLong, QueryParams queryParams) {
        return null;
    }

    @Override
    public Iterable<Project> findAll(QueryParams queryParams) {
        return null;
    }

    @Override
    public Iterable<Project> findAll(Iterable<Long> longs, QueryParams queryParams) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public <S extends Project> S save(S entity) {
        return null;
    }
}
