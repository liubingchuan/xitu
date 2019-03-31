package com.yiyao.app.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.yiyao.app.model.Paper;
import com.yiyao.app.model.Patent;
import com.yiyao.app.model.Project;

public interface PatentRepository extends ElasticsearchRepository<Patent,String>{
	Optional<Patent> findById(String id);
}
