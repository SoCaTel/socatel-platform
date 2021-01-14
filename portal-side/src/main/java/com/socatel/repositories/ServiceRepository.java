package com.socatel.repositories;

import com.socatel.models.Group;
import com.socatel.models.Organisation;
import com.socatel.models.Service;
import com.socatel.utils.enums.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service,Integer> {
    List<Service> findByOrganisationAndStatus(Organisation organisation, ServiceStatus status);
    @Query("SELECT s FROM Service s WHERE " +
            "(s.locality.id = :loc_id OR s.locality.id = :parent_loc_id OR s.locality.id = 999) AND " +
            "(s.language.code = :code OR s.language.code = :second_code) AND " +
            "s.status = :status")
    List<Service> findAllByStatus(@Param("status") ServiceStatus status, @Param("loc_id") Integer localityId, @Param("parent_loc_id") Integer localityParentId, @Param("code") String languageCode, @Param("second_code") String secondLanguageCode);
    Service findByGroup(Group group);
    @Query("SELECT s FROM Service s WHERE " +
            "(s.locality.id = :loc_id OR s.locality.id = :parent_loc_id OR s.locality.id = 999) AND " +
            "(s.language.code = :code OR s.language.code = :second_code) AND " +
            "s.status = 1")
    List<Service> findServicesByLocalityAndLanguage(@Param("loc_id") Integer localityId, @Param("parent_loc_id") Integer localityParentId, @Param("code") String languageCode, @Param("second_code") String secondLanguageCode);

    @Query("SELECT s FROM Service s WHERE " +
            "s.language.code = :code AND " +
            "s.status = 1")
    List<Service> findServicesByLanguage(@Param("code") String languageCode);
}
