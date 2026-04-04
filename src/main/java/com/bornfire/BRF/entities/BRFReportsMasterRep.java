package com.bornfire.BRF.entities;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BRFReportsMasterRep extends CrudRepository<BRFReportsMaster, String> {

	@Query("select a.report_name from BRFReportsMaster a where a.reportid=?1")
	public String getReportName(String repordId);

	@Query("select a.xbrl_taxonomy_version from BRFReportsMaster a where a.reportid=?1")
	public String getTaxonomyVersion(String reportid);

	@Query("select a.report_frequency from BRFReportsMaster a where a.reportid=?1")
	public String getReportFrq(String reportid);

	@Query("select a.institution_code from BRFReportsMaster a where a.reportid=?1")
	public String getBankCode(String reportid);

	@Query("from BRFReportsMaster a where a.report_validity='Y' and a.domain_id in ?1")
	public Iterable<BRFReportsMaster> getReportList(List<String> domainid);

	@Query("from BRFReportsMaster a where a.report_validity='Y' and a.archive_enable_flg='Y' and a.domain_id in ?1")
	public Iterable<BRFReportsMaster> getArchReportList(List<String> domainid);

	@Query("select distinct a.domain_id from BRFReportsMaster a where domain_id is not null")
	public List<String> getDomainList();

	@Modifying
	@Query("update BRFReportsMaster a set report_validity=?2, lchg_user_id=?3, lchg_time=sysdate where reportid=?1 ")
	public int updateValidity(String reportId, String valid, String userid);

}
