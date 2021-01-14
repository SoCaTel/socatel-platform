package com.socatel.utils;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

public class Constants {
    public static final int VERIFICATION_TOKEN_EXPIRATION_MINUTES = 60 * 24; // One day
    public static final int DEFAULT_PICTURES_NUMBER = 16;
    public static final String PLATFORM_HOST = "{url_host}";

    // Pagination
    public static final int DASHBOARD_NOTIFICATION_PAGE_SIZE = 5;
    public static final int DASHBOARD_MESSAGE_PAGE_SIZE = 5;
    public static final int DASHBOARD_TOPIC_PAGE_SIZE = 3;
    public static final int DASHBOARD_HISTORY_PAGE_SIZE = 6;
    public static final int TOPIC_CREATE_PAGE_SIZE = 5;
    public static final int TOPIC_SUBSCRIBE_PAGE_SIZE = 5;
    public static final int NOTIFICATION_PAGE_SIZE = 5;
    public static final int HISTORY_PAGE_SIZE = 6;
    public static final int MESSAGES_PAGE_SIZE = 100;
    public static final int POST_PAGE_SIZE = 5;
    public static final int TOPIC_FIND_PAGE_SIZE = 6;
    public static final int TOPIC_POPULAR_COMMENTS_PAGE_SIZE = 5;
    public static final int TOPIC_REPLIES_PAGE_SIZE = 5;
    public static final int MODERATOR_BANNED_USERS_PAGE_SIZE = 5;
    public static final int MODERATOR_REPORTS_PAGE_SIZE = 5;
    public static final int MODERATOR_SUGGESTED_TOPICS_PAGE_SIZE = 200;
    public static final int MODERATOR_TOPICS_PAGE_SIZE = 200;
    public static final int MODERATOR_SUGGESTED_SERVICES_PAGE_SIZE = 10;

    // Sorting
    public static final Sort SORT_BY_PIN = new Sort(Sort.Direction.DESC, "pin");
    public static final Sort SORT_BY_USERNAME = new Sort(Sort.Direction.ASC, "username");
    public static final Sort SORT_BY_TIMESTAMP_DESC = new Sort(Sort.Direction.DESC, "timestamp");
    public static final Sort SORT_BY_TIMESTAMP_ASC = new Sort(Sort.Direction.ASC, "timestamp");
    public static final Sort SORT_BY_LIKES_DESC = new Sort(Sort.Direction.DESC, "likes"); // more popular
    public static final Sort SORT_BY_LIKES_ASC = new Sort(Sort.Direction.ASC, "likes"); // less popular
    public static final Sort SORT_BY_UPVOTES_DESC = new Sort(Sort.Direction.DESC, "upvotes"); // more upvotes
    public static final Sort SORT_BY_UPVOTES_ASC = new Sort(Sort.Direction.ASC, "upvotes"); // less upvotes
    public static final Sort SORT_BY_DOWNVOTES_DESC = new Sort(Sort.Direction.DESC, "downvotes"); // more downvotes
    public static final Sort SORT_BY_DOWNVOTES_ASC = new Sort(Sort.Direction.ASC, "downvotes"); // less downvotes

    // Encryption
    private static final String SALT = "{your_salt}"; // 8-byte salt that is then hex-encoded
    private static final String PASSWORD = "{your_password}"; // generates a random 16-byte password
    public static final TextEncryptor te =
            Encryptors.queryableText(Constants.PASSWORD, Constants.SALT); // queryableText for deterministic encryption
    public static final String NOISE_BEFORE = "{your_noise_before}"; // must be constant to be deterministic
    public static final String NOISE_AFTER = "{your_noise_after}"; // must be constant to be deterministic

    // Elasticsearch
    public static final String USER_TABLE = "so_user";
    public static final String GROUP_TABLE = "so_group";
    public static final String ORGANISATION_TABLE = "so_organisation";
    public static final String HISTORY_TABLE = "so_history";
    public static final String POST_TABLE = "so_post";
    public static final String PROPOSITION_TABLE = "so_proposition";
    public static final String SERVICE_TABLE = "so_service";
    public static final String USER_POST_VOTE_TABLE = "so_user_post_vote";
    public static final String USER_PROPOSITION_VOTE_TABLE = "so_proposition_user_vote";
    public static final String LOCALITY_TABLE = "so_locality";
    public static final String LANGUAGE_TABLE = "so_language";
    public static final String SKILL_TABLE = "so_skill";
    public static final String THEME_TABLE = "so_theme";
    public static final String INDEX_TYPE = "_doc";

    // Cloud storage
    public static final String CLOUD_GCP_STORAGE_BUCKET_NAME = "{gcp_bucket_name}";
    public static final String CLOUD_GCP_PROJECT_ID = "{gcp_project_id}";
    public static final String CLOUD_GCP_CREDENTIALS_PATH = "keys/storage-key.json";

    // Twitter Handler
    public static final String TWITTER_CONSUMER_KEY = "{twitter_consumer}";
    public static final String TWITTER_CONSUMER_SECRET = "{twitter_secret}";

    /* Rest API */
    // Credentials
    public static final String REST_API_USER = "{your_user}";
    public static final String REST_API_PASSWORD = "{your_password}";
    private static final String REST_API_HOST = "{your_host}";
    private static final String REST_API_HOST_REC = "{your_host_2}";
    // SDI
    public static final String REST_API_TWEETS_BY_TOPICS = REST_API_HOST + "graphql_twSearchByTopics";
    public static final String REST_API_POSTS_BY_TOPICS = REST_API_HOST + "graphql_multiplePostsByTopics";
    public static final String REST_API_POST_BY_ID = REST_API_HOST + "qraphql_postsById/"; // + {post_identifier}
    public static final String REST_API_SERVICES_BY_TOPICS = REST_API_HOST + "graphql_servicesByTopics/0/";
    // RDI
    public static final String REST_API_SERVICES_BY_KEYWORDS = REST_API_HOST +
            "elasticsearch_getServicesByKeywords/";
    public static final String REST_API_MOST_VIEWED_AND_JOINED_GROUPS = REST_API_HOST +
            "elasticsearch_getMostViewedAndJoinedGroups/undefined/undefined";
    public static final String REST_API_MOST_POSTED_IN_GROUPS = REST_API_HOST +
            "elasticsearch_getMostPostedInGroups/undefined/undefined";
    public static final String REST_API_MOST_TRENDING_GROUPS = REST_API_HOST +
            "elasticsearch_getMostTrendingGroups/undefined/undefined";
    public static final String REST_API_GROUP_DEMOGRAPHICS = REST_API_HOST +
            "elasticsearch_getUserDemographicsGivenGroup/"; // + /{group_id}
    public static final String REST_API_THEMES_POPULARITY = REST_API_HOST +
            "elasticsearch_getGroupThemesPopularity"; // + /{size}
    public static final String REST_API_POPULAR_GROUPS_BY_LANGUAGE = REST_API_HOST +
            "elasticsearch_getMostPopularGroupsByPopRatioLanguageFiltered/undefined/undefined/"; // + {language_id}
    public static final String REST_API_POPULAR_GROUPS_BY_LOCALITY = REST_API_HOST +
            "elasticsearch_getMostPopularGroupsByPopRatioLocalityFiltered/undefined/undefined/"; // + {locality_id}
    public static final String REST_API_POPULAR_GROUPS_BY_THEMES = REST_API_HOST +
            "elasticsearch_getMostPopularGroupsByPopRatioThemesFiltered/undefined/undefined/";
    public static final String REST_API_POPULAR_GROUPS_BY_SKILLS = REST_API_HOST +
            "elasticsearch_getMostGroupsByPopRatioSkillsFiltered/";
    public static final String REST_API_MOST_ACTIVE_USERS_IN_GROUP = REST_API_HOST +
            "elasticsearch_getMostActiveUsersInGroup/"; // + {group_id}/{size}
    public static final String REST_API_POPULAR_WORDS_IN_GROUP = REST_API_HOST +
            "elasticsearch_getMostPopularWordsGivenGroup/"; // + {group_id}/{size}
    public static final String REST_API_GROUPS_BY_KEYWORDS = REST_API_HOST +
            "elasticsearch_getGroupsByKeywords"; // + size
    // DAI

    // DSE
    public static final String REST_API_INSERT_USER = REST_API_HOST + "insert_new_socatel_user";
    public static final String REST_API_INSERT_ORGANISATION = REST_API_HOST + "insert_new_socatel_organisation";
    public static final String REST_API_INSERT_GROUP = REST_API_HOST + "insert_new_socatel_group";
    public static final String REST_API_INSERT_SERVICE = REST_API_HOST + "insert_new_socatel_service";

    // REC
    public static final String REST_API_SIMILAR_GROUPS_TO_GROUP = REST_API_HOST_REC +
            "recommendation_getSimilarGroupsToGroup/"; // + {group_id}/{size}
    public static final String REST_API_SIMILAR_SERVICES_TO_SERVICE = REST_API_HOST_REC +
            "recommendation_getSimilarServicesToService/"; // + {service_id}/{size}
    public static final String REST_API_SIMILAR_SERVICES_TO_GROUP = REST_API_HOST_REC +
            "recommendation_getSimilarServicesToGroup/"; // + {group_id}/{size}
    public static final String REST_API_GROUP_GIVEN_USER = REST_API_HOST_REC +
            "recommendation_getGroupGivenUser/"; // + {encrypted_user_id}/{size}
    public static final String REST_API_SERVICE_GIVEN_USER = REST_API_HOST_REC +
            "recommendation_getServiceGivenUser/"; // + {encrypted_user_id}/{size}

    // SIZES
    public static final String REST_API_SIZE_4 = "/4"; // = {size}
    public static final String REST_API_SIZE_5 = "/5"; // = {size}

}
