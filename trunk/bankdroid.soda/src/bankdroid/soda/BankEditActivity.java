package bankdroid.soda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class BankEditActivity extends Activity implements OnClickListener, Codes
{

	private final static int[][] PATTERN_FIELDS = new int[][] {//
	{ R.id.removePattern1, R.id.pattern1 },//
			{ R.id.removePattern2, R.id.pattern2 },//
			{ R.id.removePattern3, R.id.pattern3 } };

	private final static int[][] PHONE_FIELDS = new int[][] {//
	{ R.id.removePhoneNumber1, R.id.phoneNumber1 },//
			{ R.id.removePhoneNumber2, R.id.phoneNumber2 },//
			{ R.id.removePhoneNumber3, R.id.phoneNumber3 } };

	private int numberOfPhones = 1;
	private int numberOfPatterns = 1;

	private Bank bank;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bankedit);

		( (Button) findViewById(R.id.done) ).setOnClickListener(this);
		( (Button) findViewById(R.id.cancel) ).setOnClickListener(this);
		( (ImageButton) findViewById(R.id.addPhoneNumber) ).setOnClickListener(this);
		( (ImageButton) findViewById(R.id.addPattern) ).setOnClickListener(this);
		( (ImageButton) findViewById(R.id.removePattern3) ).setOnClickListener(this);
		( (ImageButton) findViewById(R.id.removePattern2) ).setOnClickListener(this);
		( (ImageButton) findViewById(R.id.removePattern1) ).setOnClickListener(this);
		( (ImageButton) findViewById(R.id.removePhoneNumber3) ).setOnClickListener(this);
		( (ImageButton) findViewById(R.id.removePhoneNumber2) ).setOnClickListener(this);
		( (ImageButton) findViewById(R.id.removePhoneNumber1) ).setOnClickListener(this);

		showLines(R.id.addPattern, PATTERN_FIELDS, numberOfPatterns);
		showLines(R.id.addPhoneNumber, PHONE_FIELDS, numberOfPhones);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		final Intent intent = getIntent();
		if ( intent != null && intent.getSerializableExtra(BANKDROID_SODA_BANK) != null )
		{
			final Bank bank = (Bank) intent.getSerializableExtra(BANKDROID_SODA_BANK);

			( (ImageView) findViewById(R.id.bankLogo) ).setImageResource(bank.getIconId());
			( (EditText) findViewById(R.id.bankName) ).setText(bank.getName());
			( (EditText) findViewById(R.id.expiry) ).setText(String.valueOf(bank.getExpiry()));

			final String[] pn = bank.getPhoneNumbers();
			final String[] ee = bank.getExtractExpression();

			showLines(R.id.addPattern, PATTERN_FIELDS, ee.length);
			for ( int i = 0; i < ee.length; i++ )
			{
				( (EditText) findViewById(PATTERN_FIELDS[i][1]) ).setText(ee[i]);
			}

			showLines(R.id.addPhoneNumber, PHONE_FIELDS, pn.length);
			for ( int i = 0; i < pn.length; i++ )
			{
				( (EditText) findViewById(PHONE_FIELDS[i][1]) ).setText(pn[i]);
			}

			this.bank = bank;
		}
	}

	private void showLines( final int topId, final int[][] fields, final int row )
	{
		//set visibility
		for ( int i = 0; i < row; i++ )
		{
			final int[] viewIds = fields[i];
			for ( int j = 0; j < viewIds.length; j++ )
			{
				if ( !( i == 0 && j == 0 ) ) // the very first element should remain invisible
					findViewById(viewIds[j]).setVisibility(View.VISIBLE);
			}
		}

		for ( int i = row; i < fields.length; i++ )
		{
			final int[] viewIds = fields[i];
			for ( int j = 0; j < viewIds.length; j++ )
			{
				findViewById(viewIds[j]).setVisibility(View.INVISIBLE);
			}
		}

		//restore layout params
		for ( int i = 0; i < row - 1; i++ )
		{
			final View below = findViewById(fields[i][0]);

			final RelativeLayout.LayoutParams layoutParams = (LayoutParams) below.getLayoutParams();
			layoutParams.addRule(RelativeLayout.BELOW, fields[i + 1][0]);
		}
		//set high
		final View high = findViewById(fields[row - 1][0]);

		final RelativeLayout.LayoutParams layoutParams = (LayoutParams) high.getLayoutParams();
		layoutParams.addRule(RelativeLayout.BELOW, topId);

		high.getParent().requestLayout();
	}

	@Override
	protected void onSaveInstanceState( final Bundle outState )
	{
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState( final Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onClick( final View button )
	{
		if ( button.getId() == R.id.done )
		{
			finish(); //FIXME save changes here
		}
		else if ( button.getId() == R.id.cancel )
		{
			finish();
		}
		else if ( button.getId() == R.id.removePattern2 )
		{
			numberOfPatterns = 1;
			showLines(R.id.addPattern, PATTERN_FIELDS, 1);
		}
		else if ( button.getId() == R.id.removePattern3 )
		{
			numberOfPatterns = 2;
			showLines(R.id.addPattern, PATTERN_FIELDS, 2);
		}
		else if ( button.getId() == R.id.removePhoneNumber2 )
		{
			numberOfPhones = 1;
			showLines(R.id.addPhoneNumber, PHONE_FIELDS, 1);
		}
		else if ( button.getId() == R.id.removePhoneNumber3 )
		{
			numberOfPhones = 2;
			showLines(R.id.addPhoneNumber, PHONE_FIELDS, 2);
		}
		else if ( button.getId() == R.id.addPhoneNumber )
		{
			if ( numberOfPhones > 2 )
			{
				final Toast toast = Toast.makeText(getBaseContext(), R.string.tooMuchPhoneNumber, Toast.LENGTH_SHORT);
				toast.show();
			}
			else
			{
				numberOfPhones++;
				showLines(R.id.addPhoneNumber, PHONE_FIELDS, numberOfPhones);
			}
		}
		else if ( button.getId() == R.id.addPattern )
		{
			if ( numberOfPatterns > 2 )
			{
				final Toast toast = Toast.makeText(getBaseContext(), R.string.tooMuchPattern, Toast.LENGTH_SHORT);
				toast.show();
			}
			else
			{
				numberOfPatterns++;
				showLines(R.id.addPattern, PATTERN_FIELDS, numberOfPatterns);
			}
		}

	}
}
