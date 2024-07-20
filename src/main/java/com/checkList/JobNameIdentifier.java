package com.checkList;

import java.util.HashMap;
import java.util.Map;

public class JobNameIdentifier {

	public Map<String, String> jobAndLogNameFinder() {
		Map<String, String> jobAndLogName = new HashMap<>();
		//ASCS jobs in 300 & 301 server
		jobAndLogName.put( "LandingZone" , "BLCS_PRODUCER_LANDING_ZONE_BATCH_PROD");
		jobAndLogName.put( "autoAppointmentAgentProcess" , "ASCS_DAILY_NIPR_VA_REM_AUTO_APPOINTMENT_REQ_PROCESS_PROD");
		jobAndLogName.put( "fetchAgentContactInfoProcess" , "ASCS_DAILY_NIPR_PDB_BUMP_CONTACT_INFO_PROCESS_LOAD");
		jobAndLogName.put( "UnappointedProducerNotification" , "ASCS_DAILY_UNAPPOINTED_NOTIFICATION_PROCESS_LOAD");
		jobAndLogName.put( "FMOAgentReleaseNotification" , "ASCS_DAILY_FMO_MGA_RELEASE_NOTIFICATION_PROCESS_LOAD");
		jobAndLogName.put( "producerCustomPointOutBoundProcess" , "BLCS_PRODUCER_SBO_CUSTOM_POINT_OUTBOUND_PROD");
		jobAndLogName.put( "producerRtsProcess" , "BLCS_PRODUCER_RTS_PROCESS_BATCH_PROD");
		jobAndLogName.put( "NIPRPDBAlertUpdateLicenseProcess" , "ASCS_DAILY_NIPR_PDB_ALERT_UPDATE_AGENT_LICENSE_PROCESS_LOAD");
		jobAndLogName.put( "fetchFailureResponses" , "ASCS_DAILY_NIPR_GATEWAY_PROCESS_LOAD SUCCESS");
		jobAndLogName.put( "ContractFormSignCheckJob" , "ASCS_DAILY_CONTRACT_FORM_POLLING_PROCESS");
		jobAndLogName.put( "LetterExclusionMassUploadJob" , "ASCS_DAILY_LETTER_EXCLUSION_MASS_UPLOAD_PROCESS_PROD");
		jobAndLogName.put( "AgentAppointmentApprovalProcess" , "ASCS_DAILY_NIPR_VA_REM_AUTO_APPOINTMENT_RESP_PROCESS_LOAD");
		jobAndLogName.put( "NIPRPDBAlertAppointmentProcess" , "ASCS_DAILY_NIPR_PDB_ALERT_UPDATE_AGENT_APPOINTMENT_PROCESS_LOAD");
		jobAndLogName.put( "fetchReportForFailureSubscriptions" , "ASCS_DAILY_NIPR_PDB_ALERT_RETRIGGER_REPORT_PROCESS_LOAD");
		jobAndLogName.put( "fetchReportForValidSubscriptions" , "ASCS_DAILY_NIPR_PDB_ALERT_TRIGGER_REPORT_PROCESS_LOAD");
		jobAndLogName.put( "NIPRPDBAlertSubscriptionProcess" , "ASCS_DAILY_NIPR_PDB_ALERT_SUBSCRIPTION_PROCESS");
		jobAndLogName.put( "NIPRPDBAlertTargetProcess" , "ASCS_DAILY_NIPR_PDB_ALERT_TARGET_PROCESS_LOAD");
		jobAndLogName.put( "fetchAgentDemographicsFromNiprAndUpdateToASCS" , "ASCS_DAILY_NIPR_PDB_ALERT_UPDATE_AGENT_DEMOGRAPHICS_PROCESS_LOAD");
		jobAndLogName.put( "ProducerNiprProcessJob" , "ASCS_PRODUCER_NIPR_PROCESS_BATCH_PROD");
		jobAndLogName.put( "niprDailyErrorReportJob" , "ASCS_PRODUCER_NIPR_ERROR_REPORT_BATCH_PROD");
		jobAndLogName.put( "ProducerNiprProcessEmailJob" , "ASCS_PRODUCER_NIPR_EMAIL_REPORT_BATCH_PROD");
		jobAndLogName.put( "ContractFormAutoSyncJob" , "ASCS_DAILY_CONTRACT_FORM_AUTO_SYNC_PROCESS");
		jobAndLogName.put( "ProducerSetupInternalAgentJob" , "BLCS_DAILY_INTERNAL_AGENT_SETUP_PROCESS_LOAD");
		jobAndLogName.put( "internalAgentTerminationPSHRJob" , "BLCS_DAILY_INTERNAL_AGENT_TERMINATION_PSHR_JOB_PROD");
		jobAndLogName.put( "ProducerSetupPDBBumpTriggerJob" , "BLCS_DAILY_AGENT_TRIGGER_PDB_BUMP_PROCESS_LOAD");
		jobAndLogName.put( "ProducerSetupExternalAgentJob" , "BLCS_DAILY_EXTERNAL_AGENT_SETUP_PROCESS_LOAD");
		jobAndLogName.put( "ProducerSetupApptResponseJob" , "BLCS_DAILY_AGENT_APPT_RESPONSE_PROCESS_LOAD");
		jobAndLogName.put( "ProducerSetupApptRequestJob" , "BLCS_DAILY_AGENT_APPT_REQUEST_PROCESS_LOAD");
		jobAndLogName.put( "ContractFormTerminationJob" , "ASCS_DAILY_CONTRACT_FORM_TERMINATION_PROCESS");
		jobAndLogName.put( "ProducerSetupStatusUpdateJob" , "BLCS_DAILY_PRODUCER_SETUP_STATUS_UPDATE_PROCESS_LOAD");
		jobAndLogName.put( "AgentTermLetterJob" , "ASCS_DAILY_TERM_LETTER_PROCESS_LOAD");
		jobAndLogName.put( "WLPSDAuditJob" , "ASCS_DAILY_WELCOME_LETTER_AUDIT_PROCESS_LOAD");
		jobAndLogName.put( "VaRemPSDAuditJob" , "ASCS_DAILY_VA_REM_AUDIT_PROCESS_LOAD");
		jobAndLogName.put( "WelcomeLetterProcessJob" , "ASCS_DAILY_WELCOME_LETTER_PROCESS_LOAD");
		jobAndLogName.put( "LetterProcessJob" , "ASCS_DAILY_VA_REMEDIATION_LETTER_PROCESS_LOAD");
		jobAndLogName.put( "ReprocessFailedRecordsJob" , "ASCS_DAILY_REPROCESS_FAILED_RECORDS");
		jobAndLogName.put( "CustomPointHandShakeEmailJob" , "SBO_DAILY_CP_LETTER_LOAD_PROD");
		jobAndLogName.put( "AgentWelcomeLetterJob" , "ASCS_DAILY_AGENT_SETUP_WELCOM_LETTER_PROCESS_LOAD");
		jobAndLogName.put( "ReconReportJob", "ASCS_DAILY_RECON_REPORT_PROCESS_LOAD");
		//SBO jobs and report jobs in 002 server
		jobAndLogName.put("SalesEvent_Hourly_Job", "ASCS_SBO_HOURLY_SALES_EVENT_JOB");
		jobAndLogName.put("Cmplnt_Mntr_Watchlist_Job", "ASCS_SBO_HOURLY_CMPLT_MNTR_WATCHLIST_DATA_LOAD");
		jobAndLogName.put("BOD_Mailingservice_Job", "ASCS_SBO_HOURLY_MAILINGSERVICE");
		jobAndLogName.put("LicenseAppointmentReportJob", "ASCS_SBO_DAILY_LCNS_STATE_APPT_TERM_LOAD");
		jobAndLogName.put("GenerateRTSReports", "ASCS_SBO_DAILY_GENERATE_RTS_REPORTS");
		jobAndLogName.put("RTSUniverse", "ASCS_SBO_DAILY_RTS_REPORTS");
		jobAndLogName.put("RTSCertificationInProgressJob", "ASCS_SBO_DAILY_RTS_CERT_INPROGRESS");
		jobAndLogName.put("AgentTypeInfoJob", "BLCS_AGENT_TYPE_INFO_PROD");
		jobAndLogName.put("SOA_Job", "ASCS_SBO_WEEKLY_SOALOAD_PROCESS");
		jobAndLogName.put("wcw_infogix_balnchk", "PROD_ASCS_SBO_WCW_FEED_BALNCHK");
		jobAndLogName.put("WeeklyNYReportJob", "ASCS_WEEKLY_NY_ACTIVE_REPORT_PROD");
		jobAndLogName.put("AssuranceSubAgentJob", "ASCS_ASSURANCE_SUBAGENT_REPORT_PROD");
		jobAndLogName.put("AgentAppointmentInvalidDataSubAgentJob", "ASCS_BIWEEKLY_APPT_INVALID_DATA_REPORT_PROD");
		jobAndLogName.put("EnhanceSubAgentJob", "ASCS_ASSURANCE_SUBAGENT_REPORT_PROD");
		jobAndLogName.put("Amerilife_Report_Generic_Job", "ASCS_AMERILIFE_SUBAGENT_REPORT_PROD");
		jobAndLogName.put("AddressLocationReportJob", "ASCS_AGENT_ADDRESS_LOC_REPORT_PROD");
		jobAndLogName.put("CenterstoneSubAgentJob", "ASCS_CENTERSTONE_SUBAGENT_REPORT_PROD");
		jobAndLogName.put("ISGSubAgentJob", "ASCS_ISG_SUBAGENT_REPORT_PROD");
		jobAndLogName.put("IHCSubAgentJob", "ASCS_IHC_SUBAGENT_REPORT_PROD");
		jobAndLogName.put("AgentPipelineSubAgentJob", "ASCS_AGENT_PIPELINE_SUBAGENT_REPORT_PROD");
		jobAndLogName.put("EnhanceSubAgentJob", "ASCS_ENHANCE_SUBAGENT_REPORT_PROD");
		jobAndLogName.put("CaliforniaAgentsJob", "ASCS_CA_AGENTS_REPORT_PROD");
		jobAndLogName.put("BlcsMonthlyBrokerInfoJob", "ASCS_MONTHLY_BROKER_INFO_PROD");
		jobAndLogName.put("MonthlyGeorgiaCertifiedAgentJob", "ASCS_MONTHLY_GEORGIA_CERTIFIED_AGENT_REPORT_PROD");
		jobAndLogName.put("AmericanInsuranceSubAgentJob", "ASCS_AMERICAN_INSURANCE_SUBAGENT_REPORT_PROD");
		return jobAndLogName;
	}

}
