package rei.whosoonhwang;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * FileUtil test
 * @author hshwang
 * @since 2017. 11. 30.
 */
public class FileUtilTest {
	@Test
	public void testBytesToString() {
		final int num_digits = 2;
		final ClassLoader classLoader = getClass().getClassLoader();
		final File file = new File(classLoader.getResource("ayanamirei.jpg").getFile());

		System.out.println(file.length());
		System.out.println(FileUtil.bytesToString(file.length(), num_digits));
		System.out.println(FileUtil.bytesToString(Long.MIN_VALUE, num_digits));
				
		FileUtil.fseList.stream()
				.map(FileSizeExpression::getSize)
				.forEach(size -> System.out.println(FileUtil.bytesToString(size, num_digits)));
		
		System.out.println();
		final List<Long> sizes = Arrays.asList
				(FileUtil.SPACE_KB, FileUtil.SPACE_MB, FileUtil.SPACE_GB, FileUtil.SPACE_TB, Long.MAX_VALUE);		
		sizes.stream().forEach(size -> System.out.println(FileUtil.bytesToString(size, num_digits)));

		System.out.println();
		System.out.println("다스는 누구꺼? : " + FileUtil.bytesToString(FileUtil.SPACE_MB+FileUtil.SPACE_MB, num_digits));
	}
}
