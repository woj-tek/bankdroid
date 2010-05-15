package bankdroid.start;

import android.os.Bundle;
import android.view.Window;

import com.csaba.connector.BankService;

public class SearchTransactionActivity extends ServiceActivity
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.searchtransaction);
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		// TODO Auto-generated method stub

	}

}
