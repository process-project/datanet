package pl.cyfronet.datanet.deployer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

public class Unzip {

	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) > 0)
			out.write(buffer, 0, len);
		out.close();
	}

	public static final void extractOverridingAll(File zip, File outputFolder) throws ZipException,
			IOException {
		ZipFile zipFile = new ZipFile(zip);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		
		if (!outputFolder.exists())
			outputFolder.mkdir();
		
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			File file = new File(outputFolder, entry.getName());
			if (file.exists())
				FileUtils.forceDelete(file);
			if (entry.isDirectory()) {
				file.mkdir();
				continue;
			}
			copyInputStream(
					zipFile.getInputStream(entry),
					new BufferedOutputStream(new FileOutputStream(file)));
		}
		zipFile.close();
	}
	
	public static final void extractOverridingAll(InputStream inputStream, File outputFolder)
			throws ZipException, IOException {
		ZipInputStream zipStream = new ZipInputStream(inputStream);

		if (!outputFolder.exists())
			outputFolder.mkdir();

		ZipEntry entry;
		while ((entry = zipStream.getNextEntry()) != null) {
			File file = new File(outputFolder, entry.getName());
			if (file.exists())
				FileUtils.forceDelete(file);
			if (entry.isDirectory()) {
				file.mkdir();
				continue;
			}
			copyInputStream(zipStream,
					new BufferedOutputStream(new FileOutputStream(file)));
		}
		zipStream.close();
	}
}