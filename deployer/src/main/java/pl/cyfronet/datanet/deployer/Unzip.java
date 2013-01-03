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

public class Unzip {

	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
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
				deleteRecursively(file);
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
	
	public static void deleteRecursively(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					deleteRecursively(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

}
