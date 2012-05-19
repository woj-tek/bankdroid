package bankdroid.smskey.bank;

class Banks_CZ extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "AXA Bank", -1, // id, bankName, expiry
				new String[] { "AXA Bank", "2922265" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* kod [^:]*: ([0-9]*) .*") // transactionSigning, regexp
				}, "CZ")); // country

		addBank(new Bank(-1, "Česká Spořitelna", -1, // id, bankName, expiry
				new String[] { "39901" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*SMS kod[^:]*[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "CZ")); // country

		addBank(new Bank(-1, "FIO Bank", -1, // id, bankName, expiry
				new String[] { "+420725664066", "+420725664070", "+420724346388", "+420724346329" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*kod[^:]*: ([0-9]*) .*") // transactionSigning, regexp
				}, "CZ")); // country

		addBank(new Bank(-1, "Fortissimo", -1, // id, bankName, expiry
				new String[] { "+420720002971" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*kod[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "CZ")); // country

		addBank(new Bank(-1, "GE Money Bank", -1, // id, bankName, expiry
				new String[] { "GEMB" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "Vas .* klic [^:]+: ([0-9]+)"),// transactionSigning, regexp
						new Expression(true, ".* Mobilni klic: ([0-9]+)") // transactionSigning, regexp
				}, "CZ")); // country

		addBank(new Bank(-1, "KB", -1, // id, bankName, expiry
				new String[] { "65430", "999061", "5270" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*kod[^:]*: ([0-9]+).*") // transactionSigning, regexp
				}, "CZ")); // country

		addBank(new Bank(-1, "Poštovní spořitelna", -1, // id, bankName, expiry
				new String[] { "39701" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, "[^:]+[: ]+([a-zA-Z0-9-]+) kod .*") // transactionSigning, regexp
				}, "CZ")); // country

		addBank(new Bank(-1, "Raiffeisenbank", -1, // id, bankName, expiry
				new String[] { "999024", "+4737", "+39024", "+1024", "1024" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*[kK]od: *([0-9]+) .*"),// transactionSigning, regexp
						new Expression(false, ".* autentizacni kod: ([0-9]+) .*"),// transactionSigning, regexp
						new Expression(false, ".* code: ([0-9]+) .*") // transactionSigning, regexp
				}, "CZ")); // country

		addBank(new Bank(-1, "UniCredit Bank", -1, // id, bankName, expiry
				new String[] { "5200" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "UniCredit Bank.*Prihlaseni.*Kod ([a-zA-Z0-9]+)"),// transactionSigning, regexp
						new Expression(true, "UniCredit Bank.*Kod ([a-zA-Z0-9]+)") // transactionSigning, regexp
				}, "CZ")); // country

		addBank(new Bank(-1, "mBank", -1, // id, bankName, expiry
				new String[] { "mBank", "+6011", "mBank-CZ" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* heslo: ([0-9]*) mBank.*") // transactionSigning, regexp
				}, "CZ")); // country

		addBank(new Bank(-1, "ČSOB", 600, // id, bankName, expiry
				new String[] { "5808", "39601", "999020", "999023", "+421940661750" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, "CSOB: ([0-9a-z]*-[0-9a-z]*-[0-9a-z]*) .*"), // transactionSigning, regexp
						new Expression(true, "CSOB: ([0-9a-z]*) .*"), // transactionSigning, regexp
						new Expression(true, ".* SMS kod[: ]+([0-9a-zA-Z]+).*") // transactionSigning, regexp
				}, "CZ")); // country

		addBank(new Bank(-1, "ZUNO", -1, // id, bankName, expiry
				new String[] { "ZUNO" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, "[^:]*Kod[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "CZ")); // country
	}
}
