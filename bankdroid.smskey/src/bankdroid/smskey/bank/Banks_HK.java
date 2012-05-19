package bankdroid.smskey.bank;

class Banks_HK extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "DBS Bank", 300, // id, bankName, expiry
				new String[] { "+852649653703" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* [pP]assword[^:]*:[0-9a-zA-Z]+-([0-9]+).*") // transactionSigning, regexp
				}, "HK")); // country

	}
}
