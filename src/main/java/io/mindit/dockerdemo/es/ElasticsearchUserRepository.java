package io.mindit.dockerdemo.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticsearchUserRepository extends ElasticsearchRepository<ElasticsearchUser, Integer> {

}
