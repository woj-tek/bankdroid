package bankdroid.smskey.bank;

class Banks_BE extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "Dexia", -1, // id, bankName, expiry
				new String[] { "9119" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "Code [^0-9]+([0-9]+) .*") // transactionSigning, regexp
				}, "BE")); // country
	}
}
