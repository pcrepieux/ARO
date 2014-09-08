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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.att.aro.commonui.CheckBoxCellEditor;
import com.att.aro.commonui.CheckBoxRenderer;
import com.att.aro.commonui.ColorCellEditor;
import com.att.aro.commonui.ColorCellRenderer;
import com.att.aro.commonui.DataTableModel;
import com.att.aro.main.FilterIpAddressesTableModel.AppIPAddressSelection;
import com.att.aro.model.AnalysisFilter;
import com.att.aro.model.ApplicationSelection;
import com.att.aro.model.IPAddressSelection;
import com.att.aro.util.Util;

/**
 * Represents the table model for the Select Applications/IPs dialog 
 * (accessed from the View menu) that allows the user to filter the trace information 
 * based on application name or IP address.  
 */
public class FilterIpAddressesTableModel extends DataTableModel<AppIPAddressSelection> {
	private static final long serialVersionUID = 1L;

	public class AppIPAddressSelection {
		private IPAddressSelection ipSelection;
		private String appName;
		
		private AppIPAddressSelection(IPAddressSelection ipSelection, String appName) {
			this.ipSelection = ipSelection;
			this.appName = appName;
		}

		/**
		 * @return the ipSelection
		 */
		public IPAddressSelection getIpSelection() {
			return ipSelection;
		}

		/**
		 * @return the appName
		 */
		public String getAppName() {
			return appName;
		}
		
	}
	
	/**
	 * An integer that identifies the select column.
	 */
	public static final int SELECT_COL = 0;

	/**
	 * An integer that identifies the application name column.
	 */
	public static final int APP_COL = 1;
	
	/**
	 * An integer that identifies the domain name column.
	 */
	public static final int DOMAIN_COL = 2;

	/**
	 * An integer that identifies the color for an IP address.
	 */
	public static final int IP_COL = 3;

	/**
	 * An integer that identifies the color for an application.
	 */
	public static final int COLOR_COL = 4;

	private static final String[] columns = { Util.RB.getString("filter.select"),
			Util.RB.getString("filter.app"), Util.RB.getString("tcp.domain"), Util.RB.getString("filter.ip"),
			Util.RB.getString("filter.color") };

	/**
	 * Constructor that creates and initializes initializes the FilterIpAddressesTableModel 
	 * with the specified filter data from the analysis.
	 * 
	 * @param filter The analysis filter to be applied.
	 */
	public FilterIpAddressesTableModel(AnalysisFilter filter) {
		super(columns);

		List<AppIPAddressSelection> data = new ArrayList<AppIPAddressSelection>();
		
		for (ApplicationSelection app : filter.getApplicationSelections()) {
			for (IPAddressSelection ip : app.getIPAddressSelections()) {
				data.add(new AppIPAddressSelection(ip, app.getAppName()));
			}
		}
		setData(data);
	}

	/**
	 * This is the one method that must be implemented by subclasses. This method defines how 
	 * the data object managed by this table model is mapped to its columns when displayed 
	 * in a row of the table. The getValueAt() method uses this method to retrieve table cell data.
	 * 
	 * @param
	 * 		item A object containing the column information.
			columnIndex The index of the specified column.
	 *		
	 * @return An object containing the table column value. 
	 */
	@Override
	protected Object getColumnValue(AppIPAddressSelection item, int columnIndex) {
		switch (columnIndex) {
		case SELECT_COL:
			return item.ipSelection.isSelected();
		case APP_COL:
			return Util.getDefaultAppName(item.appName);
		case DOMAIN_COL:
			return item.ipSelection.getDomainName() != null ? item.ipSelection.getDomainName() : null;
		case IP_COL:
			return item.ipSelection.getIpAddress() != null ? item.ipSelection.getIpAddress().getHostAddress() : null;
		case COLOR_COL:
			return item.ipSelection.getColor();
		}
		return null;
	}
	
	/**
	 * Returns a class representing the specified column. This method is
	 * primarily used to sort numeric columns.
	 * 
	 * @param columnIndex
	 *            The index of the specified column.
	 * 
	 * @return A class representing the specified column.
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case IP_COL:
			return Double.class;
		default:
			return super.getColumnClass(columnIndex);
		}
	}
	/**
	 * Returns a value that indicates if the specified data cell is editable.
	 * 
	 * @param row The row number of the cell.
	 * 
	 * @param col The column number of the cell.
	 * 
	 * @return A boolean value that is "true" if the cell is editable, and "false" if not.
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		return col == SELECT_COL || col == COLOR_COL;
	}

	/**
	 * Returns a TableColumnModel that is based on the default table column model for the DataTableModel 
	 * class. The TableColumnModel returned by this method has the same number of columns in the same 
	 * order and structure as the table column model in the DataTableModel. When a DataTable object is 
	 * created, this method is used to create the TableColumnModel if one is not specified. This method 
	 * may be overridden in order to provide customizations to the default column model, such as providing 
	 * a default column width and/or adding column renderers and editors.
	 * 
	 * @return A TableColumnModel object.
	 * 
	 * @see com.att.aro.commonui.DataTableModel#createDefaultTableColumnModel()
	 */
	@Override
	public TableColumnModel createDefaultTableColumnModel() {
		TableColumnModel result = super.createDefaultTableColumnModel();
		TableColumn col;

		col = result.getColumn(SELECT_COL);
		col.setCellRenderer(new CheckBoxRenderer());
		col.setCellEditor(new CheckBoxCellEditor());
		col.setMaxWidth(45); // Reducing the width of checkBox column for more real estate
		col = result.getColumn(COLOR_COL);
		col.setCellRenderer(new ColorCellRenderer());
		col.setCellEditor(new ColorCellEditor());

		return result;
	}

	/**
	 * Sets the value of the specified data item.
	 * 
	 * @param aValue The value to set for the data item.
	 * 
	 * @param rowIndex The row index of the data item.
	 * 
	 * @param columnIndex The column index of the data item.
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 *      int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		super.setValueAt(aValue, rowIndex, columnIndex);

		AppIPAddressSelection obj = getValueAt(rowIndex);
		switch (columnIndex) {
		case SELECT_COL:
			if (aValue instanceof Boolean) {
				obj.ipSelection.setSelected(((Boolean) aValue).booleanValue());
			}
			break;
		case COLOR_COL:
			if (aValue instanceof Color) {
				obj.ipSelection.setColor(((Color) aValue));
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

}
