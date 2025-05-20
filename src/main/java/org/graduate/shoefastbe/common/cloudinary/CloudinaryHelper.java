package org.graduate.shoefastbe.common.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.graduate.shoefastbe.common.Common;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

// https://console.cloudinary.com/pm/c-ec1c837df708ade0d599b18e2b553b/getting-started
// https://dev.to/hackmamba/uploading-media-in-spring-boot-programmatically-with-cloudinary-35bm
public class CloudinaryHelper {
    public static Cloudinary cloudinary;

    static {
        cloudinary = new Cloudinary(
                ObjectUtils.asMap(
                        Common.CLOUDINARY_NAME, Common.CLOUDINARY_NAME_VALUE,
                        Common.CLOUDINARY_API_KEY, Common.CLOUDINARY_API_KEY_VALUE,
                        Common.CLOUDINARY_API_SECRET, Common.CLOUDINARY_API_SECRET_VALUE
                )
        );
        System.out.println("SUCCESS GENERATE INSTANCE FOR CLOUDINARY");
    }

    public static String uploadAndGetFileUrl(MultipartFile multipartFile) {
        try {
            File uploadedFile = convertMultiPartToFile(multipartFile);
            Map<String, Object> options = new HashMap<>(); // Sử dụng một `Map` có thể sửa đổi
            Map uploadResult = cloudinary.uploader().uploadLarge(uploadedFile, options);
            return uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
