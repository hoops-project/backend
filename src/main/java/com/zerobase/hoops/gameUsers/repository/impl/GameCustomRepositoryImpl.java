package com.zerobase.hoops.gameUsers.repository.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonData;
import com.zerobase.hoops.document.GameDocument;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameUsers.repository.GameCustomRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameCustomRepositoryImpl implements GameCustomRepository {

  private ElasticsearchClient client;

  @Override
  public List<GameDocument> findAllGameDocuments(LocalDate localDate,
      CityName cityName, FieldStatus fieldStatus, Gender gender,
      MatchFormat matchFormat) {

    BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

    boolQueryBuilder.must(QueryBuilders.term().field("deleted").value(false).build()._toQuery());
    boolQueryBuilder.must(QueryBuilders.range().field("startDate").gte(JsonData.of(localDate.toString())).build()._toQuery());

    if (cityName != null) {
      boolQueryBuilder.must(QueryBuilders.term().field("cityName").value(cityName.toString()).build()._toQuery());
    }
    if (fieldStatus != null) {
      boolQueryBuilder.must(QueryBuilders.term().field("fieldStatus").value(fieldStatus.toString()).build()._toQuery());
    }
    if (gender != null) {
      boolQueryBuilder.must(QueryBuilders.term().field("gender").value(gender.toString()).build()._toQuery());
    }
    if (matchFormat != null) {
      boolQueryBuilder.must(QueryBuilders.term().field("matchFormat").value(matchFormat.toString()).build()._toQuery());
    }

    Query query = new Query.Builder().bool(boolQueryBuilder.build()).build();

    SearchRequest searchRequest = SearchRequest.of(s -> s
        .index("your-index-name")
        .query(query)
    );

    try {
      SearchResponse<GameDocument> searchResponse = client.search(searchRequest, GameDocument.class);
      return searchResponse.hits().hits().stream()
          .map(hit -> hit.source())
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Failed to execute search", e);
    }

  }
}
