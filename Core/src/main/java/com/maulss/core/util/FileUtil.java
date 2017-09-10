package com.maulss.core.util;

import org.apache.commons.lang3.Validate;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class FileUtil {

    /**
     * Codename of {@code UTF-8} encoding, mainly used for parameters
     * in new {@code String} initialization.
     */
    private final static String UTF8 = "UTF-8";

    /* Disable initialization */
    private FileUtil() {}

    /**
     * Reads the file entirely and returns its content in the form of
     * a {@code byte} array.
     *
     * If the file itself doesn't exist, a new empty file will be
     * generated via {@link File#createNewFile()} with the empty
     * contents of it still being returned.
     *
     * If the directories for the file don't exist, they will all
     * be generated, and then the blank file itself will also be made.
     *
     * @param	file
     * 			The file that is to be read or generated.
     *
     * @return	The contents of the file in the form of {@code byte[]}.
     *
     * @throws	IOException
     * 			If an I/O problem occurs while reading the file.
     * @throws	FileNotFoundException
     * 			If the {@param file} parameter is found to be a directory.
     *
     * @see		#read(File)
     * @see		#utf8(byte[])
     */
    public static byte[] readBytes(final File file) throws IOException {
        checkCreate(file);
        int length = (int) file.length();
        byte[] output = new byte[length];
        InputStream in = new FileInputStream(file);
        int offset = 0;
        // normally it should be able to read the entire file with just a single
        // iteration below, but it depends on the whims of the FileInputStream
        while (offset < length) {
            offset += in.read(output, offset, (length - offset));
        }
        in.close();
        return output;
    }

    /**
     * Writes the {@code byte[]} into the contents of the specified file.
     *
     * The content inside the file previous to the output will be saved,
     * as the new content will be added instead of replaced.
     *
     * If the file itself doesn't exist, a new empty file will be
     * generated via {@link File#createNewFile()}.
     *
     * If the directories for the file don't exist, they will all
     * be generated, and then the blank file itself will also be made.
     *
     * @param	file
     * 			The file that is to be read or generated.
     * @param	bytes
     * 			The contents that are to be outputted into the file.
     *
     * @throws	IOException
     * 			If an I/O problem occurs while writing to file.
     * @throws	FileNotFoundException
     * 			If the {@param file} parameter is found to be a directory.
     *
     * @see		#write(File, String)
     * @see		#utf8(String)
     */
    public static void writeBytes(final File file,
                                  final byte[] bytes) throws IOException {
        checkCreate(file);
        OutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();
    }

    public static void write(final File file,
                             final String content) throws IOException {
        writeBytes(file, utf8(content));
    }

    public static String read(final File file) throws IOException {
        return utf8(readBytes(file));
    }

    public static boolean deleteRecursive(final File path) throws FileNotFoundException {
        Validate.notNull(path);
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());

        boolean ret = true;
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                ret = ret && deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }

    public static byte[] utf8(final String string) {
        Validate.notNull(string);

        try {
            return string.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts an array of bytes into a {@code UTF-8 String}.
     *
     * @param bytes The bytes to
     * @return {@code UTF-8 String} from bytes
     */
    public static String utf8(final byte[] bytes) {
        Validate.notNull(bytes);

        try {
            return new String(bytes, UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Copy a directory which does not necessarily has to be empty.
     *
     * @param   src    Previous directory
     * @param   target New directory
     *
     * @throws  IOException
     *          If the copy procedure has failed.
     */
    public static void copy(final File src,
                            final File target) throws IOException {
        Validate.notNull(src);
        Validate.notNull(target);

        if (src.isDirectory()) {
            if (!target.exists()) target.mkdir();

            for (String child : src.list())
                copy(new File(src, child), new File(target, child));

        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(target);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            out.close();
            in.close();
        }
    }

    /**
     * @see File#createNewFile()
     */
    private static boolean checkCreate(final File file) {
        Validate.notNull(file);

        if (!file.exists()) {
            file.mkdirs();
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static void unzip(final String zipFilePath,
                             final String outputDirectory) {
        Validate.notNull(zipFilePath);
        Validate.notNull(outputDirectory);

        InputStream 	fileInputStream 		= null;
        InputStream 	bufferedInputStream 	= null;
        ZipInputStream 	zipInputStream 			= null;
        OutputStream 	fileOutputStream 		= null;
        OutputStream 	bufferedOutputStream 	= null;

        try {
            fileInputStream 	= new FileInputStream(zipFilePath);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            zipInputStream 		= new ZipInputStream(bufferedInputStream);
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                byte[] buffer = new byte[2048];

                fileOutputStream 		= new FileOutputStream(outputDirectory + File.separator + entry.getName());
                bufferedOutputStream 	= new BufferedOutputStream(fileOutputStream, buffer.length);

                int size;
                while ((size = zipInputStream.read(buffer, 0, buffer.length)) != -1) {
                    bufferedOutputStream.write(buffer, 0, size);
                }

                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                fileOutputStream.flush();
                fileOutputStream.close();
            }

            zipInputStream.close();
            bufferedInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (fileInputStream != null) try {
                fileInputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (bufferedInputStream != null) try {
                bufferedInputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (zipInputStream != null) try {
                zipInputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (fileOutputStream != null) try {
                fileOutputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (bufferedOutputStream != null) try {
                bufferedOutputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}