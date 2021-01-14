package com.socatel.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socatel.knowledge_base_dump.*;
import com.socatel.models.*;
import com.socatel.models.relationships.PropositionUserVote;
import com.socatel.models.relationships.UserPostVote;
import com.socatel.repositories.*;
import com.socatel.rest_api.RestAPICaller;
import com.socatel.services.email.EmailService;
import com.socatel.services.group.GroupService;
import com.socatel.utils.Constants;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class ScheduledMethods {

    private final Methods methods;
    private final GroupService groupService;
    private final EmailService emailService;
    private final RestHighLevelClient client;
    private final KBDump kbDump;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final OrganisationRepository organisationRepository;
    private final HistoryRepository historyRepository;
    private final PostRepository postRepository;
    private final PropositionRepository propositionRepository;
    private final ServiceRepository serviceRepository;
    private final UserPostVoteRepository userPostVoteRepository;
    private final PropositionUserVoteRepository propositionUserVoteRepository;
    private final LocalityRepository localityRepository;
    private final LanguageRepository languageRepository;
    private final ThemeRepository themeRepository;
    private final RestAPICaller restAPICaller;

    private ObjectMapper mapper = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(ScheduledMethods.class);

    public ScheduledMethods(Methods methods, GroupService groupService, EmailService emailService, RestHighLevelClient client, KBDump kbDump, UserRepository userRepository, GroupRepository groupRepository, OrganisationRepository organisationRepository, HistoryRepository historyRepository, PostRepository postRepository, PropositionRepository propositionRepository, ServiceRepository serviceRepository, UserPostVoteRepository userPostVoteRepository, PropositionUserVoteRepository propositionUserVoteRepository, LocalityRepository localityRepository, LanguageRepository languageRepository, ThemeRepository themeRepository, RestAPICaller restAPICaller) {
        this.methods = methods;
        this.groupService = groupService;
        this.emailService = emailService;
        this.client = client;
        this.kbDump = kbDump;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.organisationRepository = organisationRepository;
        this.historyRepository = historyRepository;
        this.postRepository = postRepository;
        this.propositionRepository = propositionRepository;
        this.serviceRepository = serviceRepository;
        this.userPostVoteRepository = userPostVoteRepository;
        this.propositionUserVoteRepository = propositionUserVoteRepository;
        this.localityRepository = localityRepository;
        this.languageRepository = languageRepository;
        this.themeRepository = themeRepository;
        this.restAPICaller = restAPICaller;
    }

    /**
     * Send weekly emails to users
     */
    @Scheduled(cron = "0 0 12 * * 1") // Every Monday at 12:00:00
    public void sendAutomaticMails() {
        logger.debug("Send automatic mails");
        // TODO
    }

    /**
     * Check every hour whether a group should be advanced or extended
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void checkAllGroupsStatus() {
        logger.debug("Check all groups' status");
        for (Group g : groupService.findAll()) {
            methods.nextStep(g.getId());
        }
    }

    /**
     * Check every day at 4:00 that the database has the same data that the Elasticsearch
     */
    //@Scheduled(cron = "0 0 4 * * *") // Every day at 4 am
    public void checkDatabaseIntegrity() {
        logger.debug("Check database integrity with the Elasticsearch");
        SearchHit[] hits;

        // Check organisations
        hits = performSearchRequest(Constants.ORGANISATION_TABLE);
        for (SearchHit hit : hits) {
            if (isHitNotInDatabase(hit, organisationRepository)) {
                try {
                    DbOrganisationRowDump org = mapper.readValue(mapper.writeValueAsString(hit.getSourceAsMap()), DbOrganisationRowDump.class);
                    organisationRepository.save(org.toModel());
                } catch (Exception e) {
                    logger.error("Error checking organisation integrity of hit: " + hit.toString() + "\n" + e.getLocalizedMessage());
                }
            }
        }

        // Check services
        hits = performSearchRequest(Constants.SERVICE_TABLE);
        for (SearchHit hit : hits) {
            if (isHitNotInDatabase(hit, serviceRepository)) {
                try {
                    DbServiceRowDump service = mapper.readValue(mapper.writeValueAsString(hit.getSourceAsMap()), DbServiceRowDump.class);
                    serviceRepository.save(service.toModel(localityRepository, languageRepository, organisationRepository, groupRepository, themeRepository));
                } catch (Exception e) {
                    logger.error("Error checking service integrity of hit: " + hit.toString() + "\n" + e.getLocalizedMessage());
                }
            }
        }

    }

    private SearchHit[] performSearchRequest(String index) {
        SearchHit[] hits;
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        try {
            hits = client.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits();
        } catch (IOException e) {
            hits = new SearchHit[0];
        }
        return hits;
    }

    private <T> boolean isHitNotInDatabase(SearchHit hit, JpaRepository<T, Integer> repository) {
        return !repository.findById(Integer.valueOf(hit.getId())).isPresent();
    }

    /**
     * Check every day at 3:00 that the Elasticsearch have the same data that the database
     */
    @Scheduled(cron = "0 0 3 * * *") // Every day at 3 am
    public void checkElasticsearchIntegrity() {
        logger.debug("Check Elasticsearch integrity with the database");

        DbUserPostVoteRowDump aux;
        DbUserPropositionVoteRowDump aux2;

        // Check users
        for (User row : userRepository.findAll()) {
            logger.debug("Check user " + row.getId());
            restAPICaller.insertUser(row);
            //checkElasticsearchIntegrity(kbDump.dumpSingleUserRow(row), Constants.USER_TABLE, Methods.encrypt(row.getId()));
        }
        // Check groups
        for (Group row : groupRepository.findAll()) {
            logger.debug("Check group " + row.getId());
            restAPICaller.insertGroup(row);
            //checkElasticsearchIntegrity(kbDump.dumpSingleGroupRow(row), Constants.GROUP_TABLE, String.valueOf(row.getId()));
        }

        // Check organisations
        for (Organisation row : organisationRepository.findAll()) {
            logger.debug("Check organisation " + row.getId());
            restAPICaller.insertOrganisation(row);
            //checkElasticsearchIntegrity(kbDump.dumpSingleOrganisationRow(row), Constants.ORGANISATION_TABLE, String.valueOf(row.getId()));
        }

        // Check services
        for (Service row : serviceRepository.findAll()) {
            logger.debug("Check service " + row.getId());
            restAPICaller.insertService(row);
            //checkElasticsearchIntegrity(kbDump.dumpSingleServiceRow(row), Constants.SERVICE_TABLE, String.valueOf(row.getId()));
        }

        // Check history
        for (History row : historyRepository.findAll()) {
            logger.debug("Check history " + row.getId());
            checkElasticsearchIntegrity(kbDump.dumpSingleHistoryRow(row), Constants.HISTORY_TABLE, String.valueOf(row.getId()));
        }

        // Check posts
        for (Post row : postRepository.findAll()) {
            logger.debug("Check post " + row.getId());
            checkElasticsearchIntegrity(kbDump.dumpSinglePostRow(row), Constants.POST_TABLE, String.valueOf(row.getId()));
        }

        // Check proposition
        for (Proposition row : propositionRepository.findAll()) {
            logger.debug("Check proposition " + row.getId());
            checkElasticsearchIntegrity(kbDump.dumpSinglePropositionRow(row), Constants.PROPOSITION_TABLE, String.valueOf(row.getId()));
        }

        // Check userPostVotes
        for (UserPostVote row : userPostVoteRepository.findAll()) {
            aux = kbDump.dumpSingleUserPostVoteRow(row);
            logger.debug("Check userPostVote " + aux.getId());
            checkElasticsearchIntegrity(aux, Constants.USER_POST_VOTE_TABLE, aux.getId());
        }

        // Check propositionUserVotes
        for (PropositionUserVote row : propositionUserVoteRepository.findAll()) {
            aux2 = kbDump.dumpSingleUserPropositionVoteRow(row);
            logger.debug("Check propositionUserVote " + aux2.getId());
            checkElasticsearchIntegrity(aux2, Constants.USER_PROPOSITION_VOTE_TABLE, aux2.getId());
        }

    }

    /**
     * Check source integrity
     * @param source source
     * @param index index
     * @param id id
     * @param <T> source class
     */
    private <T> void checkElasticsearchIntegrity(T source, String index, String id) {
        Map<String, Object> sourceAsMap;
        GetResponse getResponse;
        GetRequest getRequest = new GetRequest(index, Constants.INDEX_TYPE, id);
        try {
            getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            sourceAsMap = getResponse.getSourceAsMap();
            if (sourceAsMap == null) // If index doesn't exist, create
                createIndex(source, index, id);
            // else // If index exists, update
            //  updateIndex(source, index, id);
        } catch (IOException | NullPointerException | ElasticsearchException e){
            logger.error("Error checking integrity of source: " + source.toString() + "\n" + e.getLocalizedMessage());
        }
    }

    /**
     * Create ES index
     * @param source source
     * @param index index
     * @param id id
     * @param <T> source class
     */
    private <T> void createIndex(T source, String index, String id) {
        IndexRequest indexRequest = new IndexRequest(index, Constants.INDEX_TYPE, id);
        try {
            indexRequest.source(mapper.writeValueAsString(source), XContentType.JSON);
        } catch (JsonProcessingException e) {
            logger.error("Error creating index request of source: " + source.toString() + "\n" + e.getLocalizedMessage());
        }
        try {
            client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("Error creating index source: " + source.toString() + "\n" + e.getLocalizedMessage());
        }
    }

    /**
     * Update ES index
     * @param source source
     * @param index index
     * @param id id
     * @param <T> source class
     */
    private <T> void updateIndex(T source, String index, String id) {
        UpdateRequest updateRequest = new UpdateRequest(index, Constants.INDEX_TYPE, id);
        try {
            updateRequest.doc(mapper.writeValueAsString(source), XContentType.JSON);
        } catch (JsonProcessingException e) {
            logger.error("Error updating index request of source: " + source.toString() + "\n" + e.getLocalizedMessage());
        }
        try {
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("Error updating index source: " + source.toString() + "\n" + e.getLocalizedMessage());
        }
    }
}
