package bankdroid.soda;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 *FIXME use SimpleCursorAdapter
 *FIXME implement bank deletion
 * @author gyenes
 *
 */
public class BankListActivity extends Activity implements Codes, OnItemClickListener
{
	BankArrayAdapter bankArrayAdapter;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.banklist);

		bankArrayAdapter = new BankArrayAdapter(BankManager.getAllBanks(getBaseContext()));

		( (ListView) findViewById(R.id.bankListView) ).setAdapter(bankArrayAdapter);
		( (ListView) findViewById(R.id.bankListView) ).setOnItemClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		bankArrayAdapter.notifyDatasetChanged();
	}

	class BankArrayAdapter implements ListAdapter
	{
		Bank[] banks;
		private Set<DataSetObserver> dsos;

		public BankArrayAdapter( final Bank[] banks )
		{
			this.banks = banks;
		}

		public void notifyDatasetChanged()
		{
			for ( final DataSetObserver dso : dsos )
			{
				dso.onChanged();
			}

		}

		@Override
		public boolean areAllItemsEnabled()
		{
			return true;
		}

		@Override
		public boolean isEnabled( final int position )
		{
			return true;
		}

		@Override
		public int getCount()
		{
			return banks.length;
		}

		@Override
		public Object getItem( final int index )
		{
			return banks[index];
		}

		@Override
		public long getItemId( final int index )
		{
			return index;
		}

		@Override
		public int getItemViewType( final int arg0 )
		{
			return 0; //only one view type will be used.
		}

		@Override
		public View getView( final int position, final View contentView, final ViewGroup parent )
		{
			View result = contentView; // always the same view will be used
			if ( result == null )
			{//no view was created yet
				result = LayoutInflater.from(getBaseContext()).inflate(R.layout.banklistitem, parent, false);
			}

			( (TextView) result.findViewById(R.id.bankName) ).setText(banks[position].getName());
			( (TextView) result.findViewById(R.id.phoneNumber) ).setText(banks[position].getPhoneNumbers()[0]);

			return result;
		}

		@Override
		public int getViewTypeCount()
		{
			return 1;
		}

		@Override
		public boolean hasStableIds()
		{
			return true;
		}

		@Override
		public boolean isEmpty()
		{
			return banks == null || banks.length == 0;
		}

		@Override
		public void registerDataSetObserver( final DataSetObserver dso )
		{
			if ( dsos == null )
				dsos = new HashSet<DataSetObserver>();

			dsos.add(dso);
		}

		@Override
		public void unregisterDataSetObserver( final DataSetObserver dso )
		{
			if ( dsos == null )
				return;

			dsos.remove(dso);
		}

	}

	@Override
	public void onItemClick( final AdapterView<?> parent, final View view, final int position, final long id )
	{
		if ( parent.getId() == R.id.bankListView )
		{
			Log.d(TAG, "Following pos is selected: " + position);

			final Intent intent = new Intent();
			intent.setClass(getBaseContext(), BankEditActivity.class);
			intent.putExtra(BANKDROID_SODA_BANK, (Bank) parent.getAdapter().getItem(position));
			startActivity(intent);
		}
	}
}
