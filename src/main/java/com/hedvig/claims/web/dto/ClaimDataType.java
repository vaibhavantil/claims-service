package com.hedvig.claims.web.dto;

public class ClaimDataType {

	public static enum DataType {TEXT, DATE, ASSET, FILE};
	public DataType type;
	public String name;
	public String title;
	
	public ClaimDataType(DataType type, String name, String title) {
		super();
		this.type = type;
		this.name = name;
		this.title = title;
	}
}
