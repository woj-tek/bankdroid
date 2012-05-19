package bankdroid.smskey.bank;

class Banks_NO extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "Difi", -1, // id, bankName, expiry
				new String[] { "Difi" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* MinID er: ([0-9a-zA-Z]+).*") // transactionSigning, regexp
				}, "NO")); // country

		addBank(new Bank(-1, "Skandiabanken", -1, // id, bankName, expiry
				new String[] { "+4781001001" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* engangspassord er ([0-9a-zA-Z]+)"),// transactionSigning, regexp
						new Expression(false, "([0-9a-zA-Z]+) er .* engangspassord .*") // transactionSigning, regexp
				}, "NO")); // country

	}
}
