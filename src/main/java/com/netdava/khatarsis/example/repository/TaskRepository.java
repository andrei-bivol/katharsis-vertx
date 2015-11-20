package com.netdava.khatarsis.example.repository;

import com.netdava.khatarsis.example.domain.Task;
import io.katharsis.queryParams.QueryParams;
import io.katharsis.repository.ResourceRepository;

import java.util.Arrays;

public class TaskRepository implements ResourceRepository<Task, Long> {

    @Override
    public Task findOne(Long aLong, QueryParams queryParams) {
        return Task.builder()
                .id(aLong).name("Some task " + aLong)
                .build();
    }

    @Override
    public Iterable<Task> findAll(QueryParams queryParams) {
        return findAll(null, queryParams);
    }

    @Override
    public Iterable<Task> findAll(Iterable<Long> longs, QueryParams queryParams) {
        return Arrays.asList(Task.builder().id(1L).name("First task").build());
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public <S extends Task> S save(S entity) {
        return null;
    }
}
