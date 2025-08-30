


package com.chatbotservices.service;

import com.chatbotservices.model.User;
import com.chatbotservices.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * 
 * @author Saikiran Nannapanenni
 * 
 */
@Service
public class ProfilePictureService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private SessionService sessionService;

    public String uploadProfilePicture(String sessionId, MultipartFile file) throws IOException {
        Long userId = sessionService.getUserIdFromSession(sessionId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // validate file type (JPg & PNG )
        String fileType = file.getContentType();
        if (!("image/jpeg".equals(fileType) || "image/png".equals(fileType) ))
        {
        	throw new RuntimeException ("Invlide file type, only JPEG and PNG files are allowed");
        }

        // Delete old profile picture if exists
        if (user.getProfilePicture() != null) {
            fileStorageService.deleteFile(user.getProfilePicture());
        }

        // Save new profile picture
        String fileName = fileStorageService.saveFile(file, userId);
        user.setProfilePicture(fileName);
        userRepository.save(user);

        return "Profile picture uploaded successfully!";
    }

    public byte[] getProfilePicture(String sessionId) throws IOException {
        Long userId = sessionService.getUserIdFromSession(sessionId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProfilePicture() == null) {
            throw new RuntimeException("No profile picture found");
        }

        return fileStorageService.loadFile(user.getProfilePicture());
    }

    public String deleteProfilePicture(String sessionId) throws IOException {
        Long userId = sessionService.getUserIdFromSession(sessionId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProfilePicture() != null) {
            fileStorageService.deleteFile(user.getProfilePicture());
            user.setProfilePicture(null);
            userRepository.save(user);
        }

        return "Profile picture deleted successfully!";
    }
}
