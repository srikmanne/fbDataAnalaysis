package com.irev.services;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.RequestClientOptions;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.irev.common.Logger;
import com.irev.persistence.DataSourceForJdbcTemplate;

/**
 * Class: AwsS3Service - Uploads and retrieves files from AWS S3 Author:
 * Srikanth Manne Creation Date: 6/16/2019
 */
@Primary
@Component
public class AwsS3Service {

    @Autowired
    private Environment env;
    private Logger logger;
    private DataSourceForJdbcTemplate ds;
    private JdbcTemplate jdbcTemplate;

    /**
     * Constructor
     *
     * @param logger - (Logger)
     * @param ds - (DataSourceForJdbcTemplate)
     * @param utility - (Utility)
     * @param clientFileDao - (ClientFileDao)
     */
    @Autowired
    public AwsS3Service(Logger logger,
            DataSourceForJdbcTemplate ds) {
        this.logger = logger;
        this.jdbcTemplate = ds.getJdbcTemplate();
    }

    /**
     * Uploads a single file to the bucket
     *
     * @param uploadedFile - (MultipartFile)
     * @param sFilePath - (String)
     * @return boolean - (Boolean)
     *
     */
    public Boolean uploadToS3(MultipartFile uploadedFile, String sFilePath) {
        final String sWhere = "AwsS3Service.uploadToS3()";
        try {
            AWSCredentials credentials = new BasicAWSCredentials(env.getProperty("s3accesskey"), env.getProperty("s3secretkey"));
            /*The s3 client can be constructed with various client configuration properties, such as request timeout
            For now, it will be constructed with the default configuration */
            AmazonS3 awsClient = new AmazonS3Client(credentials);
            String sBucket = env.getProperty("s3bucket").substring(5);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(uploadedFile.getSize());

            String sError = null;
            try {
                awsClient.putObject(new PutObjectRequest(sBucket, sFilePath, uploadedFile.getInputStream(), objectMetadata));
            } catch (AmazonServiceException ase) {
                sError = "Upload rejected from Amazon S3 with error: " + ase.getMessage() + ", Status Code: " + ase.getStatusCode();
                this.logger.error(sWhere, ase);
                return false;
            } catch (AmazonClientException ace) {
                sError = "Error encountered while trying to communicate with Amazon S3";
                this.logger.error(sWhere, ace);
                return false;
            }
            if (sError != null) {
                this.logger.error(sWhere, "File upload failed to: " + sFilePath);
            }
        } catch (Exception e) {
            this.logger.error(sWhere, e);

        }
        return true;
    }

    /**
     * Upload an object to S3 via input stream, return size
     *
     * @param inputStream - (InputStream)
     * @param sFileName - (String)
     * @param sFilePath - (String)
     * @param fileSize - (long)
     * @return true - (Boolean)
     */
    public Boolean uploadInputStreamToS3(InputStream inputStream, String sFileName, String sFilePath, long fileSize) {
        final String sWhere = "AwsS3Service.uploadInputStreamToS3()";
        try {
            AWSCredentials credentials = new BasicAWSCredentials(env.getProperty("s3accesskey"), env.getProperty("s3secretkey"));
            /*The s3 client can be constructed with various client configuration properties, such as request timeout
            For now, it will be constructed with the default configuration */
            AmazonS3 awsClient = new AmazonS3Client(credentials);
            String sBucket = env.getProperty("s3bucket").substring(5);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(fileSize);
            String sError = null;
            try {
                awsClient.putObject(new PutObjectRequest(sBucket, sFilePath, inputStream, objectMetadata));
            } catch (AmazonServiceException ase) {
                sError = "Upload rejected from Amazon S3 with error: " + ase.getMessage() + ", Status Code: " + ase.getStatusCode();
                this.logger.error(sWhere, ase);
            } catch (AmazonClientException ace) {
                sError = "Error encountered while trying to communicate with Amazon S3";
                this.logger.error(sWhere, ace);
            }
            if (sError != null) {
                this.logger.error(sWhere, "File upload failed to: " + sFilePath);
            }
        } catch (Exception e) {
            this.logger.error(sWhere, e);
            return false;
        }
        return true;
    }

    /**
     * Uploads a single file from disk to S3 bucket
     *
     * @param sFilePathInS3 - (String)
     * @param sPathToFileOnDisk - (String)
     * @return bUploadSuccess - (boolean)
     *
     */
    public boolean uploadFromDiskToS3(String sFilePathInS3, String sPathToFileOnDisk) {
        final String sWhere = "AwsS3Service.uploadFromDiskToS3()";
        boolean bUploadSuccess = false;
        try {
            AWSCredentials credentials = new BasicAWSCredentials(env.getProperty("s3accesskey"), env.getProperty("s3secretkey"));
            /*The s3 client can be constructed with various client configuration properties, such as request timeout
            For now, it will be constructed with the default configuration */
            AmazonS3 awsClient = new AmazonS3Client(credentials);
            String sBucket = env.getProperty("s3bucket").substring(5);

            String sError = null;
            try {
                //-Read in the file from disk
                File f = new File(sPathToFileOnDisk);

                if (f.exists()) {
                    InputStream is = new FileInputStream(f);

                    //-Set file size
                    ObjectMetadata objectMetadata = new ObjectMetadata();
                    objectMetadata.setContentLength(f.length());

                    //-Upload to S3
                    awsClient.putObject(new PutObjectRequest(sBucket, sFilePathInS3, is, objectMetadata));
                    if (doesS3ObjectExist(sFilePathInS3)) {
                        bUploadSuccess = true;

                    }
                    is.close();
                } else {
                    this.logger.debug(sWhere, "File conversion failed, invalid path to file: " + sPathToFileOnDisk);
                }
            } catch (FileNotFoundException fnfe) {
                this.logger.error(sWhere, fnfe);
            } catch (IOException ioe) {
                this.logger.error(sWhere, ioe);
            } catch (AmazonServiceException ase) {
                sError = "Upload rejected from Amazon S3 with error: " + ase.getMessage() + ", Status Code: " + ase.getStatusCode();
                this.logger.error(sWhere, ase);
            } catch (AmazonClientException ace) {
                sError = "Error encountered while trying to communicate with Amazon S3";
                this.logger.error(sWhere, ace);
            }
            if (sError != null) {
                this.logger.error(sWhere, "File upload failed to: " + sFilePathInS3);
            }
        } catch (Exception e) {
            this.logger.error(sWhere, e);
        }
        return bUploadSuccess;
    }

    /**
     * Transfer multiple files from disk to S3
     *
     * @param sLearningActivityId - (String)
     * @param sFileOnDiskPath - (String)
     * @param sPathOnS3 - (String)
     * @param user - (User)
     * @return bUploadSuccess - (boolean)
     * @throws Exception - If necessary
     */
    public boolean transferMultipleFilesFromDiskToS3(String sLearningActivityId, String sFileOnDiskPath, String sPathOnS3) throws Exception {
        final String sWhere = "AwsS3Service.transferMultipleFilesFromDiskToS3()";
        boolean bUploadSuccess = false;
        try {
            AWSCredentials credentials = new BasicAWSCredentials(env.getProperty("s3accesskey"), env.getProperty("s3secretkey"));
            AmazonS3 awsClient = new AmazonS3Client(credentials);
            String sBucket = env.getProperty("s3bucket").substring(5);
            String sError = null;

            //-Make a map of added files in case they need to be removed
            Map<String, String> mResults = new HashMap<String, String>();
            List<File> files = Files.walk(Paths.get(sFileOnDiskPath))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            for (File f : files) {
                if (!f.isDirectory() && f.exists()) {
                    try {
                        InputStream is = new FileInputStream(f);
                        is = new BufferedInputStream(is);

                        //-Set file size
                        ObjectMetadata objectMetadata = new ObjectMetadata();
                        objectMetadata.setContentLength(f.length());

                        String sPath = f.getPath();
                        sPath = sPath.substring(101, sPath.length()); //-/opt/tomcat/files/generate/UUID/UUID/ length

                        //-Upload to S3
                        awsClient.putObject(new PutObjectRequest(sBucket, sPathOnS3 + sPath, is, objectMetadata));
                        if (doesS3ObjectExist(sPathOnS3 + sPath)) {
                            bUploadSuccess = true;

                        } else {
                            bUploadSuccess = false;
                        }
                        is.close();
                    } catch (FileNotFoundException fnfe) {
                        this.logger.error(sWhere, fnfe);
                    } catch (IOException ioe) {
                        this.logger.error(sWhere, ioe);
                    } catch (AmazonServiceException ase) {
                        sError = "Upload rejected from Amazon S3 with error: " + ase.getMessage() + ", Status Code: " + ase.getStatusCode();
                        this.logger.error(sWhere, ase);
                    } catch (AmazonClientException ace) {
                        sError = "Error encountered while trying to communicate with Amazon S3";
                        this.logger.error(sWhere, ace);
                    } catch (Exception ee) {
                        sError = "Something went wrong when attempting to upload this file";
                        this.logger.error(sWhere, ee);
                    }
                }
            }
            if (!bUploadSuccess || sError != null) {
                if (!mResults.isEmpty()) {
                    //-Delete any added files
                    for (Map.Entry<String, String> entry : mResults.entrySet()) {
                        deleteFromS3(entry.getValue(), entry.getKey());
                    }
                }
                this.logger.error(sWhere, "File upload failed to: " + sPathOnS3);
                bUploadSuccess = false;
            }
        } catch (Exception e) {
            this.logger.error(sWhere, e);
            throw new Exception(e);
        }
        return bUploadSuccess;
    }

    /**
     * Download a file
     *
     * @param sFilePath - (String)
     * @return s3Object - (S3Object)
     * @throws Exception - If necessary
     */
    public S3Object getFromS3(String sFilePath) throws Exception {
        final String sWhere = "AwsS3Service.getFromS3()";
        try {
            //-Get the object from the AWS server
            AWSCredentials credentials = new BasicAWSCredentials(env.getProperty("s3accesskey"), env.getProperty("s3secretkey"));
            AmazonS3 awsClient = new AmazonS3Client(credentials);
            String sBucket = env.getProperty("s3bucket").substring(5);

            S3Object s3Object = awsClient.getObject(new GetObjectRequest(sBucket, sFilePath));
            return s3Object;
        } catch (Exception e) {
            this.logger.error(sWhere, "Failed to retrieve s3 object with the key name: " + sFilePath + ", Error message: " + e);
            throw new Exception(e);
        }
    }

    /**
     * Delete a file from the S3 server
     *
     * @param sFilePath - (String)
     * @param sClientFileId - (String)
     * @return response - (LmsResponse)
     * @throws Exception - If necessary
     */
    public Boolean deleteFromS3(String sFilePath, String sClientFileId) throws Exception {
        final String sWhere = "AwsS3Service.deleteFromS3()";

        try {
            AWSCredentials credentials = new BasicAWSCredentials(env.getProperty("s3accesskey"), env.getProperty("s3secretkey"));
            AmazonS3 awsClient = new AmazonS3Client(credentials);
            String sBucket = env.getProperty("s3bucket").substring(5);

            String sError = null;
            try {
                awsClient.deleteObject(sBucket, sFilePath);
            } catch (AmazonServiceException ase) {
                sError = "Deletion from Amazon S3 failed with error: " + ase.getMessage() + ", Status Code: " + ase.getStatusCode();
                this.logger.error(sWhere, ase);

            } catch (AmazonClientException ace) {
                sError = "Error encountered while trying to communicate with Amazon S3";
                this.logger.error(sWhere, ace);

            }
            if (sError != null) {
                this.logger.error(sWhere, "Deletion of " + sFilePath + " from S3 failed");
            } else {
                if (sClientFileId != null && !"".equals(sClientFileId)) {

                } else {
                    //-If sClientFileId is null/empty, then delete app.client_file record by sFilePath

                }
            }
        } catch (Exception e) {
            this.logger.error(sWhere, e);

        }
        return true;
    }

    /**
     * Get list of files with a certain prefix
     *
     * @param sPathName - (String)
     * @param bWithDelimiter - (boolean)
     * @return {@literal List<S3ObjectSummary>}
     * @throws Exception - If necessary
     */
    public List<S3ObjectSummary> getFilesInPath(String sPathName, boolean bWithDelimiter) throws Exception {
        final String sWhere = "AwsS3Service.getFilesInPath()";
        List<S3ObjectSummary> s3SumList = new ArrayList<>();
        try {
            AWSCredentials credentials = new BasicAWSCredentials(env.getProperty("s3accesskey"), env.getProperty("s3secretkey"));
            AmazonS3 awsClient = new AmazonS3Client(credentials);
            String sBucket = env.getProperty("s3bucket").substring(5);
            ListObjectsRequest listObjRequest = null;
            if (bWithDelimiter) {
                listObjRequest = new ListObjectsRequest().withBucketName(sBucket).withPrefix(sPathName).withDelimiter("/");
            } else {
                listObjRequest = new ListObjectsRequest().withBucketName(sBucket).withPrefix(sPathName);
            }
            ObjectListing objectListing = awsClient.listObjects(listObjRequest);
            s3SumList = objectListing.getObjectSummaries();
        } catch (Exception e) {
            this.logger.error(sWhere, e);
            throw new Exception(e);
        }
        return s3SumList;
    }

    /**
     * Deletes multiple objects under a certain path in S3
     *
     * @param sFilePath - (String)
     * @return response - (LmsResponse)
     * @throws Exception - If necessary
     */
    public Boolean deleteMultipleFromS3(String sFilePath) throws Exception {
        final String sWhere = "AwsS3Service.deleteMultipleFromS3()";

        try {
            String sError;
            AWSCredentials credentials = new BasicAWSCredentials(env.getProperty("s3accesskey"), env.getProperty("s3secretkey"));
            AmazonS3 awsClient = new AmazonS3Client(credentials);
            String sBucket = env.getProperty("s3bucket").substring(5);
            try {
                ListObjectsRequest listObjRequest = new ListObjectsRequest().withBucketName(sBucket).withPrefix(sFilePath);
                ObjectListing objListing = awsClient.listObjects(listObjRequest);
                while (true) {
                    for (Iterator<?> iterator = objListing.getObjectSummaries().iterator(); iterator.hasNext();) {
                        S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
                        awsClient.deleteObject(sBucket, summary.getKey());

                    }

                    //-More object listing to retrieve?
                    if (objListing.isTruncated()) {
                        objListing = awsClient.listNextBatchOfObjects(objListing);
                    } else {
                        break;
                    }
                }
            } catch (AmazonServiceException ase) {
                sError = "Deletion from Amazon S3 failed with error: " + ase.getMessage() + ", Status Code: " + ase.getStatusCode();
                this.logger.error(sWhere, ase);

            } catch (AmazonClientException ace) {
                sError = "Error encountered while trying to communicate with Amazon S3";
                this.logger.error(sWhere, ace);

            }
        } catch (Exception e) {
            this.logger.error(sWhere, e);
            throw new Exception(e);
        }
        return true;
    }

    /**
     * Determines whether an object exists or not
     *
     * @param sPathName - (String)
     * @return bExists - (boolean)
     * @throws Exception - If necessary
     */
    public boolean doesS3ObjectExist(String sPathName) throws Exception {
        final String sWhere = "AwsS3Service.doesS3ObjectExist()";
        boolean bExists = false;
        try {
            AWSCredentials credentials = new BasicAWSCredentials(env.getProperty("s3accesskey"), env.getProperty("s3secretkey"));
            AmazonS3 awsClient = new AmazonS3Client(credentials);
            String sBucket = env.getProperty("s3bucket").substring(5);

            bExists = awsClient.doesObjectExist(sBucket, sPathName);
        } catch (Exception e) {
            this.logger.error(sWhere, e);
            throw new Exception(e);
        }
        return bExists;
    }
}
