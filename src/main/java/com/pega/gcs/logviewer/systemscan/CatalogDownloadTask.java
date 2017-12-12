/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogViewer;

public class CatalogDownloadTask extends SwingWorker<Void, Long> {

	private static final Log4j2Helper LOG = new Log4j2Helper(CatalogDownloadTask.class);

	private static final String CATALOG_FTP_USER = "catalog";

	private static final String CATALOG_FTP_PASSWORD = "catalog!";

	private static final String CATALOG_FTP_SERVER = "pegaftp2.pega.com";

	private static final String CATALOG_FTP_LOCATION = "/hfix/CATALOG/61/CATALOG.ZIP";

	private ModalProgressMonitor progressMonitor;

	public CatalogDownloadTask(ModalProgressMonitor progressMonitor) {
		super();
		this.progressMonitor = progressMonitor;
	}

	@Override
	protected Void doInBackground() throws Exception {

		FTPClient ftp = new FTPClient();

		CopyStreamListener copyStreamListener = new CopyStreamListener() {

			@Override
			public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {

				long mbTransferred = totalBytesTransferred / 1000000;

				publish(mbTransferred);

			}

			@Override
			public void bytesTransferred(CopyStreamEvent event) {
				bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
			}
		};

		ftp.setCopyStreamListener(copyStreamListener);

		try {

			ftp.connect(CATALOG_FTP_SERVER);

			LOG.info("Connected to " + CATALOG_FTP_SERVER + " on " + ftp.getDefaultPort());

			int reply = ftp.getReplyCode();

			if (FTPReply.isPositiveCompletion(reply)) {

				if (ftp.login(CATALOG_FTP_USER, CATALOG_FTP_PASSWORD)) {

					ftp.setFileType(FTP.BINARY_FILE_TYPE);

					ftp.enterLocalPassiveMode();

					String pwd = System.getProperty("user.dir");

					File catalogFile = new File(pwd, LogViewer.SYSTEM_SCAN_CATALOG_ZIP_FILE);

					OutputStream output = new FileOutputStream(catalogFile);

					ftp.retrieveFile(CATALOG_FTP_LOCATION, output);

					output.close();
				}

				ftp.noop(); // check that control connection is working OK
				ftp.logout();
			}
		} catch (Exception e) {
			LOG.error("Error downloading Catalog file", e);
		} finally {

			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException f) {
					// do nothing
				}
			}
		}

		return null;
	}

	@Override
	protected void process(List<Long> chunks) {

		if ((isDone()) || (isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
			return;
		}

		Collections.sort(chunks);

		Long mbTransferred = chunks.get(chunks.size() - 1);

		String message = String.format("Downloading Catalog file (%d MB)", mbTransferred);

		progressMonitor.setNote(message);
	}

}
