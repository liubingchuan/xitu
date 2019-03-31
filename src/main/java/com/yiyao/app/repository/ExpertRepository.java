package com.yiyao.app.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.yiyao.app.model.Expert;
import com.yiyao.app.model.Org;

public interface ExpertRepository extends ElasticsearchRepository<Expert,String>{
	Optional<Expert> findById(String id);
}
