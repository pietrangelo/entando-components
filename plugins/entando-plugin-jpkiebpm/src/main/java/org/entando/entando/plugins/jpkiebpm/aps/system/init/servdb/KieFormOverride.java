package org.entando.entando.plugins.jpkiebpm.aps.system.init.servdb;

import com.j256.ormlite.field.*;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = KieFormOverride.TABLE_NAME)
public class KieFormOverride {

	public KieFormOverride() {}

	@DatabaseField(columnName = "id",
		dataType = DataType.INTEGER,
		 canBeNull=false, id = true)
	private int _id;
        
	@DatabaseField(columnName = "widgetinfoid",
		dataType = DataType.INTEGER,
		 canBeNull=false)
	private int _widgetInfoId;

	@DatabaseField(columnName = "date",
			dataType = DataType.DATE,
		 canBeNull=false)
	private Date _date;

	@DatabaseField(columnName = "field",
		dataType = DataType.STRING,
		width=255,  canBeNull=false)
	private String _field;

	@DatabaseField(columnName = "containerid",
		dataType = DataType.LONG_STRING,
		 canBeNull=false)
	private String _containerId;

	@DatabaseField(columnName = "processid",
		dataType = DataType.LONG_STRING,
		 canBeNull=false)
	private String _processId;

	@DatabaseField(columnName = "sourceid",
		dataType = DataType.LONG_STRING,
		 canBeNull=false)
	private String _sourceid;

	@DatabaseField(columnName = "override",
		dataType = DataType.LONG_STRING,
		 canBeNull=false)
	private String _override;

	@DatabaseField(columnName = "active",
		dataType = DataType.BOOLEAN,
		 canBeNull=false)
	private boolean _active;

	@DatabaseField(columnName = "online",
		dataType = DataType.BOOLEAN,
		 canBeNull=false)
	private boolean _online;

	public static final String TABLE_NAME = "jpkiebpm_kieformoverride";
}
