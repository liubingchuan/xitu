package com.yiyao.app.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.yiyao.app.model.Org;

public interface OrgRepository extends ElasticsearchRepository<Org,String>{
	Optional<Org> findById(String id);
}
