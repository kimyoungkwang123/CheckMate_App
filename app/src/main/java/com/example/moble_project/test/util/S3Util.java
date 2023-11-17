package com.example.moble_project.test.util;


import static com.example.moble_project.test.util.S3Key.ACCESSKEY;
import static com.example.moble_project.test.util.S3Key.SECRETKEY;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * 2022. 05. 24.
 *
 * @author jbchoi
 */
public class S3Util {

    private String accessKey = ACCESSKEY;  // IAM AccessKey
    private String secretKey = SECRETKEY;  // IAM SecretKey
    private Region region;          // S3 Region

    private AmazonS3Client s3Client;
    private String uploadedFileUrl = null;

    /**
     * 생성자 생성 시 초기 Region 설정 : AP_NORTHEAST_2
     */
    public S3Util() {
        region = Region.getRegion(Regions.AP_NORTHEAST_2);
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = new AmazonS3Client(awsCredentials, region);
    }

    /**
     * Overloading
     */
    public void uploadWithTransferUtility(
            Context context,
            String bucketName, File file,
            TransferListener listener
    ) {
        this.uploadWithTransferUtility(
                context,
                bucketName, null, file, null,
                listener
        );

    }

    /**
     * Overloading
     */
    public void uploadWithTransferUtility(
            Context context,
            String bucketName, String folder, File file,
            TransferListener listener
    ) {
        this.uploadWithTransferUtility(
                context,
                bucketName, folder, file, null,
                listener
        );
    }

    /**
     * S3 파일 업로드
     *
     * @param context    Context
     * @param bucketName S3 버킷 이름(/(슬래쉬) 없이)
     * @param folder     버킷 내 폴더 경로(/(슬래쉬) 맨 앞, 맨 뒤 없이)
     * @param fileName   파일 이름
     * @param file       Local 파일 경로
     * @param listener   AWS S3 TransferListener
     */
    public void uploadWithTransferUtility(
            Context context,
            String bucketName, @Nullable String folder,
            File file, @Nullable String fileName,
            TransferListener listener
    ) {
        if (TextUtils.isEmpty(accessKey) || TextUtils.isEmpty(secretKey)) {
            throw new IllegalArgumentException(
                    "AccessKey & SecretKey must be not null"
            );
        }

        AWSCredentials awsCredentials = new BasicAWSCredentials(
                accessKey, secretKey
        );

        AmazonS3Client s3Client = new AmazonS3Client(
                awsCredentials, region
        );

        TransferUtility transferUtility = TransferUtility.builder()
                .s3Client(s3Client)
                .context(context)
                .build();

        TransferNetworkLossHandler.getInstance(context);

        TransferObserver uploadObserver = transferUtility.upload(
                (TextUtils.isEmpty(folder))
                        ? bucketName
                        : bucketName + "/" + folder,
                (TextUtils.isEmpty(fileName))
                        ? file.getName()
                        : fileName,
                file
        );

        uploadObserver.setTransferListener(listener);

    }

    /**
     * Access, Secret Key 설정
     */
    public S3Util setKeys(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        return this;
    }

    /**
     * Access Key 설정
     */
    public S3Util setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    /**
     * Secret Key 설정
     */
    public S3Util setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    /**
     * Region Enum 으로 Region 설정
     */
    public S3Util setRegion(Regions regionName) {
        this.region = Region.getRegion(regionName);
        return this;
    }

    /**
     * Region Class 로 Region 설정
     */
    public S3Util setRegion(Region region) {
        this.region = region;
        return this;
    }

    /**
     * Singleton Pattern
     */
    public static S3Util getInstance() {
        return LHolder.instance;
    }

    private static class LHolder {
        private static final S3Util instance = new S3Util();
    }

    public File createFileFromInputStream(InputStream inputStream, Context context, String fileName) {
        File file = null;
        try {
            // 주어진 파일 이름으로 새 파일 객체를 생성합니다.
            file = new File(context.getCacheDir(), fileName);

            // try-with-resources 문을 사용하여 outputStream을 자동으로 닫습니다.
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                // outputStream은 try-with-resources 덕분에 자동으로 닫힐 것입니다.
            }
            // inputStream도 닫습니다.
            inputStream.close();
        } catch (IOException e) {
            // 파일 생성 중 오류 처리
            e.printStackTrace();
            // 실패한 경우 file을 null로 설정하여 후속 처리에 문제가 없도록 합니다.
            file = null;
        }
        return file;
    }


    public void deleteFileFromS3(String bucketName, String filePath) {
        if (TextUtils.isEmpty(accessKey) || TextUtils.isEmpty(secretKey)) {
            throw new IllegalArgumentException("AccessKey & SecretKey must be not null");
        }

        if (s3Client != null) {
            try {
                s3Client.deleteObject(bucketName, filePath);
            } catch (Exception e) {
                e.printStackTrace();
                // 여기서 로깅이나 추가 처리를 할 수 있습니다.
            }
        }
    }
    /**
     * S3 파일 URL을 얻어옵니다.
     *
     * @param bucketName S3 버킷 이름
     * @param filePath   파일 경로
     * @return 파일의 URL
     */
    public String getFileUrl(String bucketName, String filePath) {
        if (s3Client != null) {
            try {
                Date expiration = new Date();
                long msec = expiration.getTime();
                msec += 7 * 24 * 60 * 60 * 1000; // URL이 유효한 기간을 7일로 변경
                expiration.setTime(msec);

                GeneratePresignedUrlRequest generatePresignedUrlRequest =
                        new GeneratePresignedUrlRequest(bucketName, filePath)
                                .withMethod(HttpMethod.GET)
                                .withExpiration(expiration);

                URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
                return url.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}