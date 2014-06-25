/*
 * Copyright 2012 AT&T
 *
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
 */
package com.att.aro.bp;

import java.net.URI;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;

import com.att.aro.main.ApplicationResourceOptimizer;
import com.att.aro.model.TraceData;

/**
 * Defines the abstract class for a best practice.  Developers may create new ARO 
 * best practices by creating classes that extends this class.
 * 
 * Because of limited time, note that this was originally an interface, but temporarily changed to an abstract
 * class to make it work with the Text Compression US197546 that requires 3 status
 * (Pass, Fail, Warning). We'll have to refactor later.
 */
public abstract class BestPracticeDisplay {

	/**
	 * Returns the title of the best practice displayed in the tests conducted
	 * list on the Best Practices page.
	 * @return the title string
	 */
	public abstract String getOverviewTitle();

	/**
	 * Returns the title of the best practice that is displayed in the detail panel for the best practice group.
	 * @return The title string.
	 */
	public abstract String getDetailTitle();

	/**
	 * Indicates whether this best practice is a manual test.
	 * @return true if best practice is a self test
	 */
	public abstract boolean isSelfTest();

	/**
	 * Returns the detailed About description of the best practice that is displayed in the detail panel for the best practice group.
	 * @return The text string that is the About description.
	 */
	public abstract String getAboutText();

	/**
	 * Allows a best practice to indicate a link for more detailed information
	 * about the best practice.  If not null, this URI will be displayed as a
	 * "Learn More" link in the detail panel.
	 * @return The "Learn More" URI or null if one does not exist.
	 */
	public abstract URI getLearnMoreURI();
	
	/**
	 * Returns a value that indicates whether the specified best practices
	 * test has passed.  This method is not run for a self-test.
	 * 
	 * @param analysis The current analysis against which the best practice test is run.
	 * 
	 * @return A boolean value that is "true" if the best practices test has passed, 
	 * 		   and "false" otherwise.
	 */
	public abstract boolean isPass(TraceData.Analysis analysis);
	
	/**
	 * Added for US197546 Text Compression to temporarily handle the 3-status
	 * case of Pass, Warning, Fail. Will return false by default, and will be
	 * overriden in the BPTextFileCompression class
	 * @param analysis
	 * @return
	 */
	public boolean isWarning(TraceData.Analysis analysis){
		return false;
	}

	/**
	 * Returns a string containing the results of the best practices test based on the 
	 * specified analysis data.
	 * 
	 * @param analysisData The analysis data from the trace.
	 * 
	 * @return A string containing the test results.
	 */
	public abstract String resultText(TraceData.Analysis analysisData);

	/**
	 * This method is used to allow the best practice to do something within the 
	 * application to maybe display more detail about a failed test.  This action
	 * will be performed when the user clicks on a hyperlink in the results of a
	 * failed test.  This method is never called for a self-test.
	 * @param parent The ARO parent window that is displaying current analysis
	 */
	public abstract void performAction(HyperlinkEvent h, ApplicationResourceOptimizer parent);

	/**
	 * Allows the developer to include extra information when a best practice
	 * is exported to a file.  The analysis export will always export the best
	 * practice title and pass/fail/manual.  This method allows for extra 
	 * information to be exported if desired.
	 * @param analysisData The analysis data from the trace
	 * @return A list of items to be included in the export or null if no extra
	 * information is necessary.
	 */
	public abstract List<BestPracticeExport> getExportData(TraceData.Analysis analysisData);
	
	/**
	 * Returns results of the test.
	 * This can be for example a table of results inside of JPanel.
	 * 
	 * @return Results
	 */
	public abstract JPanel getTestResults();

}
