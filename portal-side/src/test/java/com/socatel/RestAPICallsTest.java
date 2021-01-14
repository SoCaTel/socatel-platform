package com.socatel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socatel.components.Methods;
import com.socatel.models.*;
import com.socatel.repositories.*;
import com.socatel.rest_api.RestAPICaller;
import com.socatel.rest_api.rdi.*;
import com.socatel.rest_api.rdi.demographic.GroupDemographic;
import com.socatel.rest_api.rec.GroupScore;
import com.socatel.rest_api.rec.SimilarGroup;
import com.socatel.rest_api.rec.SimilarService;
import com.socatel.rest_api.sdi.external_data.ExternalDataList;
import com.socatel.rest_api.sdi.external_service.ServicesList;
import com.socatel.rest_api.sdi.tweet.TweetsList;
import com.socatel.utils.enums.AgeEnum;
import com.socatel.utils.enums.ProfileEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestAPICallsTest {
    @Autowired private RestAPICaller restAPICaller;
    @Autowired private ThemeRepository themeRepository;
    @Autowired private SkillRepository skillRepository;
    @Autowired private GroupRepository groupRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private LocalityRepository localityRepository;
    @Autowired private LanguageRepository languageRepository;
    private ObjectMapper mapper = new ObjectMapper();

    private Set<Theme> generateThemes() {
        return new HashSet<Theme>(){{
            add(themeRepository.findById(1).get());
        }};
    }

    private Set<Skill> generateSkills() {
        return new HashSet<Skill>(){{
            add(skillRepository.findById(1).get());
        }};
    }

    private List<String> generateKeywords() {
        return new LinkedList<String>() {{
            add("food");
        }};
    }

    private User mockUser() {
        User user = new User();
        user.setId(0);
        user.setUsername("Test");
        user.setPassword("password");
        user.setProfile(ProfileEnum.SERVICE_PROVIDER);
        user.setLocality(localityRepository.findById(1004).get());
        user.setParentLocality(user.getLocality().getLocalityParent());
        user.setFirstLang(languageRepository.findByCode("en").get());
        user.setAgeRange(AgeEnum.BETWEEN_19_25);
        user.setEmail("test@email.com");
        return user;
    }

    // SDI

    @Test
    public void searchTweetsByThemes() {
        Set<Theme> themes = generateThemes();

        ResponseEntity<TweetsList> response = null;

        response = restAPICaller.searchTweetsByTopics(themes, "en");


        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());
        Objects.requireNonNull(response.getBody()).getPostsByTopics().forEach(System.out::println);
    }

    @Test
    public void searchPostsIdsByThemes() {
        Set<Theme> themes = generateThemes();

        ResponseEntity<ExternalDataList> response = null;
        try {
            response = restAPICaller.futureSearchPostsByTopics(themes, "en").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());
        Objects.requireNonNull(response.getBody()).getSearchByTopics().forEach(System.out::println);
    }

    @Test
    public void searchServicesByTopic() {
        ResponseEntity<ServicesList> response = null;
        try {
            response = restAPICaller.futureSearchServicesByTopics("healthcare", "es", 4).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());
        Objects.requireNonNull(response.getBody()).getServicesByTopics().forEach(System.out::println);
        List<Integer> servicesIds = Objects.requireNonNull(response.getBody()).getServicesByTopics().stream().map(x -> Integer.valueOf(x.getIdentifier())).collect(Collectors.toList());
        List<Service> services = serviceRepository.findAllById(servicesIds);
    }

    // RDI

    @Test
    public void searchMostViewedAndJoinedGroups() {
        ResponseEntity<List<GroupViewsJoins>> response = restAPICaller.searchMostViewedAndJoinedGroups();

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Group> groups;
        if (!response.getBody().isEmpty()) {
            List<GroupViewsJoins> groupViewsJoins = mapper.convertValue(response.getBody(), new TypeReference<List<GroupViewsJoins>>(){});
            List<Integer> groupsIds = groupViewsJoins.stream().map(GroupViewsJoins::getGroup_id).collect(Collectors.toList());
            groups = groupRepository.findAllById(groupsIds);
        }
    }

    @Test
    public void searchMostPostedInGroups() {
        ResponseEntity<List<GroupPosts>> response = restAPICaller.searchMostPostedInGroups();

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Group> groups;
        if (!response.getBody().isEmpty()) {
            List<GroupPosts> groupViewsJoins = mapper.convertValue(response.getBody(), new TypeReference<List<GroupPosts>>(){});
            List<Integer> groupsIds = groupViewsJoins.stream().map(GroupPosts::getGroup_id).collect(Collectors.toList());
            groups = groupRepository.findAllById(groupsIds);
        }
    }

    @Test
    public void searchGroupTrending() {
        ResponseEntity<List<GroupTrending>> response = restAPICaller.searchMostTrendingGroups();

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Group> groups;
        if (!response.getBody().isEmpty()) {
            List<GroupTrending> groupTrendings = mapper.convertValue(response.getBody(), new TypeReference<List<GroupTrending>>(){});
            List<Integer> groupsIds = groupTrendings.stream().map(GroupTrending::getGroup_id).collect(Collectors.toList());
            groups = groupRepository.findAllById(groupsIds);
        }
    }

    @Test
    public void searchGroupDemographics() {
        ResponseEntity<List<GroupDemographic>> response = restAPICaller.searchGroupDemographics(3);

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Group> groups;
        if (!response.getBody().isEmpty()) {
            List<GroupDemographic> groupTrendings = mapper.convertValue(response.getBody(), new TypeReference<List<GroupDemographic>>(){});
        }
    }

    @Test
    public void searchGroupCategorisation() {
        ResponseEntity<List<GroupCategorisation>> response = restAPICaller.searchThemesPopularity();

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Theme> themes;
        if (!response.getBody().isEmpty()) {
            List<GroupCategorisation> groupCategorisations = mapper.convertValue(response.getBody(), new TypeReference<List<GroupCategorisation>>(){});
            List<Integer> themesIds = groupCategorisations.stream().map(GroupCategorisation::getTheme_id).collect(Collectors.toList());
            themes = themeRepository.findAllById(themesIds);
        }
    }

    @Test
    public void searchPopularGroupsByLanguage() {
        ResponseEntity<List<GroupPopularity>> response = restAPICaller.searchPopularGroupsByLanguage(1);

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Group> groups = getTop5GroupsPopularity(response.getBody());
    }

    @Test
    public void searchPopularGroupsByLocality() {
        ResponseEntity<List<GroupPopularity>> response = restAPICaller.searchPopularGroupsByLocality(999);

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Group> groups = getTop5GroupsPopularity(response.getBody());
    }

    @Test
    public void searchPopularGroupsByThemes() {
        ResponseEntity<List<GroupPopularity>> response = restAPICaller.searchPopularGroupsByThemes(generateThemes(), 999);

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Group> groups = getTop5GroupsPopularity(response.getBody());
    }

    @Test
    public void searchPopularGroupsBySkills() {
        ResponseEntity<List<GroupPopularity>> response = restAPICaller.searchPopularGroupsBySkills(generateSkills(), 1004);

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Group> groups = getTop5GroupsPopularity(response.getBody());
    }

    private List<Group> getTop5GroupsPopularity(List<GroupPopularity> body) {
        if (body.isEmpty()) return new LinkedList<>();
        List<GroupPopularity> groupPopularities = mapper.convertValue(body, new TypeReference<List<GroupPopularity>>(){});
        int max = groupPopularities.size() < 5 ? groupPopularities.size() : 5;
        List<Integer> groupsIds = groupPopularities.subList(0,max).stream().map(GroupPopularity::getGroup_id).collect(Collectors.toList());
        return groupRepository.findAllById(groupsIds);
    }

    @Test
    public void searchMostActiveUsersInGroup() {
        ResponseEntity<List<UserActivity>> response = null;
        try {
            response = restAPICaller.searchMostActiveUsersInGroup(1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<User> users;
        if (!response.getBody().isEmpty()) {
            List<UserActivity> userActivities = mapper.convertValue(response.getBody(), new TypeReference<List<UserActivity>>(){});
            List<Integer> userIds = userActivities.stream().map(UserActivity::getUser_id).map(Methods::decrypt).collect(Collectors.toList());
            users = userRepository.findAllById(userIds);
        }
    }

    @Test
    public void searchGroupsByKeywords() {
        ResponseEntity<List<GroupScore>> response = null;
        try {
            response = restAPICaller.searchGroupsGivenKeyword(generateKeywords(), "en", 1000);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Group> groups;
        if (!response.getBody().isEmpty()) {
            List<GroupScore> groupScores = mapper.convertValue(response.getBody(), new TypeReference<List<GroupScore>>(){});
            List<Integer> groupsIds = groupScores.stream().map(GroupScore::getGroup_id).collect(Collectors.toList());
            groups = groupRepository.findAllById(groupsIds);
        }
    }

    // DSE

    @Test
    public void insertUser() {
        ResponseEntity response;
        response = restAPICaller.insertUser(mockUser());
        assertThat(response.getStatusCode().value(), is(200));
    }

    // REC

    @Test
    public void searchSimilarGroupsToGroup() {
        ResponseEntity<List<SimilarGroup>> response = null;

        response = restAPICaller.searchSimilarGroupsToGroup(3, 1003);

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Group> groups;
        if (!response.getBody().isEmpty()) {
            List<SimilarGroup> similarGroups = mapper.convertValue(response.getBody(), new TypeReference<List<SimilarGroup>>(){});
            List<Integer> groupsIds = similarGroups.stream().map(SimilarGroup::getSimilar_group_id).collect(Collectors.toList());
            groups = groupRepository.findAllById(groupsIds);
        }

    }

    @Test
    public void searchSimilarServicesToService() {
        ResponseEntity<List<SimilarService>> response = null;

        response = restAPICaller.searchSimilarServicesToService(3);

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Service> services;
        if (!response.getBody().isEmpty()) {
            List<SimilarService> similarServices = mapper.convertValue(response.getBody(), new TypeReference<List<SimilarService>>(){});
            List<Integer> servicesIds = similarServices.stream().map(SimilarService::getSimilar_service_id).collect(Collectors.toList());
            services = serviceRepository.findAllById(servicesIds);
        }
    }

    @Test
    public void searchSimilarServicesToGroup() {
        ResponseEntity<List<SimilarService>> response = null;

        response = restAPICaller.searchSimilarServicesToGroup(3);

        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Service> services;
        if (!response.getBody().isEmpty()) {
            List<SimilarService> similarServices = mapper.convertValue(response.getBody(), new TypeReference<List<SimilarService>>(){});
            List<Integer> servicesIds = similarServices.stream().map(SimilarService::getSimilar_service_id).collect(Collectors.toList());
            services = serviceRepository.findAllById(servicesIds);
        }
    }

    @Test
    public void searchGroupsGivenUser() {
        ResponseEntity<List<GroupScore>> response = null;
        try {
            response = restAPICaller.searchGroupsGivenUserId(3, 1004);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());

        List<Group> groups;
        if (!response.getBody().isEmpty()) {
            List<GroupScore> groupScores = mapper.convertValue(response.getBody(), new TypeReference<List<GroupScore>>(){});
            List<Integer> groupsIds = groupScores.stream().map(GroupScore::getGroup_id).collect(Collectors.toList());
            groups = groupRepository.findAllById(groupsIds);
        }
    }

    @Test
    public void searchServicesGivenUser() {
        ResponseEntity<List<SimilarService>> response = null;
        try {
            response = restAPICaller.searchServicesGivenUserId(3);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertThat(response.getStatusCode().value(), is(200));
        assertNotNull(response.getBody());
        List<Service> services;
        if (!response.getBody().isEmpty()) {
            List<SimilarService> similarServices = mapper.convertValue(response.getBody(), new TypeReference<List<SimilarService>>(){});
            List<Integer> servicesIds = similarServices.stream().map(SimilarService::getSimilar_service_id).collect(Collectors.toList());
            services = serviceRepository.findAllById(servicesIds);
        }
    }

}
