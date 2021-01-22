package com.acg.minio.util;

import com.acg.minio.config.MinioConfig;
import com.acg.minio.errors.InvalidExpiresRangeException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class MinIoUtil implements InitializingBean {


    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    private static final int DEFAULT_EXPIRY_TIME = 7 * 24 * 3600;

    /*@PostConstruct
    public void init(){
        try {
            if (minioClient == null){
                System.out.println("Minio Initialize.......................");
                minioClient = MinioClient.builder().endpoint(minioConfig.getEndpoint()).credentials(minioConfig.getAccessKey(),minioConfig.getSecretKey()).build();
                System.out.println("Minio Start.......................");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    /**
     * 检查存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ServerException
     * @throws XmlParserException
     */
    public boolean bucketExists(String bucketName) throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            ServerException,
            XmlParserException{
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ServerException
     * @throws XmlParserException
     */
    public boolean makeBucket(String bucketName) throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            ServerException,
            XmlParserException {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 列出所有存储桶名称
     *
     * @return
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ServerException
     * @throws XmlParserException
     */
    public List<String> listBucketNames() throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            ServerException,
            XmlParserException {
        List<Bucket> bucketList = minioClient.listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }

    /**
     * 列出所有存储桶
     *
     * @return
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ServerException
     * @throws XmlParserException
     */
    public List<Bucket> listBuckets() throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            ServerException,
            XmlParserException {
        return minioClient.listBuckets();
    }

    /**
     * 删除存储桶
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ServerException
     * @throws XmlParserException
     */
    public boolean removeBucket(String bucketName) throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            ServerException,
            XmlParserException {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                // 有对象文件，则删除失败
                if (item.size() > 0) {
                    return false;
                }
            }
            // 删除存储桶，注意，只有存储桶为空时才能删除成功。
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            flag = bucketExists(bucketName);
            if (!flag) {
                return true;
            }
        }
        return false;
    }

    /**
     * 列出存储桶中的所有对象名称
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */


    public List<String> listObjectNames(String bucketName) throws IOException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, ServerException, ErrorResponseException, XmlParserException, InsufficientDataException, InternalException {
        List<String> listObjectNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectNames.add(item.objectName());
            }
        }
        return listObjectNames;
    }

    /**
     * 列出存储桶中的所有对象
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public Iterable<Result<Item>> listObjects(String bucketName) throws IOException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, ServerException, ErrorResponseException, XmlParserException, InsufficientDataException, InternalException {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            return minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        }
        return null;
    }

    /**
     * 通过InputStream上传对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param stream     要上传的流
     * @param contentType 上传文件的格式，若是不设定格式默认都是application/octet-stream， 浏览器访问直接下载
     * @return
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ServerException
     * @throws XmlParserException
     */
    public boolean putObject(String bucketName, String objectName, InputStream stream, String contentType) throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            ServerException,
            XmlParserException {

        boolean flag = bucketExists(bucketName);

        if (flag) {
            minioClient.putObject( PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                    stream, stream.available(), -1)
                    .contentType(contentType)
                    .build());
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 以流的形式获取一个文件对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public InputStream getObject(String bucketName, String objectName) throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            ServerException,
            XmlParserException {

        boolean flag = bucketExists(bucketName);

        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                InputStream stream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
                return stream;
            }
        }
        return null;
    }

    /**
     * 以流的形式获取一个文件对象（断点下载）
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param offset     起始字节的位置
     * @param length     要读取的长度 (可选，如果无值则代表读到文件结尾)
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public InputStream getObject(String bucketName, String objectName, long offset, Long length) throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            ServerException,
            XmlParserException {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).offset(offset).length(length).build());
            }
        }
        return null;
    }

    /**
     * 删除一个对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ServerException
     * @throws XmlParserException
     */
    public boolean removeObject(String bucketName, String objectName) throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            ServerException,
            XmlParserException {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return true;
        }
        return false;
    }

    /**
     * 删除指定桶的多个文件对象,返回删除错误的对象列表，全部删除成功，返回空列表
     *
     * @param bucketName  存储桶名称
     * @param objects 含有要删除的多个object名称的迭代器对象
     * @return
     * @throws InvalidKeyException
     * @throws ErrorResponseException
     * @throws IllegalArgumentException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidResponseException
     * @throws NoSuchAlgorithmException
     * @throws XmlParserException
     * @throws IOException
     */
    public List<String> removeObject(String bucketName, List<DeleteObject> objects) throws IOException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, ServerException, ErrorResponseException, XmlParserException, InsufficientDataException, InternalException {
        List<String> deleteErrorNames = new LinkedList<>();
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder()
                    .bucket(bucketName)
                    .objects(objects)
                    .build());
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                deleteErrorNames.add(error.objectName());
            }
        }
        return deleteErrorNames;
    }

    /**
     * 生成一个给HTTP GET请求用的presigned URL。
     * 浏览器/移动端的客户端可以用这个URL进行下载，即使其所在的存储桶是私有的。这个presigned URL可以设置一个失效时间，默认值是7天。
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param expires    失效时间（以秒为单位），默认是7天，不得大于七天
     * @return
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws XmlParserException
     * @throws ServerException
     */
    public String getPresignedObjectUrl(String bucketName, String objectName, Integer expires) throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            XmlParserException,
            ServerException,
            InvalidExpiresRangeException {
        boolean flag = bucketExists(bucketName);
        String url = "";
        if (flag) {
            if (expires < 1 || expires > DEFAULT_EXPIRY_TIME) {
                throw new InvalidExpiresRangeException(expires,"expires must be in range of 1 to " + DEFAULT_EXPIRY_TIME);
            }
            url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(expires)
                    .build());
        }
        return url;
    }

    /**
     * 生成一个给HTTP PUT请求用的presigned URL。
     * 浏览器/移动端的客户端可以用这个URL进行上传，即使其所在的存储桶是私有的。这个presigned URL可以设置一个失效时间，默认值是7天。
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param expires    失效时间（以秒为单位），默认是7天，不得大于七天
     * @return
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws XmlParserException
     * @throws ServerException
     */
    public String getPresignedObjectGetPutUrl(String bucketName, String objectName, Integer expires) throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            XmlParserException,
            ServerException {
        boolean flag = bucketExists(bucketName);
        String url = "";
        if (flag) {
            if (expires < 1 || expires > DEFAULT_EXPIRY_TIME) {
                throw new RuntimeException("expires must be in range of 1 to " + DEFAULT_EXPIRY_TIME);
            }
            Map<String, String> reqParams = new HashMap<String, String>();
            reqParams.put("response-content-type", "application/json");
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expires)
                            .extraQueryParams(reqParams)
                            .build());
        }
        return url;
    }

    /**
     * 获取对象的元数据
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ServerException
     * @throws XmlParserException
     */
    public StatObjectResponse statObject(String bucketName, String objectName) throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            ServerException,
            XmlParserException{
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName).build());
            return statObject;
        }
        return null;
    }

    /**
     * 文件访问路径
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws XmlParserException
     * @throws ServerException
     */
    public String presignedGetObject(String bucketName, String objectName) throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            XmlParserException,
            ServerException{
        boolean flag = bucketExists(bucketName);
        String url = "";
        if (flag) {
            url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucketName).object(objectName).build());
        }
        return url;
    }

    /**
     *  设定存储桶策略
     *
     * @param bucketName
     * @param policyJson
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ServerException
     * @throws XmlParserException
     */
    public void setBucketPolicy(String bucketName, String policyJson) throws ErrorResponseException,
            InsufficientDataException,
            InternalException,
            InvalidKeyException,
            InvalidResponseException,
            IOException,
            NoSuchAlgorithmException,
            ServerException,
            XmlParserException {
        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policyJson)
                        .build());
    }

    /**
     * 获得上传的URL
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param expires
     * @return
     * @throws ErrorResponseException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws XmlParserException
     * @throws ServerException
     */
    public String presignedPutObject(String bucketName, String objectName, Integer expires) throws ErrorResponseException,InsufficientDataException,InternalException,InvalidKeyException,InvalidResponseException,IOException,NoSuchAlgorithmException,XmlParserException,ServerException {
        if(expires == null){
            // 7天
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.PUT).bucket(bucketName).object(objectName).expiry(expires).build());
            //minioClient.presignedPutObject(bucketName,objectName);
        }
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.PUT).bucket(bucketName).object(objectName).expiry(expires).build());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //this.minioClient = new MinioClient(minioConfig.getEndpoint(), minioConfig.getAccessKey(), minioConfig.getSecretKey());
        this.minioClient = MinioClient.builder().endpoint(minioConfig.getEndpoint()).credentials(minioConfig.getAccessKey(),minioConfig.getSecretKey()).build();
    }
}
