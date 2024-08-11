package com.zerobase.hoops.friends.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.hoops.document.FriendDocument;
import com.zerobase.hoops.document.InviteDocument;
import com.zerobase.hoops.document.ParticipantGameDocument;
import com.zerobase.hoops.friends.dto.InviteFriendListDto;
import com.zerobase.hoops.friends.dto.SearchFriendListDto;
import com.zerobase.hoops.friends.repository.FriendCustomRepository;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class FriendCustomRepositoryImpl implements FriendCustomRepository {

  private final ElasticsearchClient client;
  private final ObjectMapper objectMapper;

  @Override
  public Page<SearchFriendListDto.Response> findBySearchFriendList(String userId, String nickName, Pageable pageable) {

    try {
      List<String> excludedIds = getExcludedIds(userId);
      SearchRequest searchRequest = new SearchRequest.Builder()
          .index("user")
          .query(q -> q.bool(b -> b
              .must(m -> m.match(t -> t.field("nickName").query(nickName)))
              .mustNot(m -> m.terms(t -> t.field("id").terms(
                  (TermsQueryField) excludedIds))) // 올바른 메서드 체인 사용
              .must(m -> m.term(t -> t.field("deletedDateTime").value("null")))
          ))
          .from((int) pageable.getOffset())
          .size(pageable.getPageSize())
          .sort(s -> s.field(f -> f.field("nickName").order(co.elastic.clients.elasticsearch._types.SortOrder.Asc)))
          .build();

      SearchResponse<FriendDocument> response = client.search(searchRequest, FriendDocument.class);
      List<SearchFriendListDto.Response> results = response.hits().hits()
          .stream().map(Hit::source)
          .map(SearchFriendListDto.Response::toDto)
          .collect(Collectors.toList());

      long total = response.hits().total().value();

      return new PageImpl<>(results, pageable, total);
    } catch (IOException e) {
      throw new RuntimeException("Elasticsearch query failed", e);
    }
  }

  @Override
  public Page<InviteFriendListDto.Response> findByMyInviteFriendList(String userId, String gameId, Pageable pageable) {

    try {
      List<String> excludedIds = getExcludedIds(userId);
      SearchRequest searchRequest = new SearchRequest.Builder()
          .index("friend")
          .query(q -> q.bool(b -> b
              .must(m -> m.match(t -> t.field("friendUser.id").query(userId)))
              .must(m -> m.match(t -> t.field("status").query(FriendStatus.ACCEPT.name())))
              .mustNot(m -> m.terms(t -> t.field("friendUser.id").terms(
                  (TermsQueryField) excludedIds)))
              .must(m -> m.term(t -> t.field("deletedDateTime").value("null")))
          ))
          .from((int) pageable.getOffset())
          .size(pageable.getPageSize())
          .sort(s -> s.field(f -> f.field("nickName").order(co.elastic.clients.elasticsearch._types.SortOrder.Asc)))
          .build();

      SearchResponse<InviteDocument> response = client.search(searchRequest, InviteDocument.class);
      List<InviteFriendListDto.Response> results = response.hits().hits().stream()
          .map(Hit::source)
          .map(InviteFriendListDto.Response::toDto)
          .collect(Collectors.toList());
      long total = response.hits().total().value();

      return new PageImpl<>(results, pageable, total);
    } catch (IOException e) {
      throw new RuntimeException("Elasticsearch query failed", e);
    }
  }

  private List<String> getExcludedIds(String userId) {
    if (userId == null) {
      throw new IllegalArgumentException("Parameter cannot be null");
    }

    try {
      SearchRequest searchRequest = new SearchRequest.Builder()
          .index("user")
          .query(q -> q.bool(b -> b
              .must(m -> m.match(t -> t.field("roles").query("ROLE_OWNER")))
              .must(m -> m.term(t -> t.field("deletedDateTime").value("null")))
          ))
          .build();

      SearchResponse<FriendDocument> response = client.search(searchRequest, FriendDocument.class);
      List<String> excludedIds = response.hits().hits().stream()
          .map(Hit::id)
          .collect(Collectors.toList());
      excludedIds.add(userId);

      return excludedIds;
    } catch (IOException e) {
      throw new RuntimeException("Elasticsearch query failed", e);
    }
  }

  private List<String> getExcludedIdsByGame(String gameId) {
    if (gameId == null) {
      throw new IllegalArgumentException("Parameter cannot be null");
    }

    try {
      SearchRequest searchRequest = new SearchRequest.Builder()
          .index("participant_game")
          .query(q -> q.bool(b -> b
              .must(m -> m.match(t -> t.field("game.id").query(gameId)))
                  .must(m -> m.terms(t -> t.field("status").terms(
                      (TermsQueryField) Arrays.asList(ParticipantGameStatus.ACCEPT.name(), ParticipantGameStatus.APPLY.name()))
                  ))))
          .build();

      SearchResponse<ParticipantGameDocument> response = client.search(searchRequest, ParticipantGameDocument.class);
      return response.hits().hits().stream()
          .map(Hit::id)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Elasticsearch query failed", e);
    }
  }

}
