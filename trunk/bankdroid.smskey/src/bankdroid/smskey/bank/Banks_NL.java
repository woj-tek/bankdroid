package bankdroid.smskey.bank;

class Banks_NL extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "DigiD", -1, // id, bankName, expiry
				new String[] { "DigiD" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* code is[: ]+([0-9a-zA-Z]+).*") // transactionSigning, regexp
				}, "NL")); // country

		addBank(new Bank(-1, "ING Bank", -1, // id, bankName, expiry
				new String[] { "ING" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Volgnummer .* TAN-code ([0-9]+).*") // transactionSigning, regexp
				}, "NL")); // country

	}
}
