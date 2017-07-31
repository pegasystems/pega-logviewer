Pega-LogViewer
==============
Pega-LogViewer is a Java Swing based tool to view Pega RULES and Pega ALERT log files.

The tool can be downloaded from [Releases page](https://github.com/pegasystems/pega-logviewer/releases) under **Downloads** section.

Extract 'pega-logviewer-<*version*>-bin.zip' to a folder.

Run 'pega-logviewer-<*version*>.cmd' to launch the tool.

Features
----------
This tool show the log file line/entries in a table format. The columns are marked based on the log pattern.
The log pattern used are the ootb ones from prlogging.xml -> PEGA appender.
Some customers updates PEGA appender pattern.
In case the tool can't recognize the pattern automatically, it will present a dialog to enter the pattern,
so you might be required to get customised pattern from prlogging.xml.
 
For PegaRULES log the row entries are color coded based on log level, Red for Error, Green for Info, Light Blue for Debug etc.
For ALERT log, only Critical Alerts entries are coded Red.
 
The timeline graph is governed by the data in the table. in case you filter the data in the table using any of the column filter, the chart will change.

Can open big log files. In case of OOM, change the heap size in the cmd file and try again.

Features
  * See memory, requestor counts in a chart.
  * System starts, exceptions and thread dump occurences are superimposed on the timeline.
  * Search within the file output for useful content. results are highlighted in yellow
  * Set Filters for certain Log and Alert data columns.
  * Overview dialog to show (if any)
	+ PegaRULES log
		1. System Start details
		1. Errors
		1. Thread Dumps
		1. Bookmarks
		1. Search results
	
	+ Pega ALERT log
		1. Alert Overview with consolidated Alerts statistics
		1. Individual Alert tabs with details.
	
	
Build and Runtime
-----
needs JDK8.

To build the project use the following command:
```
$ mvn clean package
```

The release build is 'pega-logviewer-<*version*>-bin.zip' file under `'\target\'` folder.
