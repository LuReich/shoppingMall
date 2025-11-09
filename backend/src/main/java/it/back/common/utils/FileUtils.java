package it.back.common.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.MultiStepRescaleOp;

@Component
public class FileUtils {

    /**
     * 파일을 지정된 경로에 저장하고, 서버에 저장된 파일명을 반환합니다.
     *
     * @param file 업로드할 MultipartFile
     * @param directoryPath 파일을 저장할 디렉터리 경로 (예: "C:/ourshop/product/1/main/")
     * @return 서버에 저장된 고유한 파일명 (예: "uuid_name.jpg")
     * @throws IllegalArgumentException 파일이 비어있을 경우
     * @throws UncheckedIOException 파일 저장 중 입출력 오류 발생 시
     */
    public String saveFile(MultipartFile file, String directoryPath) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFileName = UUID.randomUUID().toString() + extension;

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // 부모 폴더까지 모두 생성
        }

        File destinationFile = new File(directory, storedFileName);

        try {
            file.transferTo(destinationFile);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 저장에 실패했습니다: " + storedFileName, e);
        }
        return storedFileName;
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String fullPath) {
        File fileToDelete = new File(fullPath);
        if (fileToDelete.exists()) {
            if (!fileToDelete.delete()) {
                // 파일 삭제 실패 시 예외 발생
                throw new UncheckedIOException("파일 삭제에 실패했습니다: " + fullPath, new IOException("Failed to delete file"));
            }
        }
        // 파일이 존재하지 않으면 아무것도 하지 않음 (오류 아님)
    }

    /**
     * 지정된 경로의 디렉터리를 삭제합니다. 디렉터리는 비어있어야 합니다.
     *
     * @param directoryPath 삭제할 디렉터리 경로
     */
    public void deleteDirectory(String directoryPath) {
        File directoryToDelete = new File(directoryPath);
        if (directoryToDelete.exists() && directoryToDelete.isDirectory()) {
            String[] files = directoryToDelete.list();
            if (files != null && files.length == 0) {
                if (!directoryToDelete.delete()) {
                    throw new UncheckedIOException("디렉터리 삭제에 실패했습니다: " + directoryPath, new IOException("Failed to delete directory"));
                }
            }
        }
    }

    /**
     * 썸네일 만들기
     */
    public String thumbNailFile(int width, int height, File originFile, String thumbPath) throws Exception {
        String thumbFileName = "";

        String fileName = originFile.getName();
        String extention = fileName.substring(fileName.lastIndexOf(".") + 1);
        String randName = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        thumbFileName = randName + "." + extention;

        try (
                InputStream in = new FileInputStream(originFile); BufferedInputStream bf = new BufferedInputStream(in);) {

            //원본 이미지 파일 뜨기
            BufferedImage originImage = ImageIO.read(originFile);
            //이미지 사이즈 줄이기
            MultiStepRescaleOp scaleImage = new MultiStepRescaleOp(width, height);
            //마스킹처리
            scaleImage.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
            //리사이즈 이미지 생성
            BufferedImage resizeImage = scaleImage.filter(originImage, null);

            String thubmbFilePath = thumbPath + thumbFileName;

            File resizeFile = new File(thubmbFilePath);

            //경로없으면 만들어주자
            if (!resizeFile.getParentFile().exists()) {
                //경로 만들어주기
                resizeFile.getParentFile().mkdirs();
            }

            //리사이즈한 파일을 실제 경로에 생성. 결과를 리턴해준다.
            boolean isWrite = ImageIO.write(resizeImage, extention, resizeFile);

            if (!isWrite) {
                throw new RuntimeException("썸네일 생성 오류");
            }

        } catch (Exception e) {
            thumbFileName = null;
            e.printStackTrace();
            throw new RuntimeException("썸네일 생성 오류 ");
        }

        return thumbFileName;

    }
}
