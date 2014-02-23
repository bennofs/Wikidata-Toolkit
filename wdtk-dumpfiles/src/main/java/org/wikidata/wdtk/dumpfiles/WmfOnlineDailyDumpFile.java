package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class for representing incremental daily dump files as published by the
 * Wikimedia Foundation. The dump file and additional information about its
 * status is online and web access is needed to fetch this data on demand.
 * 
 * @author Markus Kroetzsch
 * 
 */
class WmfOnlineDailyDumpFile extends WmfDumpFile {

	final WebResourceFetcher webResourceFetcher;
	final DirectoryManager dumpfileDirectoryManager;

	/**
	 * Constructor.
	 * 
	 * @param dateStamp
	 *            dump date in format YYYYMMDD
	 * @param projectName
	 *            project name string
	 * @param webResourceFetcher
	 *            object to use for accessing the web
	 * @param dumpfileDirectoryManager
	 *            the directory manager for the directory where dumps should be
	 *            downloaded to
	 */
	public WmfOnlineDailyDumpFile(String dateStamp, String projectName,
			WebResourceFetcher webResourceFetcher,
			DirectoryManager dumpfileDirectoryManager) {
		super(dateStamp, projectName, WmfDumpFile.POSTFIX_DAILY_DUMP_FILE);
		this.webResourceFetcher = webResourceFetcher;
		this.dumpfileDirectoryManager = dumpfileDirectoryManager;
	}

	@Override
	public DumpContentType getDumpContentType() {
		return MediaWikiDumpFile.DumpContentType.DAILY;
	}

	@Override
	public BufferedReader getDumpFileReader() throws IOException {
		String fileName = this.projectName + "-" + this.dateStamp
				+ WmfDumpFile.POSTFIX_DAILY_DUMP_FILE;
		String urlString = getBaseUrl() + fileName;

		if (this.getMaximalRevisionId() == -1) {
			throw new IOException(
					"Failed to retrieve maximal revision id. Aborting dump retrieval.");
		}

		DirectoryManager dailyDirectoryManager = this.dumpfileDirectoryManager
				.getSubdirectoryManager("daily-" + this.dateStamp);

		try (InputStream inputStream = webResourceFetcher
				.getInputStreamForUrl(urlString)) {
			dailyDirectoryManager.createFile(fileName, inputStream);
		}

		dailyDirectoryManager.createFile("maxrevid.txt", this
				.getMaximalRevisionId().toString());

		return dailyDirectoryManager.getBufferedReaderForBz2File(fileName);
	}

	@Override
	protected Long fetchMaximalRevisionId() {
		String inputLine;
		try (BufferedReader in = this.webResourceFetcher
				.getBufferedReaderForUrl(getBaseUrl() + "maxrevid.txt")) {
			inputLine = in.readLine();
		} catch (IOException e) {
			return -1L;
		}

		if (inputLine != null) {
			try {
				return new Long(inputLine);
			} catch (NumberFormatException e) {
				// fall through
			}
		}
		return -1L;
	}

	@Override
	protected boolean fetchIsDone() {
		boolean result;
		try (BufferedReader in = this.webResourceFetcher
				.getBufferedReaderForUrl(getBaseUrl() + "status.txt")) {
			String inputLine = in.readLine();
			result = inputLine.equals("done");
		} catch (IOException e) { // file not found or not readable
			result = false;
		}
		return result;
	}

	/**
	 * Get the base URL under which the files for this dump are found.
	 * 
	 * @return base URL
	 */
	String getBaseUrl() {
		return WmfDumpFile.DUMP_SITE_BASE_URL + "other/incr/"
				+ this.projectName + "/" + this.dateStamp + "/";
	}

}