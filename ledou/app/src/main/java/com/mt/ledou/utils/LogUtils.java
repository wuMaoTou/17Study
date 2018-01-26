package com.mt.ledou.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * @des	   日志级别是LEVEL_ALL显示所有信息,包括System.out.println信息
 * @des    日志级别是LEVEL_OFF关闭所有信息,包括System.out.println信息
 */
public class LogUtils {
	/** 日志输出时的TAG */
	private static String mTag = "YGCloud";
	/** 日志输出级别NONE */
	public static final int LEVEL_OFF = 0;
	/** 日志输出级别NONE */
	public static final int LEVEL_ALL = 7;

	/** 日志输出级别V */
	public static final int LEVEL_VERBOSE = 1;
	/** 日志输出级别D */
	public static final int LEVEL_DEBUG = 2;
	/** 日志输出级别I */
	public static final int LEVEL_INFO = 3;
	/** 日志输出级别W */
	public static final int LEVEL_WARN = 4;
	/** 日志输出级别E */
	public static final int LEVEL_ERROR = 5;
	/** 日志输出级别S,自定义定义的一个级别 */
	public static final int LEVEL_SYSTEM = 6;

	/** 是否允许输出log */
	private static int mDebuggable = LogUtils.LEVEL_ALL;

	/** 用于记时的变量 */
	private static long mTimestamp = 0;
	/** 写文件的锁对象 */
	private static final Object M_LOG_LOCK = new Object();

	/**
	 * 获取log点信息
	 * @return
	 */
	private static String generateTag(String mTag) {
		StackTraceElement caller = new Throwable().getStackTrace()[2];
		String tag = "%s.%s(L:%d)";
		String callerClazzName = caller.getClassName();
		callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
		tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
		tag = TextUtils.isEmpty(mTag) ? tag : mTag + ":" + tag;
		return tag;
	}

	/**---------------日志输出,已固定TAG  begin---------------**/
	/** 以级别为 d 的形式输出LOG */
	public static void v(String msg) {
		if (mDebuggable >= LEVEL_VERBOSE) {
			Log.v(generateTag(mTag), msg);
		}
	}

	/** 以级别为 d 的形式输出LOG */
	public static void d(String msg) {
		if (mDebuggable >= LEVEL_DEBUG) {
			Log.d(generateTag(mTag), msg);
		}
	}

	/** 以级别为 i 的形式输出LOG */
	public static void i(String msg) {
		if (mDebuggable >= LEVEL_INFO) {
			Log.i(generateTag(mTag), msg);
		}
	}

	/** 以级别为 w 的形式输出LOG */
	public static void w(String msg) {
		if (mDebuggable >= LEVEL_WARN) {
			Log.w(generateTag(mTag), msg);
		}
	}

	/** 以级别为 w 的形式输出Throwable */
	public static void w(Throwable tr) {
		if (mDebuggable >= LEVEL_WARN) {
			Log.w(generateTag(mTag), "", tr);
		}
	}

	/** 以级别为 w 的形式输出LOG信息和Throwable */
	public static void w(String msg, Throwable tr) {
		if (mDebuggable >= LEVEL_WARN && null != msg) {
			Log.w(generateTag(mTag), msg, tr);
		}
	}

	/** 以级别为 e 的形式输出LOG */
	public static void e(String msg) {
		if (mDebuggable >= LEVEL_ERROR) {
			Log.e(generateTag(mTag), msg);
		}
	}

	/** 以级别为 s 的形式输出LOG,主要是为了System.out.println,稍微格式化了一下 */
	public static void sf(String msg) {
		if (mDebuggable >= LEVEL_ERROR) {
			System.out.println("----------" + msg + "----------");
		}
	}

	/** 以级别为 s 的形式输出LOG,主要是为了System.out.println */
	public static void s(String msg) {
		if (mDebuggable >= LEVEL_ERROR) {
			System.out.println(msg);
		}
	}

	/** 以级别为 e 的形式输出Throwable */
	public static void e(Throwable tr) {
		if (mDebuggable >= LEVEL_ERROR) {
			Log.e(generateTag(mTag), "", tr);
		}
	}

	/** 以级别为 e 的形式输出LOG信息和Throwable */
	public static void e(String msg, Throwable tr) {
		if (mDebuggable >= LEVEL_ERROR && null != msg) {
			Log.e(generateTag(mTag), msg, tr);
		}
	}

	/**---------------日志输出,已固定TAG  end---------------**/

	/**---------------日志输出,未固定TAG  begin---------------**/
	/** 以级别为 d 的形式输出LOG */
	public static void v(String tag, String msg) {
		if (mDebuggable >= LEVEL_VERBOSE) {
			Log.v(generateTag(tag), msg);
		}
	}

	/** 以级别为 d 的形式输出LOG */
	public static void d(String tag, String msg) {
		if (mDebuggable >= LEVEL_DEBUG) {
			Log.d(generateTag(tag), msg);
		}
	}

	/** 以级别为 i 的形式输出LOG */
	public static void i(String tag, String msg) {
		if (mDebuggable >= LEVEL_INFO) {
			Log.i(generateTag(tag), msg);
		}
	}

	/** 以级别为 w 的形式输出LOG */
	public static void w(String tag, String msg) {
		if (mDebuggable >= LEVEL_WARN) {
			Log.w(generateTag(tag), msg);
		}
	}

	/** 以级别为 e 的形式输出LOG */
	public static void e(String tag, String msg) {
		if (mDebuggable >= LEVEL_ERROR) {
			Log.e(generateTag(tag), msg);
		}
	}

	/**---------------日志输出,未固定TAG  end---------------**/

	/**
	 * 把Log存储到文件中
	 * 
	 * @param log
	 *            需要存储的日志
	 * @param path
	 *            存储路径
	 */
	public static void log2File(String log, String path) {
		log2File(log, path, true);
	}

	public static void log2File(String log, String path, boolean append) {
		synchronized (M_LOG_LOCK) {
			FileUtils.writeFile(log + "\r\n", path, append);
		}
	}

	/**
	 * 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段起始点
	 * 
	 * @param msg
	 *            需要输出的msg
	 */
	public static void msgStartTime(String msg) {
		mTimestamp = System.currentTimeMillis();
		if (!TextUtils.isEmpty(msg)) {
			e("[Started：" + mTimestamp + "]" + msg);
		}
	}

	/** 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段结束点* @param msg 需要输出的msg */
	public static void elapsed(String msg) {
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - mTimestamp;
		mTimestamp = currentTime;
		e("[Elapsed：" + elapsedTime + "]" + msg);
	}

	public static <T> void printList(List<T> list) {
		if (list == null || list.size() < 1) {
			return;
		}
		int size = list.size();
		i("---begin---");
		for (int i = 0; i < size; i++) {
			i(i + ":" + list.get(i).toString());
		}
		i("---end---");
	}

	public static <T> void printArray(T[] array) {
		if (array == null || array.length < 1) {
			return;
		}
		int length = array.length;
		i("---begin---");
		for (int i = 0; i < length; i++) {
			i(i + ":" + array[i].toString());
		}
		i("---end---");
	}
}
