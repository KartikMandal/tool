package com.kartik.certificate;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class CertificateImpl {
	static List<CertificatePojo> list=new ArrayList<CertificatePojo>();
	
	public void addCertificate(String fileName)
		    throws Exception {
		    InputStream is = new FileInputStream(fileName);
		    CertificateFactory certificateFactory = CertificateFactory
		      .getInstance("X.509");
		    X509Certificate certificate = (X509Certificate) certificateFactory
		      .generateCertificate(is);
		    is.close();

		    certificate.checkValidity();

		    long creationTime = certificate.getNotBefore().getTime() / 1000;
		    long expirationTime = certificate.getNotAfter().getTime() / 1000;

		    String commonName = CertificationUtil.getSubjectDN(certificate);

		    byte[] encodedCertificate = certificate.getEncoded();

		    CertificatePojo certificatePojo = new CertificatePojo();
		    certificatePojo.setCreationTime(new Long(creationTime));
		    certificatePojo.setExpirationTime(new Long(expirationTime));
		    String certificateData = new String(Base64.encode(encodedCertificate));
		    certificatePojo.setCertificationDetail(certificateData);
		    certificatePojo.setCn(commonName);
		    list.add(certificatePojo);
		   // ssoCert.save(con, dbName);
		   // con.commit();
		  }
	public static void main(String []args){
		String fileName="D:\\Software\\java\\jdk1.8.0_05\\bin\\kartik.cer";
		CertificateImpl ccc=new CertificateImpl();
		try {
			ccc.addCertificate(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (CertificatePojo cer : list) {
			System.out.println(cer.getCn());
		}
		
		CertificateDetails certDetails = CertificateUtil.getCertificateDetails("D:\\Software\\java\\jdk1.8.0_05\\bin\\kartik.keystore", "kartik");
		System.out.println("-------------------Private key--------------------------");
		System.out.println(Base64.encode(certDetails.getPrivateKey().getEncoded()));
		System.out.println();
		System.out.println("-------------------Public key --------------------------");
		System.out.println(Base64.encode(certDetails.getPublicKey().getEncoded()));
		System.out.println();
		System.out.println("-------------------certificate --------------------------");
		System.out.println(certDetails.getX509Certificate());
		
	}
}
