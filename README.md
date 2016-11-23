Pega-LogViewer
==============
Pega-LogViewer is a Java Swing based tool to view PegaRULES Log and Alert files.

Extract 'logviewer-2.0-bin.zip' to a folder.
Run 'logviewer.cmd' to launch the tool.

Features
----------
This tool show the log file line/entries in a table format. The columns are marked based on the log pattern.
The log pattern used are the ootb ones from prlogging.xml -> PEGA appender.
Some customers updates PEGA appender pattern.
In case the tool can't recognize the pattern automatically, it will present a dialog to enter the pattern,
so you might be required to get pattern from prlogging.xml or judge the pattern by looking at the logs lines itself.
 
For PegaRULES log the row entries are color coded based on log level, Red for Error, Green for Info, Light Blue for Debug etc.
For ALERT log, only Critical Alerts entries are coded Red.
 
The timeline graph is governed by the data in the table. in case you filter the data in the table using any of the column filter, the chart will change.

You can open very huge log files. In case of OOM, just change the heap size in the cmd file and try again. 
Features
	a. See memory, requestor counts in a chart. also system starts, exceptions and thread dump occurences are superimposed on the timeline.
	b. Search within the file output for useful content. results are highlighted in yellow
	d. Set Filters for certain log and alert data columns.
	e. Overview dialog to show (if any)- 
		1. System Start details
		2. Errors
		3. Thread Dumps

