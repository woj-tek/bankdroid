package bankdroid.smskey.bank;

class Banks_US extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "Bank of America", -1, // id, bankName, expiry
				new String[] { "73981" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* SafePass .*: ([0-9]*)"),// transactionSigning, regexp
						new Expression(false, ".* SafePass .* code is \"([0-9]*)\"") // transactionSigning, regexp
				}, "US")); // country

		addBank(new Bank(-1, "Chase Bank", -1, // id, bankName, expiry
				new String[] { "242733" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Code[^0-9]*([0-9]+).*") // transactionSigning, regexp
				}, "US")); // country

	}
}
