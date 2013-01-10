package TempleRun.Util.SaveEnum;

public enum SaveType {
	MYSQL, CONFIG;
	
	public static SaveType getSaveType(String type) {
		if(type.equals("MYSQL")) {
			return MYSQL;
		} else if(type.equals("CONFIG")) {
			return CONFIG;
		} else {
			throw new IllegalArgumentException("[TempleRun] SaveType not found!");
		}
	}
}
