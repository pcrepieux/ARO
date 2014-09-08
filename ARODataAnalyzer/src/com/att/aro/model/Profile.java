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
package com.att.aro.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Represents a device profile that is used as a model of the device when analyzing trace data.
 */
public abstract class Profile implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The Carrier/Network provider name.
	 */
	public static final String CARRIER = "CARRIER";

	/**
	 * The device name. 
	 */
	public static final String DEVICE = "DEVICE";

	/**
	 * The network type (i.e. 3G or LTE) of the Profile.
	 */
	public static final String PROFILE_TYPE = "PROFILE_TYPE";

	/**
	 * The threshold for the user input window (in seconds).
	 */
	public static final String USER_INPUT_TH = "USER_INPUT_TH";

	/**
	 * The average amount of power (in watts) that should be used when GPS is in the Active state. 
	 */
	public static final String POWER_GPS_ACTIVE = "POWER_GPS_ACTIVE";

	/**
	 * The average amount of power (in watts) that should be used when GPS is in the 
	 * Standby state. 
	 */
	public static final String POWER_GPS_STANDBY = "POWER_GPS_STANDBY";

	/**
	 * The average amount of power (in watts) that should be used when the camera is on. 
	 */
	public static final String POWER_CAMERA_ON = "POWER_CAMERA_ON";

	/**
	 * The average amount of power (in watts) that should be used when Bluetooth is in the Active state. 
	 */
	public static final String POWER_BLUETOOTH_ACTIVE = "POWER_BLUETOOTH_ACTIVE";

	/**
	 * The average amount of power that should be used (in watts) when 
	 * Bluetooth is in the Standby state.
	 */
	public static final String POWER_BLUETOOTH_STANDBY = "POWER_BLUETOOTH_STANDBY";

	/**
	 * The average amount of power (in watts) that should be used when the screen is on. 
	 */
	public static final String POWER_SCREEN_ON = "POWER_SCREEN_ON";

	/**
	 * The threshold for defining a burst (in seconds). 
	 */
	public static final String BURST_TH = "BURST_TH";

	/**
	 * The threshold for defining a long burst (in seconds).
	 */
	public static final String LONG_BURST_TH = "LONG_BURST_TH";

	/**
	 * The minimum tolerable variation for periodical bursts (in seconds). 
	 */
	public static final String PERIOD_MIN_CYCLE = "PERIOD_MIN_CYCLE";

	/**
	 * The maximum tolerable variation for periodical bursts (in seconds). 
	 */
	public static final String PERIOD_CYCLE_TOL = "PERIOD_CYCLE_TOL";

	/**
	 * The minimum amount of observed samples for periodical bursts. 
	 */
	public static final String PERIOD_MIN_SAMPLES = "PERIOD_MIN_SAMPLES";

	/**
	 * The threshold for duration of a large burst (in seconds).
	 */
	public static final String LARGE_BURST_DURATION = "LARGE_BURST_DURATION";

	/**
	 * The threshold for the size of a large burst (in bytes).
	 */
	public static final String LARGE_BURST_SIZE = "LARGE_BURST_SIZE";

	/**
	 * The threshold for close spaced bursts (sec).
	 */
	public static final String CLOSE_SPACED_BURSTS = "CLOSE_SPACED_BURSTS";

	/**
	 * The time delta for throughput calculations.
	 */
	public static final String W_THROUGHPUT = "W_THROUGHPUT";

	/**
	 * Factory method used to create a profile of the specified type.
	 * @param profileType - The profile type. One of the values of the ProfileType enumeration. 
	 * @param file - The file where the profile will be stored.
	 * @param props - The property values for the profile.
	 * @return The Profile object.
	 * @throws ProfileException
	 */
	public static Profile create(ProfileType profileType, File file,
			Properties props) throws ProfileException {
		Profile profile = null;
		switch (profileType) {
		case T3G:
			return new Profile3G(file, props);
		case LTE:
			return new ProfileLTE(file, props);
		case WIFI:
			return new ProfileWiFi(file, props);
		}
		return profile;
		
	}
	
	/**
	 * Factory method used to create a profile of the specified type with the specified name.
	 * @param profileType - The profile type. One of the values of the ProfileType enumeration.
	 * @param name - The name of the profile. 
	 * @param props - The property values for the profile.
	 * @return The Profile object.
	 * @throws ProfileException
	 */
	public static Profile create(ProfileType profileType, String name,
			Properties props) throws ProfileException {
		Profile profile = null;
		switch (profileType) {
		case T3G:
			return new Profile3G(name, props);
		case LTE:
			return new ProfileLTE(name, props);
		case WIFI:
			return new ProfileWiFi(name, props);
		}
		return profile;
		
	}
	
	/**
	 * A factory method that creates a new profile of the proper type from the
	 * specified properties file
	 * 
	 * @param file
	 *            The properties file.
	 * @return The resulting profile object
	 * @throws IOException
	 *             when an error occurs accessing the file
	 * @throws ProfileException
	 *             when an error occurs reading the profile data
	 */
	public static Profile createFromFile(File file) throws IOException,
			ProfileException {
		FileReader reader = new FileReader(file);
		try {
			Properties props = new Properties();
			props.load(reader);

			String stype = props.getProperty(PROFILE_TYPE);
			ProfileType type = stype != null ? ProfileType.valueOf(stype)
					: ProfileType.T3G;
			switch (type) {
			case T3G:
				return new Profile3G(file, props);
			case LTE:
				return new ProfileLTE(file, props);
			case WIFI:
				return new ProfileWiFi(file, props);
			default:
				throw new IllegalArgumentException("Invalid profile type: "
						+ type);
			}
		} finally {
			reader.close();
		}
	}

	/**
	 * A factory method that creates a new profile of the proper type from the
	 * properties read from the specified InputStream.
	 * 
	 * @param name
	 * 			  The name of the profile to create.
	 * @param input
	 *            The input stream to be read.
	 * @return The newly created profile.
	 * @throws IOException
	 * @throws ProfileException
	 */
	public static Profile createFromInputStream(String name, InputStream input)
			throws IOException, ProfileException {
		Properties props = new Properties();
		props.load(input);
		String stype = props.getProperty(PROFILE_TYPE);
		ProfileType type = stype != null ? ProfileType.valueOf(stype)
				: ProfileType.T3G;
		switch (type) {
		case T3G:
			return new Profile3G(name, props);
		case LTE:
			return new ProfileLTE(name, props);
		case WIFI:
			return new ProfileWiFi(name, props);
		default:
			throw new IllegalArgumentException("Invalid profile type: " + type);
		}
	}

	private Map<String, String> errorLog = new HashMap<String, String>();
	private boolean init;

	private File file;
	private String name;
	private String carrier = "AT&T";
	private String device = "Captivate - ad study";

	private double userInputTh = 1.0;
	private double powerGpsActive = 1.0;
	private double powerGpsStandby = 0.5;
	private double powerCameraOn = 0.3;
	private double powerBluetoothActive = 1.0;
	private double powerBluetoothStandby = 0.5;
	private double powerScreenOn = 0.3;
	private double burstTh = 1.5;
	private double longBurstTh = 5.0;
	private double periodMinCycle = 10.0;
	private double periodCycleTol = 1.0;
	private int periodMinSamples = 3;
	private double largeBurstDuration = 5.0;
	private int largeBurstSize = 100000;
	private double closeSpacedBurstThreshold = 10.0;
	private double throughputWindow = 0.5;

	/**
	 * Initializes an instance of the Profile class.
	 */
	public Profile() {
		super();

		try {
			init(new Properties());
		} catch (ProfileException e) {
			// Ignore
		}
	}

	/**
	 * Initializes an instance of the Profile class, using the specified properties.
	 * 
	 * @param file A file where the profile can be saved. This argument can be null. 
	 * 
	 * @param properties A Properties object containing the profile properties.
	 * 
	 * @throws ProfileException
	 */
	public Profile(File file, Properties properties) throws ProfileException {
		this.file = file;
		if (file != null) {
			name = file.getAbsolutePath();
		}
		init(properties);
	}

	/**
	 * Initializes an instance of the Profile class, using the specified properties. 
	 * 
	 * @param name - The name of the profile. The name is either an absolute path to the file that 
	 * holds this profile, or the name of a pre-defined profile .
	 * 
	 * @param properties A Properties object containing the profile properties.
	 * 
	 * @throws ProfileException
	 */
	public Profile(String name, Properties properties) throws ProfileException {
		this.name = name;
		init(properties);
	}

	/**
	 * A utility method for calculating RRC energy.
	 * 
	 * @param time1 A beginning time value.
	 * 
	 * @param time2 An ending time value.
	 * 
	 * @param state An RRCState enumeration value that indicates the RRC energy state.
	 * 
	 * @param packets A List of packets that were passed during the specified time period. 
	 * The packet information may be used in determining the amount of energy used. 
	 * 
	 * @return The energy consumed in the specified RRC state.
	 */
	public abstract double energy(double time1, double time2, RRCState state,
			List<PacketInfo> packets);

	/**
	 * Returns the type of profile. Subclasses of this class must identify the profile type.
	 * 
	 * @return The profile type. One of the values of the ProfileType enumeration.
	 */
	public abstract ProfileType getProfileType();

	/**
	 * Stores the current profile values contained in this object, in the specified file. 
	 * 
	 * @param file The absolute path to the location where the profile values should be stored. 
	 * 
	 * @throws IOException
	 */
	public final synchronized void saveToFile(File file) throws IOException {
		Properties props = new Properties();

		// Get sub-class data
		saveProperties(props);

		props.setProperty(CARRIER, carrier);
		props.setProperty(DEVICE, device);
		props.setProperty(PROFILE_TYPE, getProfileType().name());
		props.setProperty(USER_INPUT_TH, String.valueOf(userInputTh));
		props.setProperty(POWER_GPS_ACTIVE, String.valueOf(powerGpsActive));
		props.setProperty(POWER_GPS_STANDBY, String.valueOf(powerGpsStandby));
		props.setProperty(POWER_CAMERA_ON, String.valueOf(powerCameraOn));
		props.setProperty(POWER_BLUETOOTH_ACTIVE, String.valueOf(powerBluetoothActive));
		props.setProperty(POWER_BLUETOOTH_STANDBY, String.valueOf(powerBluetoothStandby));
		props.setProperty(POWER_SCREEN_ON, String.valueOf(powerScreenOn));
		props.setProperty(BURST_TH, String.valueOf(burstTh));
		props.setProperty(LONG_BURST_TH, String.valueOf(longBurstTh));
		props.setProperty(PERIOD_MIN_CYCLE, String.valueOf(periodMinCycle));
		props.setProperty(PERIOD_CYCLE_TOL, String.valueOf(periodCycleTol));
		props.setProperty(PERIOD_MIN_SAMPLES, String.valueOf(periodMinSamples));
		props.setProperty(LARGE_BURST_DURATION, String.valueOf(largeBurstDuration));
		props.setProperty(LARGE_BURST_SIZE, String.valueOf(largeBurstSize));
		props.setProperty(CLOSE_SPACED_BURSTS, String.valueOf(closeSpacedBurstThreshold));
		props.setProperty(W_THROUGHPUT, String.valueOf(throughputWindow));
		props.store(new FileOutputStream(file), "Set what this comment is");
		this.file = file;
		this.name = file.getAbsolutePath();
	}

	/**
	 * Returns the name of this profile. The name is either an absolute path to the file that holds 
	 * this profile, or the name of a pre-defined profile.
	 * 
	 * @return The profile name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the profile file. 
	 * 
	 * @return The file.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the carrier. 
	 * 
	 * @return The carrier name.
	 */
	public String getCarrier() {
		return carrier;
	}

	/**
	 * Returns the name of the device. 
	 * 
	 * @return A string containing the device name.
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * Returns the user input threshold. 
	 * 
	 * @return The user input threshold value.
	 */
	public double getUserInputTh() {
		return userInputTh;
	}

	/**
	 * Returns the amount of energy used when GPS is active. 
	 * 
	 * @return The GPS active power value.
	 */
	public double getPowerGpsActive() {
		return powerGpsActive;
	}

	/**
	 * Returns the amount of energy used when GPS is in standby mode. 
	 * 
	 * @return The GPS standby mode power value.
	 */
	public double getPowerGpsStandby() {
		return powerGpsStandby;
	}

	/**
	 * Returns the amount of energy used when the camera is ON. 
	 * 
	 * @return The amount of power used when the camera is ON.
	 */
	public double getPowerCameraOn() {
		return powerCameraOn;
	}

	/**
	 * Returns the amount of energy used when Bluetooth is active. 
	 * 
	 * @return The Bluetooth active power value.
	 */
	public double getPowerBluetoothActive() {
		return powerBluetoothActive;
	}

	/**
	 * Returns the amount of energy used when Bluetooth is in standby mode. 
	 * 
	 * @return The Bluetooth standby mode power value.
	 */
	public double getPowerBluetoothStandby() {
		return powerBluetoothStandby;
	}

	/**
	 * Returns the total amount of energy used when the device screen is ON. 
	 * 
	 * @return The screen ON power value.
	 */
	public double getPowerScreenOn() {
		return powerScreenOn;
	}

	/**
	 * Returns the value of the burst threshold. 
	 * 
	 * @return The burst threshold.
	 */
	public double getBurstTh() {
		return burstTh;
	}

	/**
	 * Returns the value of the long burst threshold. 
	 * 
	 * @return The long burst threshold value.
	 */
	public double getLongBurstTh() {
		return longBurstTh;
	}

	/**
	 * Returns the minimum tolerable variation for periodical bursts (in seconds).
	 * 
	 * @return The minimum tolerable variation
	 */
	public double getPeriodMinCycle() {
		return periodMinCycle;
	}

	/**
	 * Returns the maximum tolerable variation for periodical bursts (in seconds).
	 * 
	 * @return The maximum tolerable variation.
	 */
	public double getPeriodCycleTol() {
		return periodCycleTol;
	}

	/**
	 * Returns the the minimum amount of observed samples for periodical transfers 
	 * 
	 * @return The minimum sample period value.
	 */
	public int getPeriodMinSamples() {
		return periodMinSamples;
	}

	/**
	 * Returns the total duration of all large bursts. 
	 * 
	 * @return The large burst duration value.
	 */
	public double getLargeBurstDuration() {
		return largeBurstDuration;
	}

	/**
	 * Returns the total size of all large bursts. 
	 * 
	 * @return The large burst size value (in bytes).
	 */
	public int getLargeBurstSize() {
		return largeBurstSize;
	}
	/**
	 * Returns the threshold for close spaced bursts (sec).
	 * @return Threshold for close spaced bursts (sec)
	 */
	public double getCloseSpacedBurstThreshold() {
		return closeSpacedBurstThreshold;
	}

	/**
	 * Returns the throughput window.
	 * @return The throughput window value.
	 */
	public double getThroughputWindow() {
		return throughputWindow;
	}

	/**
	 * Sets the throughput window to the specified value.
	 * @param throughputWindow
	 *            - The throughput window value to set.
	 */
	public void setThroughputWindow(double throughputWindow) {
		this.throughputWindow = throughputWindow;
	}

	/**
	 * Reads the specified profile properties and returns a double value for the specified attribute.
	 * @param properties
	 *            The profile properties to be read.
	 * @param attribute
	 *            The attribute name whose value is to be read.
	 * @param defaultVal
	 *            The default value for the attribute.
	 * @return The double alue of the specified attribute for the profile.
	 */
	protected double readDouble(Properties properties, String attribute,
			double defaultVal) {
		String value = properties.getProperty(attribute);
		try {
			if (value != null) {
				init = true;
				return Double.parseDouble(value);
			} else {
				return defaultVal;
			}
		} catch (NumberFormatException e) {
			errorLog.put(attribute, value);
			return defaultVal;
		}
	}

	/**
	 * Reads the specified profile properties and returns an int value for the specified attribute.
	 * @param properties
	 *            The profile properties to be read.
	 * @param attribute
	 *            The attribute name whose value is to be read.
	 * @param defaultVal
	 *            The default value for the attribute.
	 * @return The int value of the specified attribute for the profile.
	 */
	protected int readInt(Properties properties, String attribute,
			int defaultVal) {
		String value = properties.getProperty(attribute);
		try {
			if (value != null) {
				init = true;
				return Integer.parseInt(value);
			} else {
				return defaultVal;
			}
		} catch (NumberFormatException e) {
			errorLog.put(attribute, value);
			return defaultVal;
		}
	}

	/**
	 * Initializes Profile class members using the specified Properties object. 
	 * This method is overridden by Sub-classes to initialize properties in a Profile object. 
	 * 
	 * @param properties
	 *            The properties object containing the property values to set.
	 */
	protected abstract void setProperties(Properties properties);

	/**
	 * Saves the current profile values in the specified  Properties object. This method 
	 * is used by sub-classes to save member values to a properties object for persistence.
	 * 
	 * @param properties - The properties object in which to store the values.
	 */
	protected abstract void saveProperties(Properties properties);

	/**
	 * Initialize the Profile values from the provided Properties object.
	 * 
	 * @param properties
	 *            Object that contains profile values.
	 * @throws ProfileException
	 */
	private synchronized void init(Properties properties)
			throws ProfileException {

		carrier = properties.getProperty(CARRIER);
		device = properties.getProperty(DEVICE);
		userInputTh = readDouble(properties, USER_INPUT_TH, userInputTh);
		powerGpsActive = readDouble(properties, POWER_GPS_ACTIVE, powerGpsActive);
		powerGpsStandby = readDouble(properties, POWER_GPS_STANDBY,	powerGpsStandby);
		powerCameraOn = readDouble(properties, POWER_CAMERA_ON, powerCameraOn);
		powerBluetoothActive = readDouble(properties, POWER_BLUETOOTH_ACTIVE, powerGpsActive);
		powerBluetoothStandby = readDouble(properties, POWER_BLUETOOTH_STANDBY, powerGpsStandby);
		powerScreenOn = readDouble(properties, POWER_SCREEN_ON, powerCameraOn);
		burstTh = readDouble(properties, BURST_TH, burstTh);
		longBurstTh = readDouble(properties, LONG_BURST_TH, longBurstTh);
		periodMinCycle = readDouble(properties, PERIOD_MIN_CYCLE, periodMinCycle);
		periodCycleTol = readDouble(properties, PERIOD_CYCLE_TOL, periodCycleTol);
		periodMinSamples = readInt(properties, PERIOD_MIN_SAMPLES, periodMinSamples);
		largeBurstDuration = readDouble(properties, LARGE_BURST_DURATION, largeBurstDuration);
		largeBurstSize = readInt(properties, LARGE_BURST_SIZE, largeBurstSize);
		closeSpacedBurstThreshold = readDouble(properties, CLOSE_SPACED_BURSTS, closeSpacedBurstThreshold);
		throughputWindow = readDouble(properties, W_THROUGHPUT, throughputWindow);

		// Initialize sub-class members
		setProperties(properties);

		if (!errorLog.isEmpty()) {
			throw new ProfileException(errorLog);
		} else if (!init) {
			throw new ProfileException();
		}
	}

}// end Profile Class