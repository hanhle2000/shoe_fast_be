package org.graduate.shoefastbe.controller;

import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.common.FileInfo;
import org.graduate.shoefastbe.common.ResponseMessage;
import org.graduate.shoefastbe.common.cloudinary.CloudinaryHelper;
import org.graduate.shoefastbe.service.uploadfile.FilesStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@AllArgsConstructor
public class FilesController {

  private final FilesStorageService storageService;

  @PostMapping("/api/v1/upload")
  public ResponseEntity<ResponseMessage> uploadFiles(@RequestParam("file") MultipartFile[] files) {
    String message = "";
    try {
      List<String> fileNames = new ArrayList<>();

      Arrays.stream(files).forEach(file -> {
        storageService.save(file);
        fileNames.add(file.getOriginalFilename());
      });

      message = "Uploaded the files successfully: " + fileNames;
      return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    } catch (Exception e) {
      message = "Fail to upload files!";
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
    }
  }

//  @PostMapping("/api/v1/upload-image")
//  public ResponseEntity<?> upload(@RequestParam("file") MultipartFile[] multipartFiles){
//    return new ResponseEntity<>(storageService.upload(multipartFiles), HttpStatus.OK);
//  }
  @PostMapping("/api/v1/upload-image")
  public ResponseEntity<?> upload(@RequestParam("file") MultipartFile[] multipartFiles){
    List<String> imageUrls = new ArrayList<>();
    for(MultipartFile multipartFile : multipartFiles){
      imageUrls.add(CloudinaryHelper.uploadAndGetFileUrl(multipartFile));
    }
    return new ResponseEntity<>(imageUrls, HttpStatus.OK);
  }
  @GetMapping("/api/v1/files")
  public ResponseEntity<List<FileInfo>> getListFiles() {
    List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
      String filename = path.getFileName().toString();
      String url = MvcUriComponentsBuilder
          .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();

      return new FileInfo(filename, url);
    }).collect(Collectors.toList());

    return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
  }

  @GetMapping("/api/v1/files/{filename:.+}")
  public ResponseEntity<Resource> getFile(@PathVariable String filename) {
    Resource file = storageService.load(filename);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }
}
