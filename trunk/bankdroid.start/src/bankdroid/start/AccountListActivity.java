package bankdroid.start;

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

import com.csaba.connector.model.Account;

public class AccountListActivity extends Activity
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.accountlist);

		( (ListView) findViewById(R.id.accountList) ).setAdapter(new AccountArrayAdapter(new Account[] {}));
	}

	class AccountArrayAdapter implements ListAdapter
	{
		Account[] accounts;
		private Set<DataSetObserver> dsos;

		public AccountArrayAdapter( final Account[] accounts )
		{
			this.accounts = accounts;
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
			return accounts.length;
		}

		@Override
		public Object getItem( final int index )
		{
			return accounts[index];
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
				result = LayoutInflater.from(getBaseContext()).inflate(R.layout.accountlist_item, parent, false);
			}

			( (TextView) result.findViewById(R.id.accountNumber) ).setText(accounts[position].getNumber());
			( (TextView) result.findViewById(R.id.availableBalance) ).setText(accounts[position].getAvailableBalance()
					.toString());

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
			return accounts == null || accounts.length == 0;
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
