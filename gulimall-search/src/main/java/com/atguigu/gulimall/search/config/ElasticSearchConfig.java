package com.atguigu.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient elasticSearchClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder( new HttpHost("150.158.106.207",9200,"http")
        ));

        return client ;
    }
}
