/*
 *  Copyright 2012 AT&T
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
package com.att.aro.plugin;

import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

import com.att.aro.main.ApplicationResourceOptimizer;

/**
 * Interface to be implemented by configured Analyzer plugin tabs
 * 
 * @author ns5254
 *
 */
public interface AnalyzerPlugin {
		
	JScrollPane getPanel(final ApplicationResourceOptimizer parent);
	
	JMenuItem getMenuItem(final ApplicationResourceOptimizer parent);
}
