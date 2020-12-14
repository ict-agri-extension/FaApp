package sindh.agriculureextension.fadiary.database;

public  class Queries {

    public static final String IMAGE_TABLE="IMAGE_TABLE";
    public static final String _ID="_ID";
    public static final String _ADDRESS="_ADDRESS";
    public static final String _LAT="_LAT";
    public static final String _LANG="_LANG";
    public static final String _IMAGE="_IMAGE";
    public static final String _FARMER="_FARMER";
    public static final String _QUESTION ="_QUESTION";
    public static final String _SUGGESTION="_SUGGESTION";
    public static final String _STATUS="_STATUS";
    public static final String _LOCATION_NAME="LOCATION_NAME";
    public static final String _FARMER_PHONE="_FARMER_PHONE";
    public static final String TITLE="TITLE";
    public static final String MESSAGE="MSG";
    public static final String DIARY_IMAGE="DIARY_IMG";
    public static final String DIARY_IMG_TAKEN="DIARY_IMG_TIME";
    public static final String DIARY_TABLE="DIARY";
    public static final String _TIME="_TIME";
    public static final String DIARY_TABLE_CREATE="CREATE TABLE IF NOT EXISTS "
            +DIARY_TABLE+ " ( "
            +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "
            +DIARY_IMG_TAKEN +" LONG , "
            +DIARY_IMAGE+" VARCHAR(1000) , "
            +_STATUS+" INT "
            +" ) "
            ;
    public static final String DIARY_TABLE_DROP="DROP TABLE if EXITS "+DIARY_TABLE;
    public static final String CREATE_IMAGE_TABLE="CREATE TABLE IF NOT EXISTS "+IMAGE_TABLE+" ( " +
            _ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,"
            +_ADDRESS+" VARCHAR(500) , "
            +_LAT+" DOUBLE , "
            +_LANG+" DOUBLE , "
            +_IMAGE+" VARCHAR(1000) , "
            +_FARMER+" VARCHAR(100) , "
            +_QUESTION+" VARCHAR(1000) , "
            +_SUGGESTION+" VARCHAR(1000) , "
            +_STATUS+" INT , "
            +_LOCATION_NAME+" VARCHAR(400) , "
            +_FARMER_PHONE+" VARCHAR(11) , "
            +_TIME+" LONG "
            +" ) ";
    public static final String DROP_IMAGE_TABLE="DROP TABLE IF EXISTS "+IMAGE_TABLE;
    public static final String  LOCUST_TABLE="locust";
    public static final String LOCUST_CREATE="CREATE TABLE IF NOT EXISTS "+LOCUST_TABLE+" ( "
            +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "
            +_LANG+" DOUBLE , "
            +_LAT+" DOUBLE , "
            +_STATUS+ " INT , "
            +_IMAGE+" VARCHAR(1000) , "
            +_ADDRESS+" VARCHAR(1000) , "
            +_TIME+" LONG "
            +" ) ";

    public static final String LOCUST_DROP="DROP TABLE IF EXISTS "+LOCUST_TABLE;

}
