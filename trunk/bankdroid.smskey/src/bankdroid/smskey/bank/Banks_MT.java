package bankdroid.smskey.bank;

class Banks_MT extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "APS Bank", 60, // id, bankName, expiry
				new String[] { "APS Bank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Code .* is ([0-9]+).*") // transactionSigning, regexp
				}, "MT")); // country

	}
}
