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


package com.att.aro.main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.att.aro.commonui.AROUIManager;
import com.att.aro.model.TraceData;

/**
 * Displays the date, trace name, network type, and device profile in the header of the Overview and
 * Diagnostics tabs.
 */
public class DeviceNetworkProfilePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final ResourceBundle rb = ResourceBundleManager
			.getDefaultBundle();

	private JPanel dataPanel;
	private JLabel dateValueLabel;
	private JLabel traceValueLabel;
	private JLabel networkTypeValueLabel;
	private JLabel profileValueLabel;
	private static final Font LABEL_FONT = new Font("TEXT_FONT", Font.BOLD, 12);
	private static final Font TEXT_FONT = new Font("TEXT_FONT", Font.PLAIN, 12);
	private JLabel networkTypeLabel;

	/**
	 * Initializes a new instance of the DeviceNetworkProfilePanel class.
	 */
	public DeviceNetworkProfilePanel() {
		this.setLayout(new BorderLayout(10, 10));
		this.setBackground(UIManager.getColor(AROUIManager.PAGE_BACKGROUND_KEY));

		dateValueLabel = new JLabel();
		dateValueLabel.setFont(TEXT_FONT);
		traceValueLabel = new JLabel();
		traceValueLabel.setFont(TEXT_FONT);

		networkTypeValueLabel = new JLabel();
		networkTypeValueLabel.setFont(TEXT_FONT);
		profileValueLabel = new JLabel();
		profileValueLabel.setFont(TEXT_FONT);
		add(getDataPanel(), BorderLayout.CENTER);
	}

	/**
	 * Refreshes the various labels in the DeviceNetworkProfilePanel using the specified trace data.
	 * 
	 * @param analysisData
	 *            The Analysis object containing the trace data.
	 */
	public void refresh(TraceData.Analysis analysisData) {
		if (analysisData != null) {
			TraceData traceData = analysisData.getTraceData();
			DateFormat format = DateFormat.getDateTimeInstance();
			dateValueLabel.setText(format.format(traceData.getTraceDateTime()));
			traceValueLabel.setText(traceData.getTraceDir().getName());
			if(!traceData.getTraceDir().isFile()){
				if (analysisData.getNetworTypeInfos().size() > 1) {

					networkTypeLabel.setText(rb.getString("bestPractices.networktypes"));
					networkTypeValueLabel.setText(traceData.getNetworkTypesList());

				} else {
					networkTypeLabel.setText(rb.getString("bestPractices.networktype"));
					networkTypeValueLabel.setText(ResourceBundleManager.getEnumString((traceData
							.getNetworkType())));

				}
			}
			profileValueLabel.setText(analysisData.getProfile().getName());
		} else {
			dateValueLabel.setText(null);
			traceValueLabel.setText(null);
			networkTypeValueLabel.setText(null);
			profileValueLabel.setText(null);
		}
	}

	/**
	 * Returns the date value label.
	 * 
	 * @return A JLabel object containing the date value label.
	 */
	public JLabel getDateValueLabel() {
		return dateValueLabel;
	}

	/**
	 * Returns the trace name label.
	 * 
	 * @return A JLabel object containing the trace name label.
	 */
	public JLabel getTraceValueLabel() {
		return traceValueLabel;
	}

	/**
	 * Returns the network type value label.
	 * 
	 * @return A JLabel object containing the network type value label.
	 */
	public JLabel getNetworkTypeValueLabel() {
		return networkTypeValueLabel;
	}

	/**
	 * Returns the device profile value label.
	 * 
	 * @return A JLabel object containing tThe device value label.
	 */
	public JLabel getProfileValueLabel() {
		return profileValueLabel;
	}

	/**
	 * Creates the JPanel containing the Date , Trace, network profile and
	 * profile name.
	 * 
	 * @return the dataPanel
	 */
	private JPanel getDataPanel() {
		if (dataPanel == null) {
			dataPanel = new JPanel(new GridBagLayout());

			Insets insets = new Insets(2, 2, 2, 2);
			JLabel label;
			label = new JLabel(rb.getString("bestPractices.date"));
			label.setFont(LABEL_FONT);
			dataPanel.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE, insets,
					0, 0));
			dataPanel.add(dateValueLabel, new GridBagConstraints(1, 0, 1, 1,
					0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, insets, 0, 0));

			networkTypeLabel = new JLabel(rb.getString("overview.info.networktype"),
					JLabel.RIGHT);
			networkTypeLabel.setFont(LABEL_FONT);
			dataPanel.add(networkTypeLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.NONE, insets,
					0, 0));
			dataPanel.add(networkTypeValueLabel, new GridBagConstraints(3, 0,
					1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, insets, 0, 0));

			label = new JLabel(rb.getString("bestPractices.trace"));
			label.setFont(LABEL_FONT);
			dataPanel.add(label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE, insets,
					0, 0));
			dataPanel.add(traceValueLabel, new GridBagConstraints(1, 1, 1, 1,
					0.5, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));

			label = new JLabel(rb.getString("overview.info.profile"),
					JLabel.RIGHT);
			label.setFont(LABEL_FONT);
			dataPanel.add(label, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.NONE, insets,
					0, 0));
			dataPanel.add(profileValueLabel, new GridBagConstraints(3, 1, 1, 1,
					0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
					insets, 0, 0));
		}
		return dataPanel;
	}
}
