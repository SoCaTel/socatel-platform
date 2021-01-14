package com.socatel.rest_api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socatel.components.Methods;
import com.socatel.knowledge_base_dump.KBDump;
import com.socatel.models.*;
import com.socatel.repositories.GroupRepository;
import com.socatel.repositories.ServiceRepository;
import com.socatel.repositories.ThemeRepository;
import com.socatel.rest_api.rdi.*;
import com.socatel.rest_api.rdi.demographic.GroupDemographic;
import com.socatel.rest_api.rec.GroupScore;
import com.socatel.rest_api.rec.SimilarGroup;
import com.socatel.rest_api.rec.SimilarService;
import com.socatel.rest_api.sdi.external_data.ExternalDataList;
import com.socatel.rest_api.sdi.external_service.ServicesList;
import com.socatel.rest_api.sdi.tweet.Tweet;
import com.socatel.rest_api.sdi.tweet.TweetsList;
import com.socatel.utils.Constants;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class RestAPICaller {

    private RestTemplate restTemplate;
    private List<GroupViewsJoins> groupViewsJoinsList = new LinkedList<>(); // Class reference
    private List<GroupPosts> groupPostsList = new LinkedList<>(); // Class reference
    private List<GroupTrending> groupTrendingList = new LinkedList<>(); // Class reference
    private List<GroupDemographic> groupDemographicList = new LinkedList<>(); // Class reference
    private List<GroupCategorisation> groupCategorisationList = new LinkedList<>(); // Class reference
    private List<GroupPopularity> groupPopularityList = new LinkedList<>(); // Class reference
    private List<SimilarGroup> similarG2GList = new LinkedList<>(); // Class reference
    private List<SimilarService> similarS2GList = new LinkedList<>(); // Class reference
    private List<GroupScore> groupScoreList = new LinkedList<>(); // Class reference
    private List<UserActivity> userActivityList = new LinkedList<>(); // Class reference
    private List<ServiceByKeywords> serviceByKeywordsList = new LinkedList<>(); // Class reference

    @Autowired private KBDump kbDump;
    @Autowired private GroupRepository groupRepository;
    @Autowired private ThemeRepository themeRepository;
    @Autowired private ServiceRepository serviceRepository;

    private Logger logger = LoggerFactory.getLogger(RestAPICaller.class);
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public RestAPICaller() {
        restTemplate = new RestTemplate(getClientHttpRequestFactory());
    }

    /*public boolean isAPIHostUp() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("http://vps719087.ovh.net", 8192), 2000);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    public boolean isAPIRecHostUp() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("http://51.68.126.254", 8192), 2000);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }*/

    // SDI

    public ResponseEntity<TweetsList> searchTweetsByTopics(Set<Theme> themes, String language) {
        ResponseEntity<TweetsList> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_TWEETS_BY_TOPICS, HttpMethod.POST,
                    new HttpEntity<>(generateTopicBody(themes, language), createHeaders()), TweetsList.class);
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public CompletableFuture<ResponseEntity<ExternalDataList>> futureSearchPostsByTopics(Set<Theme> themes, String language) {
        CompletableFuture<ResponseEntity<ExternalDataList>> result;
        try {
            result = CompletableFuture.completedFuture(restTemplate.exchange(Constants.REST_API_POSTS_BY_TOPICS, HttpMethod.POST,
                    new HttpEntity<>(generateTopicBody(themes, language), createHeaders()), ExternalDataList.class));
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public CompletableFuture<ResponseEntity<ServicesList>> futureSearchServicesByTopics(String topic, String language, int num) {
        CompletableFuture<ResponseEntity<ServicesList>> result;
        try {
            result = CompletableFuture.completedFuture(restTemplate.exchange(Constants.REST_API_SERVICES_BY_TOPICS + num, HttpMethod.POST,
                    new HttpEntity<>(generateTopicBody(topic, language), createHeaders()), ServicesList.class));
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    // RDI

    public CompletableFuture<ResponseEntity<List<ServiceByKeywords>>> futureSearchServicesByKeywords(List<String> keywords, String language, int num) {
        CompletableFuture<ResponseEntity<List<ServiceByKeywords>>> result;
        try {
            result = CompletableFuture.completedFuture(restTemplate.exchange(Constants.REST_API_SERVICES_BY_KEYWORDS + num, HttpMethod.POST,
                    new HttpEntity<>(generateKeywordsBody(keywords, language, null), createHeaders()), (Class<List<ServiceByKeywords>>)serviceByKeywordsList.getClass()));
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<GroupViewsJoins>> searchMostViewedAndJoinedGroups() {
        ResponseEntity<List<GroupViewsJoins>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_MOST_VIEWED_AND_JOINED_GROUPS + "/100", HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<GroupViewsJoins>>)groupViewsJoinsList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<GroupPosts>> searchMostPostedInGroups() {
        ResponseEntity<List<GroupPosts>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_MOST_POSTED_IN_GROUPS + "/100", HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<GroupPosts>>)groupPostsList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<GroupTrending>> searchMostTrendingGroups() {
        ResponseEntity<List<GroupTrending>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_MOST_TRENDING_GROUPS, HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<GroupTrending>>)groupTrendingList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<GroupDemographic>> searchGroupDemographics(Integer groupId) {
        ResponseEntity<List<GroupDemographic>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_GROUP_DEMOGRAPHICS + groupId, HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<GroupDemographic>>)groupDemographicList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<GroupCategorisation>> searchThemesPopularity() {
        ResponseEntity<List<GroupCategorisation>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_THEMES_POPULARITY + Constants.REST_API_SIZE_5, HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<GroupCategorisation>>)groupCategorisationList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<GroupPopularity>> searchPopularGroupsByLanguage(Integer id) {
        ResponseEntity<List<GroupPopularity>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_POPULAR_GROUPS_BY_LANGUAGE + id, HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<GroupPopularity>>)groupPopularityList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<GroupPopularity>> searchPopularGroupsByLocality(Integer id) {
        ResponseEntity<List<GroupPopularity>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_POPULAR_GROUPS_BY_LOCALITY + id, HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<GroupPopularity>>)groupPopularityList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<GroupPopularity>> searchPopularGroupsByThemes(Set<Theme> themes, Integer localityId) {
        ResponseEntity<List<GroupPopularity>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_POPULAR_GROUPS_BY_THEMES + localityId, HttpMethod.POST,
                    new HttpEntity<>(generateThemeBody(themes), createHeaders()), (Class<List<GroupPopularity>>)groupPopularityList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<GroupPopularity>> searchPopularGroupsBySkills(Set<Skill> skills, Integer localityId) {
        ResponseEntity<List<GroupPopularity>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_POPULAR_GROUPS_BY_SKILLS + localityId, HttpMethod.POST,
                    new HttpEntity<>(generateSkillBody(skills), createHeaders()), (Class<List<GroupPopularity>>)groupPopularityList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<UserActivity>> searchMostActiveUsersInGroup(Integer id) {
        ResponseEntity<List<UserActivity>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_MOST_ACTIVE_USERS_IN_GROUP + id +  Constants.REST_API_SIZE_5, HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<UserActivity>>)userActivityList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    // TODO: REST_API_POPULAR_WORDS_IN_GROUP

    public ResponseEntity<List<GroupScore>> searchGroupsGivenKeyword(List<String> keywords, String language, Integer localityId) {
        ResponseEntity<List<GroupScore>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_GROUPS_BY_KEYWORDS + "/100", HttpMethod.POST,
                    new HttpEntity<>(generateKeywordsBody(keywords, language, localityId), createHeaders()), (Class<List<GroupScore>>) groupScoreList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    // DSE

    public ResponseEntity insertUser(User user) {
        ResponseEntity result;
        try {
            result = restTemplate.exchange(Constants.REST_API_INSERT_USER, HttpMethod.POST,
                    new HttpEntity<>(mapper.writeValueAsString(kbDump.dumpSingleUserRow(user)), createHeaders()), Object.class);
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity insertGroup(Group group) {
        ResponseEntity result;
        try {
            result = restTemplate.exchange(Constants.REST_API_INSERT_GROUP, HttpMethod.POST,
                    new HttpEntity<>(mapper.writeValueAsString(kbDump.dumpSingleGroupRow(group)), createHeaders()), Object.class);
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity insertOrganisation(Organisation organisation) {
        ResponseEntity result;
        try {
            result = restTemplate.exchange(Constants.REST_API_INSERT_ORGANISATION, HttpMethod.POST,
                    new HttpEntity<>(mapper.writeValueAsString(kbDump.dumpSingleOrganisationRow(organisation)), createHeaders()), Object.class);
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity insertService(Service service) {
        ResponseEntity result;
        try {
            result = restTemplate.exchange(Constants.REST_API_INSERT_SERVICE, HttpMethod.POST,
                    new HttpEntity<>(mapper.writeValueAsString(kbDump.dumpSingleServiceRow(service)), createHeaders()), Object.class);
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    // REC

    public ResponseEntity<List<SimilarGroup>> searchSimilarGroupsToGroup(Integer id, Integer localityId) {
        ResponseEntity<List<SimilarGroup>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_SIMILAR_GROUPS_TO_GROUP + id + "/100" + "/" + localityId, HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<SimilarGroup>>) similarG2GList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<SimilarService>> searchSimilarServicesToService(Integer id) {
        ResponseEntity<List<SimilarService>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_SIMILAR_SERVICES_TO_SERVICE + id + Constants.REST_API_SIZE_4, HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<SimilarService>>) similarS2GList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<SimilarService>> searchSimilarServicesToGroup(Integer id) {
        ResponseEntity<List<SimilarService>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_SIMILAR_SERVICES_TO_GROUP + id + Constants.REST_API_SIZE_4, HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<SimilarService>>) similarS2GList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<GroupScore>> searchGroupsGivenUserId(Integer userId, Integer localityId) {
        ResponseEntity<List<GroupScore>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_GROUP_GIVEN_USER + Methods.encrypt(userId) + "/100/" + localityId, HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<GroupScore>>) groupScoreList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    public ResponseEntity<List<SimilarService>> searchServicesGivenUserId(Integer userId) {
        ResponseEntity<List<SimilarService>> result;
        try {
            result = restTemplate.exchange(Constants.REST_API_SERVICE_GIVEN_USER + Methods.encrypt(userId) + Constants.REST_API_SIZE_5, HttpMethod.GET,
                    new HttpEntity<>(createHeaders()), (Class<List<SimilarService>>) similarS2GList.getClass());
        } catch (Exception e) {
            result = null;
            logger.error("Knowledge Base error: " + e);
        }
        return result;
    }

    // UTILS

    private List<Group> returnNumber(List<Integer> groupsIds, Integer num) {
        List<Group> groups = groupRepository.findAllById(groupsIds).stream().filter(Group::isInProcessOrCompleted).collect(Collectors.toList());
        int max = groups.size() < num ? groups.size() : num;
        return groups.subList(0, max);
    }

    public List<Group> getMostViewedAndJoinedGroups() {
        ResponseEntity<List<GroupViewsJoins>> responseEntity = searchMostViewedAndJoinedGroups();
        if (isEmptyResponseEntity(responseEntity)) return new LinkedList<>();
        List body = responseEntity.getBody();
        List<GroupViewsJoins> groupViewsJoins = mapper.convertValue(body, new TypeReference<List<GroupViewsJoins>>(){});
        List<Integer> groupsIds = groupViewsJoins.stream().map(GroupViewsJoins::getGroup_id).collect(Collectors.toList());
        return returnNumber(groupsIds, 5);
    }

    public List<Group> getMostPostedInGroups() {
        ResponseEntity<List<GroupPosts>> responseEntity = searchMostPostedInGroups();
        if (isEmptyResponseEntity(responseEntity)) return new LinkedList<>();
        List body = responseEntity.getBody();
        List<GroupPosts> groupPosts = mapper.convertValue(body, new TypeReference<List<GroupPosts>>(){});
        List<Integer> groupsIds = groupPosts.stream().map(GroupPosts::getGroup_id).collect(Collectors.toList());
        return returnNumber(groupsIds, 5);
    }

    public List<Group> getMostTrendingGroups() {
        ResponseEntity<List<GroupTrending>> responseEntity = searchMostTrendingGroups();
        if (isEmptyResponseEntity(responseEntity)) return new LinkedList<>();
        List body = responseEntity.getBody();
        List<GroupTrending> groupTrendings = mapper.convertValue(body, new TypeReference<List<GroupTrending>>(){});
        int max = groupTrendings.size() < 5 ? groupTrendings.size() : 5;
        List<Integer> groupsIds = groupTrendings.subList(0,max).stream().map(GroupTrending::getGroup_id).collect(Collectors.toList());
        return returnNumber(groupsIds, 5);
    }

    public List<Theme> getPopularThemes() {
        ResponseEntity<List<GroupCategorisation>> responseEntity = searchThemesPopularity();
        if (isEmptyResponseEntity(responseEntity)) return new LinkedList<>();
        List body = responseEntity.getBody();
        List<GroupCategorisation> groupCategorisations = mapper.convertValue(body, new TypeReference<List<GroupCategorisation>>(){});
        List<Integer> themesIds = groupCategorisations.stream().map(GroupCategorisation::getTheme_id).collect(Collectors.toList());
        return themeRepository.findAllById(themesIds);
    }

    public List<Group> getGroupsGivenUser(Integer userId, Integer localityId) {
        ResponseEntity<List<GroupScore>> responseEntity = searchGroupsGivenUserId(userId, localityId);
        if (isEmptyResponseEntity(responseEntity)) return new LinkedList<>();
        List body = responseEntity.getBody();
        try {
            List<GroupScore> groupScores = mapper.convertValue(body, new TypeReference<List<GroupScore>>(){});
            List<Integer> groupsIds = groupScores.stream().map(GroupScore::getGroup_id).collect(Collectors.toList());
            return returnNumber(groupsIds, 5);
        } catch (Exception e) {
            return new LinkedList<>();
        }
    }

    public List<Service> getServicesGivenUser(Integer userId) {
        ResponseEntity<List<SimilarService>> responseEntity = searchServicesGivenUserId(userId);
        if (isEmptyResponseEntity(responseEntity)) return new LinkedList<>();
        List body = responseEntity.getBody();
        List<SimilarService> similarServices = mapper.convertValue(body, new TypeReference<List<SimilarService>>(){});
        List<Integer> servicesIds = similarServices.stream().map(SimilarService::getSimilar_service_id).collect(Collectors.toList());
        return serviceRepository.findAllById(servicesIds);
    }

    public List<Group> getGroupsGivenKeyword(List<String> keywords, String language, Integer localityId) {
        ResponseEntity<List<GroupScore>> responseEntity = searchGroupsGivenKeyword(keywords, language, localityId);
        if (isEmptyResponseEntity(responseEntity)) return new LinkedList<>();
        List body = responseEntity.getBody();
        List<GroupScore> groupScores = mapper.convertValue(body, new TypeReference<List<GroupScore>>(){});
        List<Integer> groupsIds = groupScores.stream().map(GroupScore::getGroup_id).collect(Collectors.toList());
        return returnNumber(groupsIds, 5);
    }

    public List<Group> getGroupsGivenKeyword(String keyword, String language, Integer localityId) {
        return getGroupsGivenKeyword(new LinkedList<String>(){{addAll(Arrays.asList(keyword.split(" ")));}}, language, localityId);
    }

    public List<Group> getPopularGroupsByLanguage(Integer languageId) {
        return getTop5GroupsPopularity(searchPopularGroupsByLanguage(languageId));
    }

    public List<Group> getPopularGroupsByLocality(Integer localityId) {
        return getTop5GroupsPopularity(searchPopularGroupsByLocality(localityId));
    }

    public List<Group> getPopularGroupsBySkills(Set<Skill> skills, Integer localityId) {
        return getTop5GroupsPopularity(searchPopularGroupsBySkills(skills, localityId));
    }

    public List<Group> getPopularGroupsByThemes(Set<Theme> themes, Integer localityId) {
        return getTop5GroupsPopularity(searchPopularGroupsByThemes(themes, localityId));
    }

    /**
     * Retrieve the top 5 popular groups from the response entity
     * @param responseEntity response entity
     * @return top 5 popular groups
     */
    private List<Group> getTop5GroupsPopularity(ResponseEntity<List<GroupPopularity>> responseEntity) {
        if (isEmptyResponseEntity(responseEntity)) return new LinkedList<>();
        List<GroupPopularity> body = responseEntity.getBody();
        List<GroupPopularity> groupPopularities = mapper.convertValue(body, new TypeReference<List<GroupPopularity>>(){});
        //int max = groupPopularities.size() < 5 ? groupPopularities.size() : 5;
        //List<Integer> groupsIds = groupPopularities.subList(0,max).stream().map(GroupPopularity::getGroup_id).collect(Collectors.toList());
        //return groupRepository.findAllById(groupsIds);
        List<Integer> groupsIds = groupPopularities.stream().map(GroupPopularity::getGroup_id).collect(Collectors.toList());
        List<Group> groups = groupRepository.findAllById(groupsIds).stream().filter(Group::isInProcessOrCompleted).collect(Collectors.toList());
        int max = groups.size() < 5 ? groups.size() : 5;
        return groups.subList(0, max);
    }

    public List<Tweet> retrieveTweets(Set<Theme> themes, String language) {
        ResponseEntity<TweetsList> responseEntity = searchTweetsByTopics(themes, language);
        try {
            if (responseEntity.getStatusCode().value() == 200 && responseEntity.getBody() != null)
                return responseEntity.getBody().getPostsByTopics();
        } catch (NullPointerException e) {
            logger.error("Knowledge Base error: " + e);
        }
        return new LinkedList<>();
    }

    public List<Service> retrieveServices(Integer id, Boolean group) {
        ResponseEntity<List<SimilarService>> responseEntity;
        if (group)
            responseEntity = searchSimilarServicesToGroup(id);
        else responseEntity = searchSimilarServicesToService(id);
        try {
            if (responseEntity.getStatusCode().value() == 200 && responseEntity.getBody() != null && !responseEntity.getBody().isEmpty()) {
                List<SimilarService> similarServices = mapper.convertValue(responseEntity.getBody(), new TypeReference<List<SimilarService>>(){});
                List<Integer> servicesIds = similarServices.stream().map(SimilarService::getSimilar_service_id).collect(Collectors.toList());
                return serviceRepository.findAllById(servicesIds);
            }
        } catch (NullPointerException e) {
            logger.error("Knowledge Base error: " + e);
        }
        return new LinkedList<>();
    }

    public List<Group> retrieveGroupsToGroup(Integer id, Integer localityId) {
        ResponseEntity<List<SimilarGroup>> responseEntity = searchSimilarGroupsToGroup(id, localityId);
        try {
            if (responseEntity.getStatusCode().value() == 200 && responseEntity.getBody() != null && !responseEntity.getBody().isEmpty()) {
                List<SimilarGroup> similarGroups = mapper.convertValue(responseEntity.getBody(), new TypeReference<List<SimilarGroup>>(){});
                List<Integer> groupsIds = similarGroups.stream().map(SimilarGroup::getSimilar_group_id).collect(Collectors.toList());
                return returnNumber(groupsIds, 4);
            }
        } catch (NullPointerException e) {
            logger.error("Knowledge Base error: " + e);
        }
        return new LinkedList<>();
    }

    private <T> boolean isEmptyResponseEntity(ResponseEntity<List<T>> responseEntity) {
        return responseEntity == null || responseEntity.getBody() == null || responseEntity.getBody().isEmpty();
    }

    // CREDENTIALS

    private HttpHeaders createHeaders() {
        return new HttpHeaders() {{
            setBasicAuth(Constants.REST_API_USER, Constants.REST_API_PASSWORD, Charset.forName("UTF-8"));
            MediaType mediaType = new MediaType("application", "json");
            List<MediaType> acceptableMediaTypes = new ArrayList<>();
            acceptableMediaTypes.add(mediaType);
            setAccept(acceptableMediaTypes);
            setContentType(mediaType);
        }};
    }

    private Map<String, Object> generateTopicBody(Set<Theme> themes, String language) {
        Map<String, Object> body = new HashMap<>();
        List<String> bodyThemes = new LinkedList<>();
        for (Theme theme : themes) {
            bodyThemes.add(theme.getName());
        }
        body.put("topics", bodyThemes);
        body.put("language", language);
        return body;
    }

    private Map<String, Object> generateTopicBody(String topic, String language) {
        Map<String, Object> body = new HashMap<>();
        List<String> topicBody = new LinkedList<>();
        topicBody.add(topic);
        body.put("topics", topicBody);
        body.put("language", language);
        return body;
    }

    private Map<String, Object> generateThemeBody(Set<Theme> themes) {
        Map<String, Object> body = new HashMap<>();
        List<String> bodyThemes = new LinkedList<>();
        for (Theme theme : themes) {
            bodyThemes.add(theme.getName());
        }
        body.put("themes", bodyThemes);
        return body;
    }

    private Map<String, Object> generateSkillBody(Set<Skill> skills) {
        Map<String, Object> body = new HashMap<>();
        List<String> bodySkills = new LinkedList<>();
        for (Skill skill : skills) {
            bodySkills.add(skill.getName());
        }
        body.put("skills", bodySkills);
        return body;
    }

    private Map<String, Object> generateKeywordsBody(List<String> keywords, String language, Integer localityId) {
        Map<String, Object> body = new HashMap<>();
        body.put("keywords", keywords);
        body.put("language", language);
        if (localityId != null)
            body.put("locality_id", localityId);
        return body;
    }

    private HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient());
        clientHttpRequestFactory.setConnectTimeout(1000);
        clientHttpRequestFactory.setConnectionRequestTimeout(1000);
        clientHttpRequestFactory.setReadTimeout(1000);
        return clientHttpRequestFactory;
    }

    private HttpClient httpClient() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(Constants.REST_API_USER, Constants.REST_API_PASSWORD));
        return HttpClientBuilder
                .create()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }
}
