package com.vin.VinSystem.Auth.Controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vin.VinSystem.Auth.Entity.User;
import com.vin.VinSystem.Auth.Repository.UserRepository;
import com.vin.VinSystem.Common.ApiResponse;
import com.vin.VinSystem.Config.CloudinaryService;

@RestController
@RequestMapping("/api/users")
public class AvatarController {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public AvatarController(UserRepository userRepository,
                             CloudinaryService cloudinaryService) {
        this.userRepository     = userRepository;
        this.cloudinaryService  = cloudinaryService;
    }

   @PostMapping("/upload-avatar")
public ApiResponse<Map<String, String>> uploadAvatar(
        @RequestParam("file") MultipartFile file,
        Authentication authentication) throws IOException {

    String username = authentication.getName();
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getAvatarPublicId() != null) {
        cloudinaryService.delete(user.getAvatarPublicId());
    }

    // Gọi upload với folder avatars — CloudinaryService.upload() tự xử lý
    Map<String, Object> result = cloudinaryService.upload(file, "vinsystem/avatars");
    String imageUrl  = (String) result.get("secure_url");
    String publicId  = (String) result.get("public_id");

    user.setAvatar(imageUrl);
    user.setAvatarPublicId(publicId);
    userRepository.save(user);

    return ApiResponse.success(Map.of("avatarUrl", imageUrl), "Upload avatar thành công");
}
}