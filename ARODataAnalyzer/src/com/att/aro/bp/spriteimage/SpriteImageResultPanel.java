/*
 * Copyright 2013 AT&T
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
package com.att.aro.bp.spriteimage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.att.aro.bp.BestPracticeButtonPanel;
import com.att.aro.bp.DataTableResultPanel;
import com.att.aro.commonui.DataTable;
import com.att.aro.main.ApplicationResourceOptimizer;
import com.att.aro.util.Util;

/**
 * Represents the panel that hasSprite Image test results.
 */
public class SpriteImageResultPanel extends JPanel implements DataTableResultPanel {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(SpriteImageResultPanel.class.getName());

	private static final int ROW_HEIGHT = 20;
	private static final int NO_OF_ROWS = 6;
	private static final int SCROLL_PANE_HEIGHT = SpriteImageResultPanel.NO_OF_ROWS
			* SpriteImageResultPanel.ROW_HEIGHT;
	private static final int SCROLL_PANE_LENGHT = 300;
	private int noOfRecords = 0;

	private JLabel title;
	private JPanel contentPanel;
	private JScrollPane scrollPane;
	private BestPracticeButtonPanel bpButtonPanel;
	private SpriteImageTableModel tableModel;
	private DataTable<SpriteImageEntry> contentTable;

	/**
	 * Initializes a new instance of the SpriteImageResultPanel class.
	 * 
	 */
	public SpriteImageResultPanel() {
		LOGGER.fine("init. SpriteImageResultPanel");
		initialize();
	}

	/**
	 * Sets the SpriteImage best practice result data.
	 * 
	 * @param data
	 *            the data to be displayed in the result content table
	 */
	public void setData(Collection<SpriteImageEntry> data) {
		this.tableModel.setData(data);
		noOfRecords = data.size();
		if (bpButtonPanel != null) {
			bpButtonPanel.setNoOfRecords(noOfRecords);
		}
	}

	/**
	 * Initializes the SpriteImageResultPanel.
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getContentPanel(), BorderLayout.CENTER);
		JPanel contentPanelWidth = new JPanel(new GridLayout(2, 1, 5, 5));
		JPanel contentPanelWidthAdjust = new JPanel(new GridBagLayout());
		contentPanelWidthAdjust.add(contentPanelWidth, new GridBagConstraints(
				0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
		contentPanelWidthAdjust.setBackground(Color.WHITE);
		this.add(contentPanelWidthAdjust, BorderLayout.EAST);
	}

	/**
	 * Returns the title label.
	 * 
	 * @return JLabel title 
	 */
	private JLabel getTitle() {
		if (this.title == null) {
			this.title = new JLabel(Util.RB.getString("spriteimages.table.header"), SwingConstants.CENTER);
		}
		return this.title;
	}

	/**
	 * Initializes and returns the content panel.
	 * 
	 * @return JPanel content panel
	 */
	private JPanel getContentPanel() {
		if (this.contentPanel == null) {
			this.contentPanel = new JPanel(new BorderLayout());
			this.contentPanel.add(getScrollPane(), BorderLayout.CENTER);
			this.contentPanel.add(getButtonsPanel(), BorderLayout.EAST);
		}
		return this.contentPanel;
	}

	/**
	 * Initializes and returns the JPanel that contains the button.
	 */
	private JPanel getButtonsPanel() {
		if (this.bpButtonPanel == null) {

			bpButtonPanel = new BestPracticeButtonPanel();
			bpButtonPanel.setScrollPane(getScrollPane());
			bpButtonPanel.setNoOfRecords(noOfRecords);

		}
		return this.bpButtonPanel;
	}

	/**
	 * Restores the table.
	 */
	public void restoreTable() {
		JButton viewBtn = ((BestPracticeButtonPanel) getButtonsPanel())
				.getViewBtn();
		if (this.noOfRecords > 5) {

			((BestPracticeButtonPanel) getButtonsPanel()).setExpanded(false);
			viewBtn.setEnabled(true);
			viewBtn.setText("+");
		} else {
			viewBtn.setEnabled(false);
		}
		this.scrollPane.setPreferredSize(new Dimension(
				SpriteImageResultPanel.SCROLL_PANE_LENGHT,
				SpriteImageResultPanel.SCROLL_PANE_HEIGHT));
	}

	/**
	 * Initializes and returns the Scroll Pane.
	 * 
	 * @return JScrollPane scroll pane
	 */
	private JScrollPane getScrollPane() {
		if (this.scrollPane == null) {
			this.scrollPane = new JScrollPane(getContentTable());
		}
		return this.scrollPane;
	}

	/**
	 * Initializes and returns the content table.
	 * 
	 * @return DataTable content table
	 */
	private DataTable<SpriteImageEntry> getContentTable() {
		if (this.contentTable == null) {
			this.tableModel = new SpriteImageTableModel();
			this.contentTable = new DataTable<SpriteImageEntry>(this.tableModel);
			this.contentTable.setAutoCreateRowSorter(true);
			this.contentTable.setGridColor(Color.LIGHT_GRAY);
			this.contentTable.setRowHeight(SpriteImageResultPanel.ROW_HEIGHT);
			this.contentTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			this.contentTable.addMouseListener(getSpriteImageTableMouseListener());
		}
		return this.contentTable;
	}

	/**
	 * Returns mouse listener.
	 * 
	 * @return mouse listener
	 */
	private MouseListener getSpriteImageTableMouseListener() {

		MouseListener ml = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				SpriteImageEntry entry = SpriteImageResultPanel.this.contentTable.getSelectedItem();
				if ((e.getClickCount() == 2) && (entry != null)) {
					ApplicationResourceOptimizer aro = ApplicationResourceOptimizer.getAroFrame();
					aro.displayDiagnosticTab();
					aro.getAroAdvancedTab().setHighlightedRequestResponse(entry.getHttpRequestResponse());
				}
			}
		};

		return ml;
	}

	/**
	 * Sets number of records in table.
	 */
	public void setNoOfRecords(int noOfRecords) {
		this.noOfRecords = noOfRecords;
		restoreTable();
	}

	/**
	 * Updates the table.
	 */
	public void updateTableForPrint() {
		// Check if already expanded, do nothing.
		BestPracticeButtonPanel bpanel=	((BestPracticeButtonPanel) getButtonsPanel());
		JButton viewBtn = bpanel.getViewBtn();
		if (!bpanel.isExpanded() && this.noOfRecords > 5) {
			
			viewBtn.doClick();
			this.scrollPane.revalidate();
		}
	}

	/**
	 * Clears data from the result table.
	 */
	public void clearTable() {
		LOGGER.log(Level.FINE, "clear sprite image data");
		this.tableModel.removeAllRows();
	}
	
}
