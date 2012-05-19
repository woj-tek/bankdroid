package bankdroid.smskey.bank;

class Banks_AT extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "A-Trust", 300, // id, bankName, expiry
				new String[] { "A-Trust" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN.*[: ]+([a-zA-Z0-9]+)") // transactionSigning, regexp
				}, "AT")); // country

		addBank(new Bank(-1, "Bank Austria", -1, // id, bankName, expiry
				new String[] { "+435050526101" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN[: ]+([a-zA-Z0-9]+)") // transactionSigning, regexp
				}, "AT")); // country

		addBank(new Bank(-1, "ERSTE Bank", 300, // id, bankName, expiry
				new String[] { "+436646601350", "+436646601353" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "[^:]+TAC-SMS[^:]+[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "AT")); // country

		addBank(new Bank(-1, "PSK Bank", -1, // id, bankName, expiry
				new String[] { "PSK" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN[: ]+([0-9]*)") // transactionSigning, regexp
				}, "AT")); // country

		addBank(new Bank(-1, "Volksbank", -1, // id, bankName, expiry
				new String[] { "Volksbank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN[: ]+([0-9]+)") // transactionSigning, regexp
				}, "AT")); // country

		addBank(new Bank(-1, "easybank", -1, // id, bankName, expiry
				new String[] { "easybank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN[: ]+([0-9]*)") // transactionSigning, regexp
				}, "AT")); // country

		addBank(new Bank(-1, "Raiffeisen", 300, // id, bankName, expiry
				new String[] { "Raiffeisen" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN lautet[ :]+([a-zA-Z0-9]+) .*") // transactionSigning, regexp
				}, "AT")); // country

	}
}
