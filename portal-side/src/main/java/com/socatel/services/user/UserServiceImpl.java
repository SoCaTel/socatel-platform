package com.socatel.services.user;

import com.socatel.models.Document;
import com.socatel.models.Organisation;
import com.socatel.models.User;
import com.socatel.models.tokens.PasswordResetToken;
import com.socatel.models.tokens.VerificationToken;
import com.socatel.publishers.ElasticsearchPublisher;
import com.socatel.repositories.LocalityRepository;
import com.socatel.repositories.PasswordResetTokenRepository;
import com.socatel.repositories.UserRepository;
import com.socatel.repositories.VerificationTokenRepository;
import com.socatel.services.history.HistoryService;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.AnonEnum;
import com.socatel.utils.enums.ESType;
import com.socatel.utils.enums.HistoryTypeEnum;
import com.socatel.utils.enums.VisibleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private UserRepository userRepository;
    private VerificationTokenRepository verificationTokenRepository;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private LocalityRepository localityRepository;
    private ElasticsearchPublisher elasticsearchPublisher;
    private HistoryService historyService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           VerificationTokenRepository verificationTokenRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           LocalityRepository localityRepository,
                           ElasticsearchPublisher elasticsearchPublisher,
                           HistoryService historyService) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.localityRepository = localityRepository;
        this.elasticsearchPublisher = elasticsearchPublisher;
        this.historyService = historyService;
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public Long countAll() {
        return userRepository.count();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<User> getModerators() { return userRepository.findModerators(); }

    @Override
    public List<User> getBannedUsers() {
        return userRepository.findBannedUsers(Constants.SORT_BY_USERNAME);
    }

    @Override
    public List<User> findAllNotAnonymized() {
        return userRepository.findAllByAnonymizedIsFalse();
    }

    @Override
    public List<?> findByUsernameLike(String username, String loggedUsername) {
        return userRepository.findByUsernameLike(username, loggedUsername);
    }

    /*public User findByConfirmationToken(String confirmationToken) {
        return userRepository.findByConfirmationToken(confirmationToken).orElse(null);
    }*/

    @Override
    public void setProfilePicture(User user, Document document) {
        user.setPicture(document);
        userRepository.save(user);
    }

    @Override
    public void save(User user) {
        boolean newUser = user.getId() == 0;
        User saved = userRepository.save(user);
        if (newUser) {
            historyService.createHistory(saved, null, null, "history.created_account", HistoryTypeEnum.CREATED_ACCOUNT, VisibleEnum.VISIBLE);
            elasticsearchPublisher.publishUser(saved, ESType.CREATE);
        }
        else elasticsearchPublisher.publishUser(saved, ESType.UPDATE);
    }

    @Override
    public void anonymise(User user) {
        user.setAnonymized(AnonEnum.ANONYMIZED);
        User saved = userRepository.save(user);
        historyService.createHistory(saved, null, null, "history.deactivate_account", HistoryTypeEnum.DEACTIVATE_ACCOUNT, VisibleEnum.VISIBLE);
    }

    @Override
    public void delete(User user) {
        user.logicalDelete(localityRepository.findPanEuropean());
        User saved = userRepository.save(user);
        elasticsearchPublisher.publishUser(saved, ESType.UPDATE);
        historyService.createHistory(saved, null, null, "history.delete_account", HistoryTypeEnum.DELETE_ACCOUNT, VisibleEnum.VISIBLE);
    }

    @Override
    public User getUser(String verificationToken) {
        return verificationTokenRepository.findByToken(verificationToken).getUser();
    }

    @Override
    public List<User> getOrganisationMembers(Organisation organisation) {
        return userRepository.findByOrganisation(organisation);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String verificationToken) {
        return verificationTokenRepository.findByToken(verificationToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(String verificationToken) {
        VerificationToken token = getVerificationToken(verificationToken);
        if (token == null) return null;
        token.generateNewToken();
        return verificationTokenRepository.save(token);
    }

    @Override
    public void createPasswordResetToken(User user, String token) {
        passwordResetTokenRepository.save(new PasswordResetToken(token, user));
    }

    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(String token, Integer userId) {
        return passwordResetTokenRepository.findByTokenAndUserId(token, userId);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(s); // log with username
        if (!optionalUser.isPresent()) optionalUser = userRepository.findByEmail(s); // log with email
        if (!optionalUser.isPresent()) throw new UsernameNotFoundException(s);
        return optionalUser.get();
    }

}
