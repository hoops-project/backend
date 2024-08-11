package com.zerobase.hoops.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories
public class ElasticSearchConfig {

  @Value("${spring.elasticsearch.host}")
  private String host;

  @Value("${spring.elasticsearch.username}")
  private String username;

  @Value("${spring.elasticsearch.password}")
  private String password;

  @Bean
  public RestClientBuilder restClientBuilder() {
    final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
        AuthScope.ANY,
        new UsernamePasswordCredentials(username, password)
    );

    return RestClient.builder(
        new HttpHost(host, 9200)
    ).setHttpClientConfigCallback(httpClientBuilder ->
        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
    );
  }

  @Bean
  public RestClient restClient() {
    return restClientBuilder().build();
  }

  @Bean
  public RestClientTransport restClientTransport() {
    return new RestClientTransport(restClient(), new JacksonJsonpMapper());
  }

  @Bean
  public ElasticsearchClient elasticsearchClient() {
    return new ElasticsearchClient(restClientTransport());
  }

}
