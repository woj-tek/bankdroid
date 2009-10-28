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

/**
 * FIXME work with direct DB modifications / read
 * FIXME implement new bank creation
 * @author gyenes
 *
 */
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
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		final Intent intent = getIntent();
		if ( bank == null && intent != null && intent.getSerializableExtra(BANKDROID_SODA_BANK) != null )
		{
			final Bank bank = (Bank) intent.getSerializableExtra(BANKDROID_SODA_BANK);
			this.bank = (Bank) bank.clone();
		}

		if ( bank != null )
		{

			( (ImageView) findViewById(R.id.bankLogo) ).setImageResource(bank.getIconId());
			( (EditText) findViewById(R.id.bankName) ).setText(bank.getName());
			( (EditText) findViewById(R.id.expiry) ).setText(String.valueOf(bank.getExpiry()));

			final String[] pn = bank.getPhoneNumbers();
			final String[] ee = bank.getExtractExpressions();

			showLines(R.id.addPattern, PATTERN_FIELDS, ee);

			showLines(R.id.addPhoneNumber, PHONE_FIELDS, pn);
		}
	}

	private void storeValues()
	{
		bank.setName(( (EditText) findViewById(R.id.bankName) ).getText().toString());
		bank.setExpiry(Integer.parseInt(( (EditText) findViewById(R.id.expiry) ).getText().toString()));

		saveFields(PATTERN_FIELDS, bank.getExtractExpressions());
		saveFields(PHONE_FIELDS, bank.getPhoneNumbers());
	}

	private void saveFields( final int[][] fields, final String[] store )
	{
		final int count = store.length;
		for ( int i = 0; i < count; i++ )
		{
			store[i] = ( (EditText) findViewById(fields[i][1]) ).getText().toString();
		}
	}

	private void showLines( final int topId, final int[][] fields, final String[] values )
	{
		final int row = values.length;
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

		//set values
		for ( int i = 0; i < row; i++ )
		{
			( (EditText) findViewById(fields[i][1]) ).setText(values[i]);
		}
	}

	@Override
	protected void onSaveInstanceState( final Bundle outState )
	{
		super.onSaveInstanceState(outState);
		storeValues();
		outState.putSerializable(BANKDROID_SODA_BANK, bank);
	}

	@Override
	protected void onRestoreInstanceState( final Bundle savedInstanceState )
	{
		super.onRestoreInstanceState(savedInstanceState);
		if ( savedInstanceState.containsKey(BANKDROID_SODA_BANK) )
		{
			this.bank = (Bank) savedInstanceState.getSerializable(BANKDROID_SODA_BANK);
		}
	}

	@Override
	public void onClick( final View button )
	{
		switch ( button.getId() )
		{
		case R.id.done:
			storeValues();
			BankManager.storeBank(getBaseContext(), bank);
			finish();
			break;

		case R.id.cancel:
			finish();
			break;

		case R.id.removePattern1:
			removePattern(1);
			break;

		case R.id.removePattern2:
			removePattern(2);
			break;

		case R.id.removePattern3:
			removePattern(3);
			break;

		case R.id.removePhoneNumber1:
			removePhoneNumber(1);
			break;

		case R.id.removePhoneNumber2:
			removePhoneNumber(2);
			break;

		case R.id.removePhoneNumber3:
			removePhoneNumber(3);
			break;

		case R.id.addPhoneNumber:
			addPhoneNumber();
			break;

		case R.id.addPattern:
			addPattern();
			break;
		}

	}

	private void addPattern()
	{
		final int numberOfPatterns = bank.getExtractExpressions().length;
		if ( numberOfPatterns > 2 )
		{
			final Toast toast = Toast.makeText(getBaseContext(), R.string.tooMuchPattern, Toast.LENGTH_SHORT);
			toast.show();
		}
		else
		{
			bank.addExtractExpression("");
			showLines(R.id.addPattern, PATTERN_FIELDS, bank.getExtractExpressions());
		}
	}

	private void addPhoneNumber()
	{
		final int numberOfPhones = bank.getPhoneNumbers().length;
		if ( numberOfPhones > 2 )
		{
			final Toast toast = Toast.makeText(getBaseContext(), R.string.tooMuchPhoneNumber, Toast.LENGTH_SHORT);
			toast.show();
		}
		else
		{
			bank.addPhoneNumber("");
			showLines(R.id.addPhoneNumber, PHONE_FIELDS, bank.getPhoneNumbers());
		}
	}

	private void removePhoneNumber( final int i )
	{
		if ( bank.getPhoneNumbers().length == 1 )
		{
			final Toast toast = Toast.makeText(getBaseContext(), R.string.minPhoneNumber, Toast.LENGTH_SHORT);
			toast.show();
		}
		else
		{
			storeValues();
			bank.removePhoneNumber(i - 1);
			showLines(R.id.addPhoneNumber, PHONE_FIELDS, bank.getPhoneNumbers());
		}
	}

	private void removePattern( final int i )
	{
		if ( bank.getExtractExpressions().length == 1 )
		{
			final Toast toast = Toast.makeText(getBaseContext(), R.string.minPattern, Toast.LENGTH_SHORT);
			toast.show();
		}
		else
		{
			storeValues();
			bank.removeExtractExpression(i - 1);
			showLines(R.id.addPattern, PATTERN_FIELDS, bank.getExtractExpressions());
		}
	}

}
