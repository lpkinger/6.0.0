package com.uas.erp.core.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;

import org.apache.commons.io.IOUtils;

public class CsvWriter {

	private final File csvFile;
	private final BufferedWriter writer;
	private static final String outputDir = "/tmp/";

	private boolean isNewLine = true;

	private static File createTempFile() throws IOException {
		File file = new File(outputDir);
		if (!file.exists()) {
			file.mkdir();
		}
		String tempFileName = String.valueOf(new Random().nextDouble());
		return File.createTempFile(tempFileName, ".csv", file);
	}

	public CsvWriter() throws IOException {
		this(createTempFile());
	}

	public CsvWriter(File csvFile) throws IOException {
		this.csvFile = csvFile;
		this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"), 1024);
	}

	public void close() throws IOException {
		IOUtils.closeQuietly(writer);
		if (csvFile != null && !csvFile.delete())
			throw new IOException("Could not delete temporary file after processing: " + csvFile);
	}

	public void write(OutputStream stream) throws IOException {
		this.writer.flush();
		FileInputStream in = new FileInputStream(csvFile);
		try {
			IOUtils.copy(in, stream);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public void newCell(Object value) throws IOException {
		if (!isNewLine)
			this.writer.write(",");
		this.writer.write(value == null ? "" : String.valueOf(value));
		this.isNewLine = false;
	}

	public void newHeaderCell(Object text) throws IOException {
		newCell("\"" + (text == null ? "" : text) + "\"");
	}

	public void newLine() throws IOException {
		this.writer.newLine();
		this.isNewLine = true;
	}

}
