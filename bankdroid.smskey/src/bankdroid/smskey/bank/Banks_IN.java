package bankdroid.smskey.bank;

class Banks_IN extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "State Bank of India", -1, // id, bankName, expiry
				new String[] { "+919404400000" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*PW[^:]*[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "IN")); // country

	}
}
