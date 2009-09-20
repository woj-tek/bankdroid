package bankdroid.soda;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BankListActivity extends Activity
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.banklist);

		/*final ArrayAdapter<Bank> bankAdapter = new ArrayAdapter<Bank>(getBaseContext(), R.layout.banklistitem, Bank
				.getAvailableBanks());*/

		( (ListView) findViewById(R.id.bankListView) ).setAdapter(new BankArrayAdapter(Bank.getAvailableBanks()));

	}

	class BankArrayAdapter implements ListAdapter
	{
		Bank[] banks;
		private Set<DataSetObserver> dsos;

		public BankArrayAdapter( final Bank[] banks )
		{
			this.banks = banks;
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

			( (TextView) result.findViewById(R.id.bankName) ).setText(banks[position].getId());
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
}
