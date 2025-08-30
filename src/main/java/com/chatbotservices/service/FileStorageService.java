
package com.chatbotservices.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author Saikiran Nannapanenni
 * 
 */
@Service
public class FileStorageService {
	private final String UPLOAD_DIR = System.getProperty("user.dir") + "/Profile/";

	public FileStorageService() throws IOException {
		Files.createDirectories(Paths.get(UPLOAD_DIR));
	}

	public String saveFile(MultipartFile file, Long userId) {
		try {
			String originalFileName = Paths.get(file.getOriginalFilename()).getFileName().toString();
			String fileName = userId + "_" + System.currentTimeMillis() + "_" + originalFileName;
			Path filePath = Paths.get(UPLOAD_DIR, fileName);
			Files.write(filePath, file.getBytes());
			return fileName;
		} catch (IOException e) {
			throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
		}
	}

	public byte[] loadFile(String fileName) {
		try {
			Path filePath = Paths.get(UPLOAD_DIR, fileName);
			return Files.readAllBytes(filePath);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load file: " + e.getMessage(), e);
		}
	}

	public void deleteFile(String fileName) {
		try {
			Path filePath = Paths.get(UPLOAD_DIR, fileName);
			Files.deleteIfExists(filePath);
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
		}
	}
}
