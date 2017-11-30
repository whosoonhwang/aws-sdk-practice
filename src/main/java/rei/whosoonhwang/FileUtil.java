package rei.whosoonhwang;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * File util class
 * @author hshwang
 * @since 2017. 11. 30.
 */
public final class FileUtil {	
	public static final long SPACE_KB = 1024;
	public static final long SPACE_MB = 1024 * SPACE_KB;
	public static final long SPACE_GB = 1024 * SPACE_MB;
	public static final long SPACE_TB = 1024 * SPACE_GB;
	
	public static final long [] sizes =  { SPACE_KB, SPACE_MB, SPACE_GB, SPACE_TB, Long.MAX_VALUE };
	public static final String [] expressions = { "bytes", "Kb", "Mb", "Gb", "Tb" };
	public static final double [] denominators = { 1, SPACE_KB, SPACE_MB, SPACE_GB, SPACE_TB };
	
	public static final List<FileSizeExpression> fseList;
	static {
		final int length = sizes.length;
		final FileSizeExpression [] list = new FileSizeExpression[length];
		for (int i=0 ; i<length ; ++i) {
			list[i] = FileSizeExpression.of(sizes[i], expressions[i], denominators[i]);
		}
		fseList = Collections.unmodifiableList(Arrays.asList(list));
	}	
	
	/**
	 * file size to string expression
	 * @param byteSize
	 * @param num_digits
	 * @return
	 * @since 2017. 11. 30.
	 */
	public static String bytesToString(final long byteSize, final int num_digits) {
		if (byteSize <= 0) return "0byte";
		
		final NumberFormat nf = new DecimalFormat();
	    nf.setMaximumFractionDigits(num_digits);
	    
		return fseList.stream()
				.filter(fse -> byteSize < fse.getSize())
				.findFirst()
				.map(fse -> nf.format(byteSize/fse.getDenominator()) + fse.getExpression())
				.orElseGet(() -> "Long.MAX_VALUE 사이즈의 파일이 만들어지는 세상");
	}
}

final class FileSizeExpression {
	private final Long size;
	private final String expression;
	private final Double denominator;
	
	private FileSizeExpression(final Long size, final String expression, final Double denominator) {
		this.size = size;
		this.expression = expression;
		this.denominator = denominator;
	}
	
	public static FileSizeExpression of(final Long size, final String expression, final Double denominator) {
		return new FileSizeExpression(size, expression, denominator);
	}

	public Long getSize() {
		return size;
	}

	public String getExpression() {
		return expression;
	}

	public Double getDenominator() {
		return denominator;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("size : " + this.size);
		sb.append("\t");
		sb.append("expression : " + this.expression);
		sb.append("\t");
		sb.append("denominator : " + this.denominator);
		return sb.toString();
	}
}