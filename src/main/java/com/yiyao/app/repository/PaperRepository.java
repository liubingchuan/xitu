package com.yiyao.app.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.yiyao.app.model.Paper;
import com.yiyao.app.model.Project;

public interface PaperRepository extends ElasticsearchRepository<Paper,String>{
	Optional<Paper> findById(String id);
}
