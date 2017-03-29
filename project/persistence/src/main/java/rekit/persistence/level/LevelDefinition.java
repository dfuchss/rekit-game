package rekit.persistence.level;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import rekit.config.GameConf;

/**
 *
 * This class holds all necessary information about a level.
 *
 */
public final class LevelDefinition implements Comparable<LevelDefinition> {

	private final LevelType type;
	private final String name;
	private final int seed;
	private int arcadeNum;

	public LevelDefinition(InputStream in, LevelType type) {
		this(in, type, GameConf.PRNG.nextInt());
	}

	public LevelDefinition(InputStream in, LevelType type, int seed) {
		this.type = type;
		this.seed = seed;
		Scanner scanner = new Scanner(in, Charset.defaultCharset().name());
		scanner.useDelimiter("\\A");
		String input = scanner.hasNext() ? scanner.next() : "";
		scanner.close();

		LevelParser.parseLevel(input, this);
		this.name = this.calcName();
		this.arcadeNum = -1;
	}

	LevelDefinition(InputStream inputStream, int arcadeNumber) {
		this(inputStream, LevelType.Arcade);
		this.arcadeNum = arcadeNumber;
	}

	private String calcName() {
		String name = null;
		if (this.type != LevelType.Arcade) {
			name = this.type.toString().replace('_', ' ');
		}
		if (this.isSettingSet(SettingKey.NAME)) {
			name = this.getSetting(SettingKey.NAME);
		}
		return (name == null ? this.getID() : name);
	}

	private List<String[][]> structures = new LinkedList<>();

	void addStructure(List<String[]> lines) {
		String[][] structure = new String[lines.size()][];
		int i = 0;
		for (String[] line : lines) {
			structure[i++] = line.clone();
		}
		this.structures.add(structure);
	}

	private SortedMap<String, String> aliases = new TreeMap<>();

	void setAlias(String toReplace, String replaceWith) {
		if (this.aliases.put(toReplace, replaceWith) != null) {
			GameConf.GAME_LOGGER.warn("Multiple definitions (alias) for " + toReplace);
		}

	}

	public String getAlias(String key) {
		return this.aliases.get(key);
	}

	private SortedMap<String, String> settings = new TreeMap<>();

	void setSetting(SettingKey key, String value) {
		if (this.settings.put(key.getID(), value) != null) {
			GameConf.GAME_LOGGER.warn("Multiple definitions (settings) for " + key);
		}

	}

	public String getSetting(SettingKey key) {
		String val = this.settings.get(key.getID());
		return val == null ? null : val.replace('_', ' ');
	}

	public boolean isSettingSet(SettingKey key) {
		if (!this.settings.containsKey(key.getID())) {
			return false;
		}
		String setting = this.settings.get(key.getID());
		if (setting.toLowerCase(Locale.ENGLISH).matches("(true|false)")) {
			return setting.equalsIgnoreCase("true");
		}
		return this.settings.get(key.getID()) != null;
	}

	private SortedMap<String, String> bossSettings = new TreeMap<>();

	void setBossSetting(String setting, String value) {
		if (this.bossSettings.put(setting, value) != null) {
			GameConf.GAME_LOGGER.warn("Multiple definitions (bosssettings) for " + setting);
		}

	}

	public String getBossSetting(String key) {
		return this.bossSettings.get(key);
	}

	public LevelType getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	private String id = null;

	public synchronized String getID() {
		if (this.id != null) {
			return this.id;
		}
		this.id = this.calcID();
		return this.id;

	}

	private String calcID() {
		MessageDigest cs = null;
		try {
			cs = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			GameConf.GAME_LOGGER.fatal(e.getMessage());
			return null;
		}
		StringBuilder content = new StringBuilder();
		content.append(this.type);
		if (this.type == LevelType.Arcade) {
			this.structures.forEach(struct -> Arrays.stream(struct).forEach(row -> Arrays.stream(row).forEach(elem -> content.append(elem.trim()))));
			this.aliases.forEach((k, v) -> content.append(k).append(v));
			this.settings.forEach((k, v) -> content.append(k).append(v));
			this.bossSettings.forEach((k, v) -> content.append(k).append(v));
		}
		cs.update(content.toString().getBytes());
		StringBuffer res = new StringBuffer();
		for (byte bytes : cs.digest()) {
			res.append(String.format("%02x", bytes & 0xff));
		}
		return res.toString();
	}

	private Map<String, Serializable> data = new HashMap<>();

	public void setData(DataKey key, Serializable value) {
		this.setData(key, value, true);
	}

	void setData(DataKey key, Serializable value, boolean notify) {
		this.data.put(key.getKey(), value);
		if (notify) {
			LevelManager.contentChanged();
		}
	}

	public Serializable getData(DataKey key) {
		Serializable data = this.data.get(key.getKey());
		if (data == null) {
			return key.getDefaultVal();
		}
		return data;
	}

	public long getSeed() {
		return this.seed;
	}

	public int amountOfStructures() {
		return this.structures.size();
	}

	public String[][] getStructure(int idx) {
		return this.structures.get(idx);
	}

	public int getArcadeNum() {
		return this.arcadeNum;
	}

	@Override
	public int compareTo(LevelDefinition o) {
		return 2 * this.type.compareTo(o.getType()) + Integer.compare(this.arcadeNum, o.getArcadeNum());
	}

}