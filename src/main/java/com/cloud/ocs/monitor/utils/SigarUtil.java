package com.cloud.ocs.monitor.utils;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.hyperic.sigar.Sigar;

/**
 * 将Sigar的本地库文件路径添加到系统变量java.library.path中
 * 
 * @author Wang Chao
 *
 * @date 2014-12-14 上午12:27:51
 *
 */
public class SigarUtil {

	public final static Sigar sigar = initSigar();

    private static Sigar initSigar() {
        try {
        	String filePath = SigarUtil.class.getClassLoader().getResource("/sigar/.sigar_shellrc").getPath();
            File classPath = new File(filePath).getParentFile();

            String path = System.getProperty("java.library.path");
            if (SystemUtils.IS_OS_WINDOWS) {
                path += ";" + classPath.getCanonicalPath();
            } else {
                path += ":" + classPath.getCanonicalPath();
            }
            System.setProperty("java.library.path", path);

            return new Sigar();
        } catch (Exception e) {
            return null;
        }
    }
}
