package rei.whosoonhwang;

import static org.junit.Assert.*;

import java.io.File;
import org.junit.Test;
import com.amazonaws.regions.Regions;

/**
 * AWSFileUtil class test
 * @author hshwang
 * @since 2017. 11. 30.
 */
public final class AWSFileUtilTest {
		
	@Test
	public void testFileUpload() {		
		final Regions region = Regions.AP_NORTHEAST_2;
		final String s3BucketName = "your-aws-s3-bucket-name";
		final String path = "/your/aws/s3/bucket/path";
		final ClassLoader classLoader = getClass().getClassLoader();
		final File file = new File(classLoader.getResource("ayanamirei.jpg").getFile());		
		final boolean publicYn = false;
		final boolean encryptionYn = true;		
		final boolean expected = true;
		
		boolean isDone = AWSS3Util.uploadFileToS3(region, s3BucketName, path, file, publicYn, encryptionYn);
		
		assertEquals(isDone, expected);
		
		System.out.println("origin size : " + FileUtil.bytesToString(file.length(), 2));
	}
	
	@Test
	public void testFileDownload() {		
		final Regions region = Regions.AP_NORTHEAST_2;
		final String s3BucketName = "your-aws-s3-bucket-name";
		final String path = "/your/aws/s3/bucket/path";
		final String fileUid = "ayanamirei.jpg";
		final String savePath = System.getProperty("user.home") + "/aws_temp/";
		final String saveFileName = "ayanamirei_from_s3.jpg";
		
		final File file = AWSS3Util.downloadFileFromS3(region, s3BucketName, path, fileUid, savePath, saveFileName);
		System.out.println("remote size : " + FileUtil.bytesToString(file.length(), 2));
	}

	@Test
	public void testFileUploadDownload() {
		testFileUpload();
		testFileDownload();
	}
}
