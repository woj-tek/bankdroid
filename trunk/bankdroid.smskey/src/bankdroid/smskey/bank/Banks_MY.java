package bankdroid.smskey.bank;

class Banks_MY extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "CIMB Bank", -1, // id, bankName, expiry
				new String[] { "66300" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* TAC[^:]*: ([0-9]+).*") // transactionSigning, regexp
				}, "MY")); // country

		addBank(new Bank(-1, "Maybank", -1, // id, bankName, expiry
				new String[] { "66628" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* TAC:([0-9]+).*") // transactionSigning, regexp
				}, "MY")); // country

	}
}
