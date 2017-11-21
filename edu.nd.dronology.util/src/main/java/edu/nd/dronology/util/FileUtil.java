// $HeadURL$
// $Revision$
// $Date$
// $Author$
package edu.nd.dronology.util;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.io.IOUtils;

import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class FileUtil {

	protected static ILogger LOGGER = LoggerProvider.getLogger(FileUtil.class);
	public static final String LINE_SEP = System.lineSeparator();

	public static final String DIR_SEP = "/";

	/**
	 * Write a string to a file. Overwrite previous content. Create the file if
	 * it does not exist.
	 * 
	 * @param str
	 *            The string to write.
	 * @param file
	 *            The file where the string is written to.
	 */
	public static void writeStringToFile(String str, File file) {
		writeStringToFile(str, file, false);
	}

	/**
	 * Write a string to a file. Overwrite previous content. Create the file if
	 * it does not exist.
	 * 
	 * @param str
	 *            The string to write.
	 * @param file
	 *            The file where the string is written to.
	 * @param append
	 *            append to existing file content.
	 */
	public static void writeStringToFile(String str, File file, boolean append) {
		PrintWriter out = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new PrintWriter(new FileWriter(file, append));
			out.println(str);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	/**
	 * Write a string to a file. Overwrite previous content. Create the file if
	 * it does not exist.
	 * 
	 * @param str
	 *            The string to write.
	 * @param resourceUrl
	 *            The URL of the file where the string is written to.
	 */
	public static void writeStringToFile(String str, URL resourceUrl) {
		writeStringToFile(str, resourceUrl.getPath());

	}

	/**
	 * Write a string to a file. Overwrite previous content. Create the file if
	 * it does not exist.
	 * 
	 * @param str
	 *            The string to write.
	 * @param filePath
	 *            The path to the file where the string is written to.
	 */
	public static void writeStringToFile(String str, String filePath) {
		writeStringToFile(str, new File(filePath));
	}

	/**
	 * Read a file and return its contents as a string.
	 * 
	 * @param absolutePath
	 *            The absolute path to the file.
	 * @return The contents of the file.
	 */
	public static String readFile(String absolutePath) {
		StringBuilder contents = new StringBuilder();
		BufferedReader buf = getBufferedFileReader(absolutePath);
		String line;
		try {
			while (buf != null && (line = buf.readLine()) != null) {
				contents.append(line + LINE_SEP);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contents.toString();
	}

	public static String readFile(InputStream input) {
		String contents = null;
		try {
			contents = IOUtils.toString(input, "UTF-8");
		} catch (IOException e) {
			LOGGER.error(e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}
		return contents;
	}

	/**
	 * Get a buffered reader for a file.
	 * 
	 * @param absolutePath
	 *            The absolute path to the file that will be read.
	 * @return The buffered reader for the file.
	 */
	public static BufferedReader getBufferedFileReader(String absolutePath) {
		FileReader fr = null;
		BufferedReader buf = null;
		try {
			String path = absolutePath;
			fr = new FileReader(path);
			buf = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			LOGGER.warn("Could not read file: " + absolutePath);

		}
		return buf;
	}

	public static String getFileName(final String file) {
		String fname = reverseBackSlashes(file);
		if (fname.lastIndexOf("/") != -1) {
			return fname.substring(fname.lastIndexOf("/") + 1, fname.length());
		}
		return fname;// StringUtils.EMPTY

	}

	public static String getFileName(final File file) {
		return getFileName(file.getAbsolutePath());
	}

	public static String getFileNameWithoutExtension(final File file) {
		String fname = file.getAbsolutePath().toString();
		fname = reverseBackSlashes(fname);
		if (fname.lastIndexOf("/") != -1) {
			fname = fname.substring(fname.lastIndexOf("/") + 1, fname.length());
		}
		if (fname.lastIndexOf(".") != -1) {
			fname = fname.substring(0, fname.lastIndexOf("."));
		}

		return fname;// StringUtils.EMPTY

	}

	/**
	 * makes backslashes in toReverse to slashes used for making a path an URL
	 * 
	 * @param toReverse
	 * @return reversed String (slashes instead of backslashes)
	 */
	public static String reverseBackSlashes(String toReverse) {
		return toReverse.replace('\\', '/');
	}

	public static String reverseSlashes(String toReverse) {
		return toReverse.replace('/', '\\');
	}

	public static String getFileName(URL fileURL) {
		return getFileName(fileURL.getFile());
	}

	/**
	 * copies file lying at "inputPathAndFileName" to "outputPathAndFileName"
	 * 
	 * @param inputPathAndFileName
	 * @param outputPathAndFileName
	 * @return {@code true} if copying went fine.
	 * @throws IOException
	 */
	public static boolean copyFile(String inputPathAndFileName, String outputPathAndFileName) {
		FileChannel in = null;
		FileChannel out = null;
		FileInputStream fin = null;
		FileOutputStream fout = null;
		try {
			fin = new FileInputStream(inputPathAndFileName);
			fout = new FileOutputStream(outputPathAndFileName);
			in = fin.getChannel();
			out = fout.getChannel();
			ByteBuffer bb = ByteBuffer.allocateDirect(4096);
			while (in.read(bb) != -1) {
				bb.flip();
				out.write(bb);
				bb.clear();
			}
			return true;
		} catch (IOException e) {
			LOGGER.error("Can't copy file", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.warn("Can't close stream", e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOGGER.warn("Can't close stream", e);
				}
			}
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}
		return false;
	}

	public static boolean copyFile(InputStream input, String destination) {
		try {
			OutputStream out;

			out = new FileOutputStream(new File(destination));

			byte[] buf = new byte[1024];
			int len;
			while ((len = input.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			input.close();
			out.close();
		} catch (FileNotFoundException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param source
	 * @param dest
	 * @return {@code true} if copying went fine.
	 * @throws IOException
	 */
	public static boolean copyFile(URL source, String dest) throws IOException {
		FileChannel in = null;
		FileChannel out = null;
		FileInputStream fis = null;
		FileOutputStream fout = null;
		try {
			fis = new FileInputStream(new File(source.toURI()).getAbsolutePath());
			fout = new FileOutputStream(dest);
			in = fis.getChannel();
			out = fout.getChannel();
			ByteBuffer bb = ByteBuffer.allocateDirect(4096);
			while (in.read(bb) != -1) {
				bb.flip();
				out.write(bb);
				bb.clear();
			}
			return true;
		} catch (URISyntaxException e) {
			LOGGER.error(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}
		return false;
	}

	public static byte[] toByteArray(File file) {
		// LOGGER.info("File '" + file.getAbsolutePath() + "' requested");
		FileInputStream stream = null;
		try {
			if (!file.exists()) {
				LOGGER.info("File not found: " + file.getPath());
				return null;
			}

			stream = new FileInputStream(file);
			return IOUtils.toByteArray(stream);
		} catch (IOException e) {
			LOGGER.error(e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}

		return new byte[0];
	}

	public static boolean saveByteArrayToFile(File file, byte[] content) {
		FileOutputStream stream = null;
		try {
			if (file.exists()) {
				LOGGER.info("Deleting old file");
				file.delete();
			}
			stream = new FileOutputStream(file.getPath());
			stream.write(content);
			stream.flush();

			LOGGER.info("File saved@ " + file.getPath());
			return true;
		} catch (IOException e) {
			LOGGER.error(e);
			return false;
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			System.gc();
		}
	}

	public static String getExtension(String fname) {
		fname = reverseBackSlashes(fname);
		if (fname.lastIndexOf(".") != -1) {
			fname = fname.substring(fname.lastIndexOf(".") + 1, fname.length());
		} else {
			return null;
		}
		return fname;// StringUtils.EMPTY

	}

	public static String appendExtension(String filename, String extension) {
		NullUtil.checkNull(filename, extension);
		return filename + "." + extension;
	}

	public static String concat(String path, String filename, String extension) {
		return reverseBackSlashes(path) + "/" + filename + "." + extension;
	}

	public static String concatPath(String... path) {
		NullUtil.checkArrayNull(path);
		StringBuilder sb = new StringBuilder();
		for (String s : path) {
			sb.append(reverseBackSlashes(s));
			sb.append(File.separator);
		}
		return sb.toString();
	}

	public static void openExplorer(String path) {
		String directory = reverseSlashes(path);
		try {
			Runtime.getRuntime().exec("cmd.exe /C " + "call explorer " + directory);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
