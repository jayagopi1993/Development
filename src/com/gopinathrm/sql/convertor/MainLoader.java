package com.gopinathrm.sql.convertor;

import java.io.File;

public class MainLoader {

	
	
	public static void main(String[] args) {
		File folder = new File("D:\\Workspace\\JavaWS\\DDLFiles");
		DDLConvertLoader ddlConvertLoader = new DDLConvertLoader();
		ddlConvertLoader.convertDDL(folder,"ORACLE", "MYSQL");
	}
	
	
	/*public static void main(String[] args) {
		String ss = "Create table tt ( test number , testt varchar2(100), testtt number  (19,0), tth number(5,0), sddd number(7,0), sdfse number(3,0) default 'a' ); ";
		
		
		
		
		
		if(ss.contains("number")) {
			
			System.out.println(ss);
			boolean sss=true;
			while(sss) {
				if(ss.contains("number ")) {
					ss=ss.replace("number ", "number");
				}else {
					sss=!sss;
				}
			}
			
		
			
			
			
			
			
			
			while(ss.contains("number")) {
			int indexOfNum = ss.indexOf("number");
			char symbol = ss.charAt(indexOfNum+6);
			
			if(Character.toString(symbol).matches("[,?]")) {
				ss=ss.replaceFirst("number", "NUMERIC");
			} else if(Character.toString(symbol).matches("[(?]")) {
				
				String ssed = ss.substring(indexOfNum+6, indexOfNum+14);
				String df = ssed.substring(ssed.indexOf("(")+1, ssed.indexOf(")"));
				String ssss[] = df.split(",");
				if(ssss.length>0) {
					Integer in = Integer.parseInt(ssss[0]);
					System.out.println(in);
					System.out.println("df length"+df.length());
					String pq=ss.substring(indexOfNum, indexOfNum+df.length()+8);
					System.out.println("PQ--"+pq);
					if(in<=3) {
						System.out.println(ss);
						ss=ss.replaceFirst("number", "TINYINT");
					}else if(in>3 && in<=5) {
						ss=ss.replaceFirst("number", "SMALLINT");
					}else if(in>5 && in<=7) {
						ss=ss.replaceFirst("number", "MEDIUMINT");
					}else if(in>7 && in<=10) {
						ss=ss.replaceFirst("number", "INTEGER");
					}else if(in>10) {
						ss=ss.replaceFirst("number", "BIGINT");
					}
				}
				
				
			//	ss=ss.replaceFirst("number", "NUMERIC");
			}
			
			}
			System.out.println(ss);
			
			System.out.println();
			System.out.println(ss.indexOf("number")+6);
			System.out.println(ss.charAt(23));
			System.out.println(ss.charAt(29));
			System.out.println(ss.indexOf(")", 23));
			System.out.println(ss.charAt(49));
		}

	}*/

}
