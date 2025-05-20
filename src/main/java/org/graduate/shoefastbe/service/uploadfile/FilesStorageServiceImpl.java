package org.graduate.shoefastbe.service.uploadfile;

import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class FilesStorageServiceImpl implements FilesStorageService {
    @Autowired
    ServletContext app;

    private final Path root = Paths.get("uploads");

    @Override
    public void init() {
        try {
            Files.createDirectory(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    @Transactional
    public void save(MultipartFile file) {
        try {
            File directory = Files.createDirectory(root).toFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<String> upload(MultipartFile[] files) {
        List<String> filenames = new ArrayList<>();
        try {
            Files.createDirectories(this.root); // Đảm bảo thư mục tồn tại
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();

                // Kiểm tra tên file có hợp lệ hay không
                if (originalFilename == null || originalFilename.contains("..")) {
                    throw new RuntimeException("Invalid file path: " + originalFilename);
                }

                // Chuẩn hóa tên tệp (loại bỏ các ký tự không hợp lệ và thay thế khoảng trắng)
                String filename = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

                // Đảm bảo tên tệp không bị trùng lặp bằng cách thêm timestamp hoặc UUID
                Path path = this.root.resolve(filename);
                if (Files.exists(path)) {
                    String newFilename = System.currentTimeMillis() + "-" + filename;
                    path = this.root.resolve(newFilename);
                    filename = newFilename;
                }
                file.transferTo(path);
                filenames.add(filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
        return filenames;
    }

    private Path getPath(String folder, String filename) {
        File dir = Paths.get(app.getRealPath("/"), folder).toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return Paths.get(dir.getAbsolutePath(), filename);
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

}
