package bankdroid.smskey.bank;

class Banks_CH extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "Aargauische Kantonalbank", -1, // id, bankName, expiry
				new String[] { "+41797892290", "+41628357799" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*SMS-Code[^:]+[: ]+([A-Za-z0-9]+).*") // transactionSigning, regexp
				}, "CH")); // country

		addBank(new Bank(-1, "Cornèrcard", -1, // id, bankName, expiry
				new String[] { "30403", "+41763332646", "41763332646" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Code[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "CH")); // country

		addBank(new Bank(-1, "Credit Suisse", -1, // id, bankName, expiry
				new String[] { "00000" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* CODE: ([0-9]+)") // transactionSigning, regexp
				}, "CH")); // country

		addBank(new Bank(-1, "Raiffeisen", -1, // id, bankName, expiry
				new String[] { "8008" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*Passwortzusatz ([a-zA-Z0-9]+).*") // transactionSigning, regexp
				}, "CH")); // country

		addBank(new Bank(-1, "Zürcher Kantonalbank", -1, // id, bankName, expiry
				new String[] { "ZKB Online", "0041798070110" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* mTAN[^:]+[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "CH")); // country

	}
}
