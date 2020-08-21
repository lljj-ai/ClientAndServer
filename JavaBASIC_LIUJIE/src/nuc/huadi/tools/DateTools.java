/*
 *华迪实训第八组
 */
package nuc.huadi.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author liujie
 * @version 1.0
 */
public class DateTools {
	public String Date() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return sdf.format(new Date());
	}
}
