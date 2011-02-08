package bankdroid.soda.bank.test;

import java.util.regex.Pattern;

import junit.framework.TestCase;
import bankdroid.soda.bank.Bank;
import bankdroid.soda.bank.BankDescriptor;
import bankdroid.soda.bank.Expression;

public abstract class BankTestBase extends TestCase
{

	public Bank testBank( final String bankName, final String phoneNumber, final String message, final String code )
			throws Exception
	{
		final Bank bank = testBank(bankName, phoneNumber);

		assertTrue(bankName + ": Code assertion failed for code " + code, code.equals(bank.extractCode(message)));
		assertNull(
				bankName + ": Inverse code assertion failed for code " + code,
				bank
						.extractCode("Sikeres bejelentkezas - FHB NetB@nk. Felhasznalonev: Minta Janos. Idopont: 2009.08.24 14:30"));

		return bank;
	}

	public Bank testBank( final String bankName, final String phoneNumber ) throws Exception
	{
		final Bank bank = BankDescriptor.findByPhoneNumber(phoneNumber);

		//check phone number matching
		assertTrue(bankName + ": Failed to realize phone number.", bankName.equals(bank.getName()));

		//check logo availability
		assertNotNull(BankDescriptor.loadLogo(bank));

		//check expressions
		for ( final Expression exp : bank.getExtractExpressions() )
		{
			final String pattern = exp.getExpression();
			try
			{
				final Pattern compiled = Pattern.compile(pattern);
				assertNotNull(bankName + ": compiled patter is null: " + pattern, compiled);
			}
			catch ( final Exception e )
			{
				e.printStackTrace();
				assertTrue(bankName + ": compilation of pattern failed: " + pattern, false);
			}
		}
		return bank;
	}
}
