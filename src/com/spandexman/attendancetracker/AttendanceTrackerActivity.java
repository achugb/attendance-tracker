package com.spandexman.attendancetracker;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


public class AttendanceTrackerActivity extends ListActivity {
	
	private DbManager mydbmanager;
	private SharedPreferences SP;
	public double minPercent;
	AwesomeAdapter lvAdapter;
	
	private class AwesomeAdapter extends BaseAdapter
	{
		private LayoutInflater inflater;
		private Cursor cursor;
		
		
		public AwesomeAdapter(Context context)
		{
			inflater = LayoutInflater.from(context);
			
		}

		@Override
		public int getCount() { 
			return mydbmanager.fetchAllData().getCount();
		}

		@Override
		public Cursor getItem(int position) {
			return mydbmanager.fetchSingleData(position);
		}

		@Override
		public long getItemId(int position) {
			return position ;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null)
			{
				convertView = inflater.inflate(R.layout.list_layout, null);
				
				holder = new ViewHolder();
				holder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
				holder.tvAttended = (TextView)convertView.findViewById(R.id.tvAttended);
				holder.tvMissed = (TextView)convertView.findViewById(R.id.tvMissed);
				holder.tvPercentage = (TextView)convertView.findViewById(R.id.tvPercentage);
				holder.btAttended = (Button)convertView.findViewById(R.id.attended);
				holder.btMissed = (Button)convertView.findViewById(R.id.missed);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder)convertView.getTag();
			}
			

			cursor = mydbmanager.fetchSingleData(position);
			startManagingCursor(cursor);
			
			int a=cursor.getInt(cursor.getColumnIndex(DbManager.clmn_attended));
			int m=cursor.getInt(cursor.getColumnIndex(DbManager.clmn_missed));
			double p = ((double)a)/(a+m);
			
			holder.tvTitle.setText(cursor.getString(cursor.getColumnIndex(DbManager.clmn_title)));
			holder.tvAttended.setText("Attended: " + a);
			holder.tvMissed.setText("Missed: " + m);
			holder.tvPercentage.setText("Percentage: "+ (p*100));
			
			if (p<minPercent)
				holder.tvPercentage.setTextColor(Color.RED);
			else if (p==minPercent)
				holder.tvPercentage.setTextColor(Color.YELLOW);
			else
				holder.tvPercentage.setTextColor(Color.GRAY);
			
			return convertView;
			
		}
		private class ViewHolder
		{
			TextView tvTitle;
			TextView tvAttended;
			TextView tvMissed;
			TextView tvPercentage;
			Button btAttended;
			Button btMissed;
		}
		
	}
	
	public OnClickListener attendedListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			int position = getListView().getPositionForView(v);
			Cursor c = mydbmanager.fetchSingleData(position);
			startManagingCursor(c);
			
			int a=c.getInt(c.getColumnIndex(DbManager.clmn_attended));
			int m=c.getInt(c.getColumnIndex(DbManager.clmn_missed));
			String t = c.getString(c.getColumnIndex(DbManager.clmn_title));
			a++;
			mydbmanager.updateRow(position, t, a, m);
			lvAdapter.notifyDataSetChanged();
		}
	};
	
	public OnClickListener missedListener = new OnClickListener() {
			
		@Override
		public void onClick(View v) {
			
			int position = getListView().getPositionForView(v);
			Cursor c = mydbmanager.fetchSingleData(position);
			startManagingCursor(c);
			
			int a=c.getInt(c.getColumnIndex(DbManager.clmn_attended));
			int m=c.getInt(c.getColumnIndex(DbManager.clmn_missed));
			String t = c.getString(c.getColumnIndex(DbManager.clmn_title));
			m++;
			mydbmanager.updateRow(position, t, a, m);
			lvAdapter.notifyDataSetChanged();
		}
	};
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SP = getSharedPreferences("AttendanceTrackerPrefs", 0);
        minPercent = SP.getFloat("minPercent", (float)0.75);
        
        mydbmanager = new DbManager(this);
        mydbmanager.open();
        lvAdapter = new AwesomeAdapter(this);
        setListAdapter(lvAdapter);
    }
    
}
