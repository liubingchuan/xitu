package com.yiyao.app.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.yiyao.app.model.Project;

public interface ProjectRepository extends ElasticsearchRepository<Project,String>{
	Optional<Project> findById(String id);
}
