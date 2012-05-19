package bankdroid.smskey.bank;

class Banks_DE extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, ".comdirect", -1, // id, bankName, expiry
				new String[] { "comdirect" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN.* lautet[: ]+([0-9a-zA-Z]+).*"), // transactionSigning, regexp
				}, "DE")); // country

		addBank(new Bank(-1, "Deutsche Bank", -1, // id, bankName, expiry
				new String[] { "DB Mobile" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN.* lautet[: ]+([0-9a-zA-Z]+).*"), // transactionSigning, regexp
				}, "DE")); // country

		addBank(new Bank(-1, "Fidor Bank", -1, // id, bankName, expiry
				new String[] { "FidorBankAG" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*TAN[: ]+([0-9a-zA-Z]+).*"), // transactionSigning, regexp
						new Expression(false, ".*mTan[: ]+([0-9a-zA-Z]+).*") // transactionSigning, regexp
				}, "DE")); // country

		addBank(new Bank(-1, "HypoVereinsbank", -1, // id, bankName, expiry
				new String[] { "HVB" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN.* lautet[: ]+([0-9a-zA-Z]+).*") // transactionSigning, regexp
				}, "DE")); // country

		addBank(new Bank(-1, "ING-DiBa", -1, // id, bankName, expiry
				new String[] { "Ihre Bank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*TAN.* Uhr[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "DE")); // country

		addBank(new Bank(-1, "Postbank", -1, // id, bankName, expiry
				new String[] { "Postbank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN.* lautet[: ]+([0-9a-zA-Z]+).*") // transactionSigning, regexp
				}, "DE")); // country

		addBank(new Bank(-1, "Sparkasse", -1, // id, bankName, expiry
				new String[] { "Saalespk", "smsTAN", "SMSBANKING", "SPK_SPN", "SparkasseSW" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN.* lautet[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "DE")); // country

		addBank(new Bank(-1, "Sparkasse Darmstadt", -1, // id, bankName, expiry
				new String[] { "Sparkasse" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN.* lautet[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "DE")); // country

		addBank(new Bank(-1, "Sparkasse Hannover", -1, // id, bankName, expiry
				new String[] { "SpkHannover" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN.* lautet ([0-9]*).*") // transactionSigning, regexp
				}, "DE")); // country

		addBank(new Bank(-1, "VR Bank Rhein-Neckar", -1, // id, bankName, expiry
				new String[] { "VR Bank RN", "VR-Bank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*TAN.* lautet[ :]+([0-9]+).*") // transactionSigning, regexp
				}, "DE")); // country

	}
}
