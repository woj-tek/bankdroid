package bankdroid.smskey.bank;

class Banks_SG extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "ANZ Singapore", 300, // id, bankName, expiry
				new String[] { "ANZ" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*OTP[^\\d]+([0-9]+).*") // transactionSigning, regexp
				}, "SG")); // country

		addBank(new Bank(-1, "AIA Singapore", 600, // id, bankName, expiry
				new String[] { "+6598174779" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Password[ is]+([0-9]+).*") // transactionSigning, regexp
				}, "SG")); // country

		addBank(new Bank(-1, "CIMB Bank", -1, // id, bankName, expiry
				new String[] { "CIMBBank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* OTP [^\\.]+is ([0-9]+).*") // transactionSigning, regexp
				}, "SG")); // country

		addBank(new Bank(-1, "Citibank", 120, // id, bankName, expiry
				new String[] { "Citi" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* PIN[is ]+([0-9]+).*") // transactionSigning, regexp
				}, "SG")); // country

		addBank(new Bank(-1, "DBS Bank", 300, // id, bankName, expiry
				new String[] { "DBS Bank", "DBS Cards" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* [pP]assword[^:]*[: ]+[0-9a-zA-Z]+-([0-9]+).*"), // transactionSigning, regexp
						new Expression(false, ".*OTP is [0-9a-zA-Z]+-([0-9]+).*") // transactionSigning, regexp
				}, "SG")); // country

		addBank(new Bank(-1, "Great Eastern", 180, // id, bankName, expiry
				new String[] { "Grt Eastern" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*[pP]assword[^\\d]+([0-9]+).*") // transactionSigning, regexp
				}, "SG")); // country

		addBank(new Bank(-1, "Maybank", 120, // id, bankName, expiry
				new String[] { "+6591825012", "MaybankSG" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Password.* is ([0-9]+),.*") // transactionSigning, regexp
				}, "SG")); // country

		addBank(new Bank(-1, "OCBC Bank", 600, // id, bankName, expiry
				new String[] { "OCBC BANK" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Password is ([0-9]+).*") // transactionSigning, regexp
				}, "SG")); // country

		addBank(new Bank(-1, "SingTel", -1, // id, bankName, expiry
				new String[] { "My SingTel" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*password is ([0-9]+).*"),// transactionSigning, regexp
				}, "SG")); // country

		addBank(new Bank(-1, "Standard Chartered", 100, // id, bankName, expiry
				new String[] { "StanChart", "73702" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*[ -]P[iI][nN][ (].* is ([0-9]+).*"),// transactionSigning, regexp
						new Expression(false, ".*ALP is ([0-9]+).*") // transactionSigning, regexp
				}, "SG")); // country

		addBank(new Bank(-1, "UOB", 180, // id, bankName, expiry
				new String[] { "UOB" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* SMS-OTP is [a-zA-Z0-9]+-([0-9]+) .*") // transactionSigning, regexp
				}, "SG")); // country

	}
}
