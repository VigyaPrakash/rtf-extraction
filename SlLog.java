	import java.util.Vector;

	import lotus.domino.*;
	public class SlLog { 
	static Vector vLog=new Vector();



		public static void addLog(String logEntries)
		{
			vLog.add(logEntries);

		}

		public static void logEntry(Database db, String type)
		
		{
			
			try{
			lotus.domino.Document d;
			Item n;
			d = db.createDocument();
			d.replaceItemValue("Form", "Log");
			d.replaceItemValue("Type", type);
			d.replaceItemValue("LogEntries", "");
			n=d.getFirstItem("LogEntries");
			for(int i=0;i<vLog.size();i++)
			{
				if(d.getSize() + vLog.size() > 30000)
				{
					d.save();
					d = db.createDocument();
					d.replaceItemValue("Form", "Log");
					d.replaceItemValue("Type", type);
					d.replaceItemValue("LogEntries", "");
					n=d.getFirstItem("LogEntries");
				}
				n.setSummary(true);
				n.appendToTextList(vLog.get(i).toString());
				d.save();
			}
			
				}
				catch(NotesException e)
				{
				e.printStackTrace();
				}
		}
		}
		

