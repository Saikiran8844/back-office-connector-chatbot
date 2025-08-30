
package com.chatbotservices.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chatbotservices.model.User;

import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailTo;

/**
 * MailService for sending emails via Brevo (Sendinblue)
 * 
 * @author Saikiran Nannapaneni
 **/
@Service
public class MailService {

	private static final Logger logger = LogManager.getLogger(MailService.class);

    @Value("${brevo.resetpassword.id}")
    private Long resetpasswordId;
    @Value("${brevo.passwordchanged.id}")
    private Long sendPasswordChangedEmail;
    
    @Value("${brevo.api.key}")
    private String api_key;

    public String sendResetPasswordEmail(String recipientEmail, String resetLink,  User user) throws Exception {
        logger.info("Initiating password reset email for recipient: {}", recipientEmail);
        
//        SSLBypass.disableSSLVerification();


        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(api_key);

        try {
            TransactionalEmailsApi api = new TransactionalEmailsApi();
            logger.debug("Transactional email API client initialized.");

            List<SendSmtpEmailTo> toList = new ArrayList<>();
            SendSmtpEmailTo to = new SendSmtpEmailTo();
            to.setEmail(recipientEmail);
            toList.add(to);

            Properties params = new Properties();
            params.setProperty("username",user.getName());
            params.setProperty("resetLink", resetLink);
            logger.debug("Email parameters set with reset link: {}", resetLink);

            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
            sendSmtpEmail.setTo(toList);
            sendSmtpEmail.setParams(params);
            sendSmtpEmail.setTemplateId(resetpasswordId);

            logger.info("Sending email using template ID: {}", resetpasswordId);
            CreateSmtpEmail response = api.sendTransacEmail(sendSmtpEmail);
            logger.info("Email sent successfully. Response: {}", response);
            
            return response.toString();
        } catch (Exception e) {
            logger.error("Exception occurred while sending email: {}", e.getMessage(), e);
            throw e;
        }
    
    }
    
    
    public String sendPasswordChangedEmail(String recipientEmail, User user) throws Exception {
        logger.info("Sending password change confirmation email to: {}", recipientEmail);

        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(api_key);

        try {
            TransactionalEmailsApi api = new TransactionalEmailsApi();
            logger.debug("Transactional email API client initialized.");

            List<SendSmtpEmailTo> toList = new ArrayList<>();
            SendSmtpEmailTo to = new SendSmtpEmailTo();
            to.setEmail(recipientEmail);
            toList.add(to);

            Properties params = new Properties();
            params.setProperty("username", user.getName());  

            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
            sendSmtpEmail.setTo(toList);
            sendSmtpEmail.setParams(params);
            sendSmtpEmail.setTemplateId(sendPasswordChangedEmail); // Ensure this template ID is set in application.properties

            logger.info("Sending email using template ID: {}", sendPasswordChangedEmail);
            CreateSmtpEmail response = api.sendTransacEmail(sendSmtpEmail);
            logger.info("Email sent successfully. Response: {}", response);
            
            return response.toString();
        } catch (Exception e) {
            logger.error("Exception occurred while sending email: {}", e.getMessage(), e);
            throw e;
        }
    }


	
}
