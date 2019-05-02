
package com.kartik.certificate;


import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;

import com.kartik.exception.InvalidBase64EncodingException;
import com.kartik.exception.InvalidCertificateTypeException;

/**
 * This class provides utility static functions to read data from/into
 * <code>X509Certificate</code> objects.
 */

public class CertificationUtil {


    /* RCS version information */

    /**
     * Constructs a <code>X509Certificate</code> object from its byte representation.
     * @param data a byte array representing one instance of a <code>X509Certificate</code>
     *
     * @return an instance of <code>X509Certificate</code>
     *
     * @throws CertificateException if we fail to extract the certificate from the bytes
     * @throws InvalidCertificateTypeException if the certificate read is not of
     * <code>X509Certificate</code> type.
     */
    public static X509Certificate readCertificate(byte[] data)
            throws CertificateException, InvalidCertificateTypeException {

        Certificate cert = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        cert = cf.generateCertificate(bais);

        if (!(cert instanceof X509Certificate))
            throw new InvalidCertificateTypeException(cert.getClass().getName());

        X509Certificate x509cert = (X509Certificate) cert;

        return x509cert;
    }

    /**
     * A wrapper for <code>readCertificate(byte[]) which first decodes
     * what is expected to be a Base64 <code>String</code> before constructing
     * the certificate.
     *
     * @see CertificationUtil#readCertificate(byte[])
     */
    public static X509Certificate readCertificate(String data)
            throws CertificateException, InvalidBase64EncodingException,
            InvalidCertificateTypeException {

        byte[] decoded = Base64.decode(data.toCharArray());
        return readCertificate(decoded);
    }

    /** extracts the Subject Distinguished Name (DN) from the certificate */
    public static String getSubjectDN(X509Certificate cert) {

        return cert.getSubjectDN().getName();
    }

    /** extracts the Issuer Distinguished Name (DN) from the certificate */
    public static String getIssuerDN(X509Certificate cert) {

        return cert.getIssuerDN().getName();
    }

    /** extracts the 'Not After' date from the certificate, in epoch ms. In
     * the unexpected case where no such date is found, we return a time
     * guaranteed to be in the past
     */
    public static long getExpirationTime(X509Certificate cert) {

        Date date = cert.getNotAfter();

        /* should not happen - being conservative */
        if (date == null) return Long.MIN_VALUE;

        return date.getTime();
    }

    /** extracts the 'Not Before' date from the certificate, in epoch ms. In
     * the unexpected case where no such date is found, we return a time
     * guaranteed to be in the future
     */
    public static long getStartTime(X509Certificate cert) {

        Date date = cert.getNotBefore();

        /* should not happen - being conservative */
        if (date == null) return Long.MAX_VALUE;

        return date.getTime();
    }

    /** returns whether or not the certificate time is valid meaning that
     * the current time is after the 'Not Before' time and before the 'Not After'
     * time.
     * @see X509Certificate#checkValidity(Date)
     */
    public static boolean isCertificateTimeValid(X509Certificate cert, Date currentDate) {

        boolean result = true;

        try {
            cert.checkValidity(currentDate);
        }
        catch (CertificateExpiredException e) {
            result = false;
        }
        catch (CertificateNotYetValidException e) {
            result = false;
        }

        return result;
    }


}

