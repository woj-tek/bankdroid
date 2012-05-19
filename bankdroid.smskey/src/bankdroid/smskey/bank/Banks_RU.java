package bankdroid.smskey.bank;

class Banks_RU extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "ASTRAL", -1, // id, bankName, expiry
				new String[] { "ASTRAL" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "^([a-zA-Z0-9]+)$"),// transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "Alfa Bank", -1, // id, bankName, expiry
				new String[] { "Alfa-Bank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*Alfa-Click - ([0-9a-zA-Z]+)[\\. ]*.*"),// transactionSigning, regexp
						new Expression(true, ".* [pP]arol[-: ]+([0-9a-zA-Z]+).*") // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "Агропромбанк", -1, // id, bankName, expiry
				new String[] { "1660" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*Код[ :]+([0-9]+)"),// transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "Bank of Moscow", -1, // id, bankName, expiry
				new String[] { "8000" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* [pP]arol[' ]+([0-9]+).*") // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "Восточный экспресс банк", -1, // id, bankName, expiry
				new String[] { "VOST ALERT" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "[pP]arol[^:]+[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "Citibank", -1, // id, bankName, expiry
				new String[] { "Citibank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*Parol[': ]+([0-9]+).*") // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "Faktura.ru", -1, // id, bankName, expiry
				new String[] { "6470" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*пароль [^\\d]+ ([0-9]+) .*"), // transactionSigning, regexp
						new Expression(true, "Пароль ([0-9]+) .*"), // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "HandyBank", -1, // id, bankName, expiry
				new String[] { "HandyBank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*код[: ]+([0-9]+).*"), // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "Platezh.RU", -1, // id, bankName, expiry
				new String[] { "Platezh.RU" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "SMS-code: ([0-9a-zA-Z]+)") // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "QIWI Wallet", -1, // id, bankName, expiry
				new String[] { "QIWI Wallet" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, "[^:]*kod[: ]+([0-9]+)") // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "Raiffeisenbank", -1, // id, bankName, expiry
				new String[] { "Raiffeisen" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Kod [^']+ '([0-9]+)'.*") // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "rsb.ru", -1, // id, bankName, expiry
				new String[] { "rsb.ru" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*[Кк]од[^-]+[- ]+([0-9]+).*") // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "Sberbank", -1, // id, bankName, expiry
				new String[] { "900", "+79262000900", "+79165723900" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*[pP]arol[: ]+([0-9]+).*"),// transactionSigning, regexp
						new Expression(false, ".*[pP]arol[^-]+[- ]+([0-9]+).*"),// transactionSigning, regexp
						new Expression(false, "^[a-zA-Z :]*([0-9]+)[ -]+parol.*"), // transactionSigning, regexp
						new Expression(false, ".*пароль[ :]+([0-9]+).*"), // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "SBank", -1, // id, bankName, expiry
				new String[] { "SBank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "Code[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "Svyaznoy Bank", -1, // id, bankName, expiry
				new String[] { "SvyaznoyBnk" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*[pP]arol[^:]*: *([0-9]+).*"), // transactionSigning, regexp
						new Expression(false, "[^ ]+ ([0-9]+) .*") // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "TCS Bank", -1, // id, bankName, expiry
				new String[] { "TCS Bank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*пароль[: ]+([A-Za-z0-9]+).*") // transactionSigning, regexp
				}, "RU")); // country

		addBank(new Bank(-1, "WebMoney", -1, // id, bankName, expiry
				new String[] { "WebMoney" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "Код[^:]*[: ]+([0-9]+).*"), // transactionSigning, regexp
						new Expression(false, ".*KOD[: ]+([0-9]+).*"), // transactionSigning, regexp
						new Expression(false, "Chislo-otvet[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "RU")); // country
	}
}
