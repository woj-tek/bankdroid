package bankdroid.smskey.bank;

class Banks_SE extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "Skandiabanken", -1, // id, bankName, expiry
				new String[] { "Skandia" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "Kod[: ]+([0-9]+) .*") // transactionSigning, regexp
				}, "SE")); // country

	}
}
