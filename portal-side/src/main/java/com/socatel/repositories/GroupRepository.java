package com.socatel.repositories;

import com.socatel.models.Group;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.GroupStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group,Integer> {
    Optional<Group> findByName(String name);

    @Query("SELECT g FROM Group g LEFT JOIN g.usersRelation u WHERE u.userId = :userId")
    List<Group> findAllByUserId(int userId);

    /*********************
     * MODERATOR QUERIES *
     *********************/

    @Query("SELECT g FROM Group g WHERE " +
            "g.locality.name = :loc_name AND " +
            "g.language.code = :code AND " +
            "g.status = 0")
    Page<Group> findSuggestedGroups(@Param("loc_name") String localityName, @Param("code") String languageCode, Pageable pageable);

    @Query("SELECT g FROM Group g WHERE " +
            "g.locality.name = :loc_name AND " +
            "g.language.code = :code AND " +
            "g.status = 6")
    Page<Group> findRejectedGroups(@Param("loc_name") String localityName, @Param("code") String languageCode, Pageable pageable);

    @Query("SELECT g FROM Group g WHERE " +
            "g.locality.name = :loc_name AND " +
            "g.language.code = :code AND " +
            "g.status in (0, 6)")
    Page<Group> findSuggestedAndRejectedGroups(@Param("loc_name") String localityName, @Param("code") String languageCode, Pageable pageable);

    @Query("SELECT g FROM Group g WHERE " +
            "(g.locality.id = :loc_id OR g.locality.id = :parent_loc_id OR g.locality.id = 999) AND " +
            "(g.language.code = :code OR g.language.code = :second_code) AND " +
            "g.status = :status")
    List<Group> findByStatusAndLocalityAndLanguage(@Param("status") GroupStatusEnum status, @Param("loc_id") Integer localityId, @Param("parent_loc_id") Integer localityParentId, @Param("code") String languageCode, @Param("second_code") String secondLanguageCode);

    @Query("SELECT g FROM Group g WHERE " +
            "(g.locality.id = :loc_id OR g.locality.id = :parent_loc_id OR g.locality.id = 999) AND " +
            "(g.language.code = :code OR g.language.code = :second_code) AND " +
            "g.status in (1, 2, 3, 4, 5) AND " +
            "g.nextStepTimestamp <> null")
    List<Group> findAllInProcess(@Param("loc_id") Integer localityId, @Param("parent_loc_id") Integer localityParentId, @Param("code") String languageCode, @Param("second_code") String secondLanguageCode);

    @Query("SELECT g FROM Group g WHERE " +
            "g.status in (1, 2, 3, 4, 5) ")
    List<Group> findRecentAvailableGroups(Pageable pageable);

    default List<Group> findTop4RecentGroups() {
        return findRecentAvailableGroups(PageRequest.of(0, 4, Constants.SORT_BY_TIMESTAMP_DESC));
    }

    /*********************
     * ANONYMOUS QUERIES *
     *********************/

    @Query("SELECT g FROM Group g WHERE " +
            "g.language.code = :code AND " +
            "g.status not in (0, 6)")
    List<Group> findAllByLanguage_Code(@Param("code") String languageCode, Sort sort);

    @Query("SELECT g FROM Group g WHERE " +
            "g.language.code = :code AND " +
            "g.status not in (0, 6) AND " +
            "g.name LIKE :name")
    List<Group> findAllByLanguageAndQuery(@Param("code") String languageCode, @Param("name") String name, Sort sort);

    @Query("SELECT g FROM Group g " +
            "LEFT JOIN g.themes t " +
            "WHERE g.language.code = :code AND " +
            "g.status not in (0, 6) AND " +
            "t.name LIKE :name")
    List<Group> findAllByLanguageAndThemeQuery(@Param("code") String languageCode, @Param("name") String name, Sort sort);

    /*********************
     * LOGGED IN QUERIES *
     *********************/

    @Query("SELECT g FROM Group g WHERE " +
            "(g.locality.id = :loc_id OR g.locality.id = :parent_loc_id OR g.locality.id = 999) AND " +
            "(g.language.code = :code OR g.language.code = :second_code) AND " +
            "g.status not in (0, 6)")
    List<Group> findGroupsByLocalityAndLanguageCode(@Param("loc_id") Integer localityId, @Param("parent_loc_id") Integer localityParentId, @Param("code") String languageCode, @Param("second_code") String secondLanguageCode, Sort sort);

    @Query("SELECT DISTINCT g FROM Group g " +
            "LEFT JOIN g.themes t " +
            "WHERE " +
            "(g.language.code = :code OR g.language.code = :second_code) AND " +
            "(g.locality.id = :loc_id OR g.locality.id = :parent_loc_id OR g.locality.id = 999) AND " +
            "g.status not in (0, 6) AND " +
            "(t.name LIKE :name OR g.name LIKE :name)")
    List<Group> findAllByLocalityAndLanguageAndQuery(@Param("loc_id") Integer localityId, @Param("parent_loc_id") Integer localityParentId, @Param("code") String languageCode, @Param("second_code") String secondLanguageCode, @Param("name") String name, Sort sort);

}
