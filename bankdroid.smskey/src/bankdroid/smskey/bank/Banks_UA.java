package bankdroid.smskey.bank;

class Banks_UA extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "Alfa Bank", -1, // id, bankName, expiry
				new String[] { "AlfaBank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Kod[ :]+([0-9]+)[;\\. ]*.*") // transactionSigning, regexp
				}, "UA")); // country

		addBank(new Bank(-1, "Bank Forum", -1, // id, bankName, expiry
				new String[] { "BankForum" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*OTP[ :]+([A-Za-z0-9]+).*") // transactionSigning, regexp
				}, "UA")); // country

		addBank(new Bank(-1, "Bank Credit Dnepr", -1, // id, bankName, expiry
				new String[] { "Creditdnepr" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*Parol[^:]+[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "UA")); // country

		addBank(new Bank(-1, "LiqPAY", -1, // id, bankName, expiry
				new String[] { "1095" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "Parol[ :]+([0-9]+).*") // transactionSigning, regexp
				}, "UA")); // country

		addBank(new Bank(-1, "PrivatBank", -1, // id, bankName, expiry
				new String[] { "4116", "10060", "privat24.ua", "82215" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "[^:]+: ([0-9]+) www.privat24.ua .*"),// transactionSigning, regexp
						new Expression(false, "[^:]*[Pp]arol[^:]*[: ]+([0-9]+).*"), // transactionSigning, regexp
						new Expression(false, "Secure3D[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "UA")); // country

		addBank(new Bank(-1, "UBKI", -1, // id, bankName, expiry
				new String[] { "7660" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "Code[^:]*[: ]+([0-9]+) .*") // transactionSigning, regexp
				}, "UA")); // country

		addBank(new Bank(-1, "UkrSibBank", -1, // id, bankName, expiry
				new String[] { "0931777755" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "Password: ([0-9a-zA-Z]+) .*") // transactionSigning, regexp
				}, "UA")); // country

		addBank(new Bank(-1, "ПУМБ", -1, // id, bankName, expiry
				new String[] { "10943", "+380952952300" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*Parol[: ]+([0-9a-zA-Z]+) .*") // transactionSigning, regexp
				}, "UA")); // country
	}
}
