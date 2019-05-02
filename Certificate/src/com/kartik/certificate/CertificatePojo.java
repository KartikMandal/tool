package com.kartik.certificate;

public class CertificatePojo {

	private long certificateId;
	/**
	 * @return the certificateId
	 */
	public long getCertificateId() {
		return certificateId;
	}
	/**
	 * @return the certificationDetail
	 */
	public String getCertificationDetail() {
		return certificationDetail;
	}
	/**
	 * @return the creationTime
	 */
	public long getCreationTime() {
		return creationTime;
	}
	/**
	 * @return the expirationTime
	 */
	public long getExpirationTime() {
		return expirationTime;
	}
	/**
	 * @return the cn
	 */
	public String getCn() {
		return cn;
	}
	/**
	 * @param certificateId the certificateId to set
	 */
	public void setCertificateId(long certificateId) {
		this.certificateId = certificateId;
	}
	/**
	 * @param certificationDetail the certificationDetail to set
	 */
	public void setCertificationDetail(String certificationDetail) {
		this.certificationDetail = certificationDetail;
	}
	/**
	 * @param creationTime the creationTime to set
	 */
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	/**
	 * @param expirationTime the expirationTime to set
	 */
	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}
	/**
	 * @param cn the cn to set
	 */
	public void setCn(String cn) {
		this.cn = cn;
	}
	private String certificationDetail;
	private long creationTime;
	private long expirationTime;
	private String cn;
}
