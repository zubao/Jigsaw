package com.lipeilong.jigsaw.log;


import com.lipeilong.jigsaw.util.FileUtil;
import com.lipeilong.jigsaw.util.PathUtil;

/**
 * 设置CrashHandler的相关目录
 *
 */
public class CrashHandlerConfig {

	/**
	 * 崩溃日志保存路径
	 */
	private static final String CRASH_LOG_DIR = PathUtil.getCacheDir() + "/crash" ;
	
	
	/**
	 * 是否处理奔溃
	 */
	public static final boolean CRASH_LOG = true ; // PRINT_LOG;
	
	/**
	 * 获取保存日志的位置
	 * @return
	 */
	public static String getCrashLogDir() {
		String dir = CRASH_LOG_DIR;
		FileUtil.ensureDir(dir);
		return dir;
	}
}
