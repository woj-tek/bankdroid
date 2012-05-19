package bankdroid.smskey.bank;

class Banks_SK extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "Tatra banka", -1, // id, bankName, expiry
				new String[] { "+421902022200" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "[^:]* kod: ([A-Z0-9a-z]*)"),// transactionSigning, regexp
						new Expression(true, ".* KOD=([0-9-]+)") // transactionSigning, regexp
				}, "SK")); // country

		addBank(new Bank(-1, "Unicredit", -1, // id, bankName, expiry
				new String[] { "UniCredit" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Kod ([A-Z0-9a-z]+)") // transactionSigning, regexp
				}, "SK")); // country

		addBank(new Bank(-1, "VUB banka", -1, // id, bankName, expiry
				new String[] { "323" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* KOD=([0-9-]+)") // transactionSigning, regexp
				}, "SK")); // country

		addBank(new Bank(-1, "Zuno Bank", 600, // id, bankName, expiry
				new String[] { "+421902020722" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "[^:]*Kod[^:]*: ([0-9]+) .*") // transactionSigning, regexp
				}, "SK")); // country

		addBank(new Bank(-1, "mBank", -1, // id, bankName, expiry
				new String[] { "2265", "2287", "+421902026050" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* kod: ([0-9]*) mBank.*") // transactionSigning, regexp
				}, "SK")); // country

		addBank(new Bank(-1, "ČSOB", -1, // id, bankName, expiry
				new String[] { "247", "+421940661750" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*kod[: ]+([0-9a-zA-Z-]+).*") // transactionSigning, regexp
				}, "SK")); // country

		addBank(new Bank(-1, "Orange", -1, // id, bankName, expiry
				new String[] { "905" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*SMS kod je ([a-zA-Z0-9]+)\\..*") // transactionSigning, regexp
				}, "SK")); // country

		addBank(new Bank(-1, "Dexia banka", -1, // id, bankName, expiry
				new String[] { "+421902025600" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "Dexia [^:]*[: ]+([a-zA-Z0-9]+).*"), // transactionSigning, regexp
						new Expression(true, "Dexia[^:]*[: ]+Kod[ ]+([a-zA-Z0-9]+).*"), // transactionSigning, regexp
						new Expression(false, "Dexia[^:]*[: ]+([0-9]+).*"), // transactionSigning, regexp
						new Expression(true, "Prima[^:]*[: ]+Kod[ ]+([a-zA-Z0-9]+).*"), // transactionSigning, regexp
						new Expression(false, "Prima[^:]*[: ]+([0-9]+).*"), // transactionSigning, regexp
				}, "SK")); // country
	}
}
