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
import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.TextAnchor;

import com.att.aro.commonui.AROUIManager;
import com.att.aro.model.TraceData;

/**
 * Represents the panel that displays the Connection Statistics chart in the
 * Overview tab.
 */
public class ProperSessionTerminationPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final ResourceBundle rb = ResourceBundleManager.getDefaultBundle();

	private static final int WIDTH = 400;
	private static final int HEIGHT = 170;
	private static final int SESSION_TERMINATION = 0;
	private static final int SESSION_TIGHT_CONN = 1;
	private static final int SESSION_BURST = 2;
	private static final int SESSION_LONG_BURST = 3;
	private static final double BAR_WIDTH_PERCENT = .2;

	// chart panel settings for constructor
	private static final boolean USER_BUFFER = false;
	private static final boolean PROPERTIES = false;
	private static final boolean COPY = false;
	private static final boolean SAVE = false;
	private static final boolean PRINT = false;
	private static final boolean ZOOM = false;
	private static final boolean TOOL_TIPS = true;

	private CategoryPlot plot;

	private double sessionTermPct;
	private double tightlyCoupledTCPPct;
	private double longBurstPct;
	private double nonPeriodicBurstPct;

	/**
	 * Initializes a new instance of the ProperSessionTerminationPanel class.
	 */
	public ProperSessionTerminationPanel() {
		super(new BorderLayout());
		initialize();
	}

	/**
	 * Sets the analysis data to be used for creating the Connection Statistics
	 * chart.
	 * 
	 * @param analysis
	 *            - The analysis data to be used for calculating the chart plot.
	 */
	public void setAnalysisData(TraceData.Analysis analysis) {
		plot.setDataset(createDataset(analysis));
	}

	/**
	 * Initializes the Panel for Proper Session Termination plot.
	 */
	private void initialize() {
		JFreeChart chart = ChartFactory.createBarChart(
				rb.getString("overview.sessionoverview.title"), null, null, createDataset(null),
				PlotOrientation.HORIZONTAL, false, false, false);
		chart.setBackgroundPaint(this.getBackground());
		chart.getTitle().setFont(AROUIManager.HEADER_FONT);
		

		this.plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.gray);
		plot.setRangeGridlinePaint(Color.gray);
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setMaximumCategoryLabelWidthRatio(1.0f);
		domainAxis.setMaximumCategoryLabelLines(2);
		domainAxis.setLabelFont(AROUIManager.LABEL_FONT);
		domainAxis.setTickLabelFont(AROUIManager.LABEL_FONT);
																							
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setLabel(rb.getString("analysisresults.percentage"));
		rangeAxis.setRange(0.0, 100.0);
		rangeAxis.setTickUnit(new NumberTickUnit(10));
		rangeAxis.setLabelFont(AROUIManager.LABEL_FONT);
		rangeAxis.setTickLabelFont(AROUIManager.LABEL_FONT);
				

		BarRenderer renderer = new StackedBarRenderer();
		renderer.setBasePaint(AROUIManager.CHART_BAR_COLOR);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setBaseItemLabelGenerator(new PercentLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelPaint(Color.black);

		// Make second bar in stack invisible
		renderer.setSeriesItemLabelsVisible(1, false);
		renderer.setSeriesPaint(1, new Color(0, 0, 0, 0));

		ItemLabelPosition insideItemlabelposition = new ItemLabelPosition(ItemLabelAnchor.INSIDE3,
				TextAnchor.CENTER_RIGHT);
		renderer.setBasePositiveItemLabelPosition(insideItemlabelposition);

		ItemLabelPosition outsideItemlabelposition = new ItemLabelPosition(
				ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT);
		renderer.setPositiveItemLabelPositionFallback(outsideItemlabelposition);
		
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setShadowVisible(false);
		renderer.setMaximumBarWidth(BAR_WIDTH_PERCENT);
		
		renderer.setBaseToolTipGenerator(new CategoryToolTipGenerator() {
			@Override
			public String generateToolTip(CategoryDataset arg0, int arg1, int arg2) {
				String sessionInfo = "";
				switch (arg2) {
				case SESSION_TERMINATION:
					sessionInfo = rb.getString("tooltip.sessionTermination");
					break;
				case SESSION_TIGHT_CONN:
					sessionInfo = rb.getString("tooltip.sessionTightConn");
					break;
				case SESSION_BURST:
					sessionInfo = rb.getString("tooltip.sessionBurst");
					break;
				case SESSION_LONG_BURST:
					sessionInfo = rb.getString("tooltip.sessionLongBurst");
					break;
				}

				return sessionInfo;
			}
		});

		plot.setRenderer(renderer);
		plot.getDomainAxis().setMaximumCategoryLabelLines(2);

		ChartPanel chartPanel = new ChartPanel(chart, WIDTH, HEIGHT,
				ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH + 100,
				100, ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH,
				ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT, USER_BUFFER, PROPERTIES, COPY, SAVE, PRINT,
				ZOOM, TOOL_TIPS);

		chartPanel.setDomainZoomable(false);
		chartPanel.setRangeZoomable(false);
		this.add(chartPanel, BorderLayout.CENTER);
	}

	/**
	 * Creates the plot data set for the Connection statistics chart from the
	 * current analysis.
	 * 
	 * @param analysis
	 *            The analysis data for the trace.
	 * @return CategoryDataset The plot data set.
	 */
	private CategoryDataset createDataset(TraceData.Analysis analysis) {
		this.sessionTermPct = analysis!= null ? analysis.calculateSessionTermPercentage(analysis) : 0;
		this.tightlyCoupledTCPPct = analysis!= null ? analysis.calculateTightlyCoupledConnection(analysis) : 0;
		this.longBurstPct = analysis!= null ? analysis.calculateLargeBurstConnection(analysis) : 0;
		this.nonPeriodicBurstPct = analysis!= null ? analysis.calculateNonPeriodicConnection(analysis) : 0;

		double[][] data = new double[2][4];
		data[0][0] = sessionTermPct;
		data[0][1] = tightlyCoupledTCPPct;
		data[0][2] = nonPeriodicBurstPct;
		data[0][3] = longBurstPct;
		data[1][0] = 100.0 - sessionTermPct;
		data[1][1] = 100.0 - tightlyCoupledTCPPct;
		data[1][2] = 100.0 - nonPeriodicBurstPct;
		data[1][3] = 100.0 - longBurstPct;
		return DatasetUtilities.createCategoryDataset(
				new Integer[] { 1, 2 },
				new String[] { rb.getString("overview.sessionoverview.sessionTerm"),
						rb.getString("overview.sessionoverview.tightlyGroupedBurstTerm"),
						rb.getString("overview.sessionoverview.nonPeriodicBurstTerm"),
						rb.getString("overview.sessionoverview.longBurstTerm") }, data);
	}

	/**
	 * Adds the connection statistics information in to provided writer.
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public void addTraceOverview(FileWriter writer) throws IOException {
		addKeyValue(writer, rb.getString("Export.sessionoverview.sessionTerm"), ""
				+ sessionTermPct+"%");
		addKeyValue(writer, rb.getString("Export.sessionoverview.tightlyGroupedBurstTerm"), ""
				+ tightlyCoupledTCPPct+"%");
		addKeyValue(writer, rb.getString("Export.sessionoverview.nonPeriodicBurstTerm"), ""
				+ nonPeriodicBurstPct+"%");
		addKeyValue(writer, rb.getString("Export.sessionoverview.longBurstTerm"), ""
				+ longBurstPct+"%");
	}

	/**
	 * method writes a provided key values in to the file writer.
	 * 
	 * @param writer
	 * @param key
	 * @param value
	 * @param value1
	 * @throws IOException
	 */
	private void addKeyValue(FileWriter writer, String key, String value) throws IOException {
		final String lineSep = System.getProperty(rb.getString("statics.csvLine.seperator"));
		writer.append(key);
		writer.append(',');
		writer.append(value);
		writer.append(lineSep);
	}
}
