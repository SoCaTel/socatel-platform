package com.socatel.controllers;

import com.socatel.components.Methods;
import com.socatel.services.organisation.OrganisationService;
import com.socatel.services.user.UserService;
import com.socatel.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class TwitterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterController.class);
    private final OrganisationService organisationService;
    private final UserService userService;

    public TwitterController(OrganisationService organisationService, UserService userService) {
        this.organisationService = organisationService;
        this.userService = userService;
    }

    @RequestMapping("/twitter_oauth/get_token")
    public void getToken(HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            //get the Twitter object
            Twitter twitter = getTwitter();

            //get the callback url so they get back here
            String callbackUrl = "http://platform.socatel.eu/twitter_oauth/oauth_callback";

            //go get the request token from Twitter
            RequestToken requestToken = twitter.getOAuthRequestToken(callbackUrl);

            //put the token in the session because we'll need it later
            request.getSession().setAttribute("requestToken", requestToken);

            //let's put Twitter in the session as well
            request.getSession().setAttribute("twitter", twitter);

            //now get the authorization URL from the token
            String twitterUrl = requestToken.getAuthorizationURL();

            LOGGER.info("Authorization url is " + twitterUrl);

            response.sendRedirect(twitterUrl);

        } catch (Exception e) {
            LOGGER.error("Problem logging in with Twitter!", e);
            request.getSession().setAttribute("twitter_error", true);
        }

    }

    /**
     * This is where we land when we get back from Twitter
     */
    @RequestMapping("/twitter_oauth/oauth_callback")
    public void twitterCallback(@RequestParam(value="oauth_verifier", required=false) String oauthVerifier,
                                  @RequestParam(value="denied", required=false) String denied,
                                  HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {

        if (denied != null) {
            //if we get here, the user didn't authorize the app
            request.getSession().setAttribute("denied", true);
            response.sendRedirect(request.getContextPath() + "/organisation");
            return;
        }

        Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
        RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
        String verifier = request.getParameter("oauth_verifier");

        try {
            AccessToken token = twitter.getOAuthAccessToken(requestToken, verifier);
            organisationService.setTwitterData(
                    Methods.getLoggedInUser(userService).getOrganisation(),
                    token.getScreenName(),
                    twitter.showUser(token.getUserId()).getDescription(),
                    token.getUserId(),
                    token.getToken(),
                    token.getToken()
            );
            request.getSession().removeAttribute("requestToken");
        } catch (Exception e) {
            request.getSession().setAttribute("twitter_error", true);
        }
        response.sendRedirect(request.getContextPath() + "/organisation");
    }


    /**
     * Display twitter registry
     * @return twitter form
     */
    @RequestMapping(value = {"/twitter_oauth/remove"})
    public String removeTwitterData() {
        organisationService.removeTwitterData(Methods.getLoggedInUser(userService).getOrganisation());
        return "redirect:/organisation";
    }


    /**
     * Instantiates the Twitter object
     */
    public Twitter getTwitter() {
        Twitter twitter;

        //set the consumer key and secret for our app
        String consumerKey = Constants.TWITTER_CONSUMER_KEY;
        String consumerSecret = Constants.TWITTER_CONSUMER_SECRET;

        //build the configuration
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);
        Configuration configuration = builder.build();

        //instantiate the Twitter object with the configuration
        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();

        return twitter;
    }
}
