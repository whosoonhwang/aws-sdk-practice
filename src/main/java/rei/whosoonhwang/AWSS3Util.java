package rei.whosoonhwang;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

/**
 * AWS S3 util class
 * @author hshwang
 * @since 2017. 11. 30.
 */
public final class AWSS3Util {
	
	/**
	 * build for amazons3 client
	 * @param region
	 * @return
	 * @since 2017. 11. 30.
	 */
	private static AmazonS3 buildS3Client(final Regions region) {
		return AmazonS3ClientBuilder.standard()
				.withRegion(region)
				.build();
	}
	
	/**
	 * to s3 file path
	 * @param s3BucketName
	 * @param path
	 * @return
	 * @since 2017. 11. 30.
	 */
	private static String convertS3FilePath(final String s3BucketName, final String path){
		return s3BucketName + path.replace("\\", "/");
	}
	
	/**
	 * check for file existence
	 * @param s3Client
	 * @param s3Path
	 * @param fileName
	 * @return
	 * @since 2017. 11. 30.
	 */
	public static boolean existS3File(final AmazonS3 s3Client
									, final String s3Path
									, final String fileName) {
		
	    try {			
	    	s3Client.getObjectMetadata(s3Path, fileName); 
	    } catch(final AmazonServiceException e) {
	    	return false;
	    }
	    return true;
	}
	
	/**
	 * check for file existence
	 * @param region
	 * @param s3BucketName
	 * @param path
	 * @param fileName
	 * @return
	 * @since 2017. 11. 30.
	 */
	public static boolean existS3File(final Regions region
				 					, final String s3BucketName
									, final String path
									, final String fileName) {
		
		final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withRegion(region)
				.build();

		final String s3Path = convertS3FilePath(s3BucketName, path);		
    return existS3File(s3Client, s3Path, fileName);
	}

	/**
	 * file upload to AWS-S3
	 * @param region
	 * @param s3BucketName
	 * @param path
	 * @param file
	 * @param publicYn
	 * @param encryptionYn
	 * @return
	 * @throws RuntimeException
	 * @since 2017. 11. 30.
	 */
	public static boolean uploadFileToS3(final Regions region
										, final String s3BucketName
										, final String path
										, final File file
										, final boolean publicYn
										, final boolean encryptionYn) throws RuntimeException {
		boolean isDone = false;
		try {
			final AmazonS3 s3Client = buildS3Client(region);
			
			final TransferManager tm = TransferManagerBuilder.standard()
					.withS3Client(s3Client)
					.build();
			
			final String s3Path = convertS3FilePath(s3BucketName, path);
			
			final PutObjectRequest putObjectRequest = new PutObjectRequest(s3Path, file.getName(), file);
			final ObjectMetadata objectMetadata = new ObjectMetadata();
			if (encryptionYn) objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
			if (publicYn) putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
			putObjectRequest.setMetadata(objectMetadata);
			
			final Upload upload = tm.upload(putObjectRequest);
			upload.waitForCompletion();
			isDone = upload.isDone();
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
		return isDone;
	}
	
	/**
	 * file download from AWS-S3
	 * @param region
	 * @param s3BucketName
	 * @param path
	 * @param fileUid
	 * @param savePath
	 * @param saveFileName
	 * @return
	 * @since 2017. 11. 30.
	 */
	public static File downloadFileFromS3(final Regions region
											, final String s3BucketName
											, final String path
											, final String fileUid
											, final String savePath
											, final String saveFileName) {

		final AmazonS3 s3Client = buildS3Client(region);
		
		final String s3Path = convertS3FilePath(s3BucketName, path);
		if (!existS3File(s3Client, s3Path, fileUid)) return null;
		
		final File dir = new File(savePath);
		if (!dir.exists()) dir.mkdirs();
		
		final File file = new File(savePath, saveFileName);		
		final S3Object s3Object = s3Client.getObject(new GetObjectRequest(s3Path, fileUid));
		
		return convertS3ObjectToFile(s3Object, file);
	}
	
	/**
	 * convert s3object to file
	 * @param s3Object
	 * @param file
	 * @return
	 * @since 2017. 11. 30.
	 */
	private static File convertS3ObjectToFile(final S3Object s3Object, final File file) {
		try (
				final S3ObjectInputStream inputStream = s3Object.getObjectContent();
				final OutputStream outStream = new FileOutputStream(file)
			){
		    
		    byte[] buf = new byte[1024];
		    int len = 0;
		    
		    while ((len = inputStream.read(buf)) > 0){
		       outStream.write(buf, 0, len);
		    }
		    
		    return file;
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
