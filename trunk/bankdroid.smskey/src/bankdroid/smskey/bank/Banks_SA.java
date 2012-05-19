package bankdroid.smskey.bank;

class Banks_SA extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "Al Rajhi Bank", -1, // id, bankName, expiry
				new String[] { "AlRajhiBank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*([0-9]{4}).*") // transactionSigning, regexp
				}, "SA")); // country

		addBank(new Bank(-1, "Al Inma Bank", -1, // id, bankName, expiry
				new String[] { "alinma bank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*([0-9]{4}).*") // transactionSigning, regexp
				}, "SA")); // country

		addBank(new Bank(-1, "ANB", -1, // id, bankName, expiry
				new String[] { "ANB" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*([0-9]{4}).*") // transactionSigning, regexp
				}, "SA")); // country

		addBank(new Bank(-1, "Bank Albilad", -1, // id, bankName, expiry
				new String[] { "BankAlbilad" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*([0-9]{5}).*") // transactionSigning, regexp
				}, "SA")); // country

		addBank(new Bank(-1, "Banque Saudi Fransi", -1, // id, bankName, expiry
				new String[] { "FransiPhone", "FransiFlash" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* ([0-9]{6}) .*"),// transactionSigning, regexp
						new Expression(false, ".* activation code .* ([0-9]{6}).*") // transactionSigning, regexp
				}, "SA")); // country

		addBank(new Bank(-1, "NCB", -1, // id, bankName, expiry
				new String[] { "AlahliSMS" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*([0-9]{6}).*"),// transactionSigning, regexp
				}, "SA")); // country

		addBank(new Bank(-1, "Riyad Bank", -1, // id, bankName, expiry
				new String[] { "RiyadBank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* code .* Logon[^:]+[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "SA")); // country

		addBank(new Bank(-1, "SABB", -1, // id, bankName, expiry
				new String[] { "SABB" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* ([0-9]{6}) .*") // transactionSigning, regexp
				}, "SA")); // country

		addBank(new Bank(-1, "Samba", -1, // id, bankName, expiry
				new String[] { "Samba" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*([0-9]{6}).*") // transactionSigning, regexp
				}, "SA")); // country

	}
}
