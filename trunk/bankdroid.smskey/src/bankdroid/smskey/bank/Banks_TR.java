package bankdroid.smskey.bank;

class Banks_TR extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "AvivaSA", -1, // id, bankName, expiry
				new String[] { "AvivaSA A.S" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*CepSifreniz[: ]+([0-9]+)") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "Akbank", -1, // id, bankName, expiry
				new String[] { "CEPSIFRE", ".CEPSIFRE.", "-AKBANK-", "AKBANK." }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*CepSifreniz[: ]+([0-9A-Za-z]+)"), // transactionSigning, regexp
						new Expression(false, ".*SIFRENIZ[: ]+([0-9A-Za-z]+)") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "Bank Asya", -1, // id, bankName, expiry
				new String[] { ".BANKASYA.", ".BANK ASYA.", "BANKASYA" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*Sifreniz: ([0-9]+) .*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "Citibank", -1, // id, bankName, expiry
				new String[] { "CITI.", "Citibank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* [sS]ifreniz[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "DenizBank", -1, // id, bankName, expiry
				new String[] { "Denizbank", "(DenizBank)", "DENIZBANK." }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Sifresi[ :]*([0-9]+).*"),// transactionSigning, regexp
						new Expression(false, "Sifre[ :]+([0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "Finansbank", -1, // id, bankName, expiry
				new String[] { "+903462672265", "FINANSBANK", ".FINANSBANK" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Sifreniz[ :]*([0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "Garanti Bank", -1, // id, bankName, expiry
				new String[] { "-GARANTI-", "GARANTI" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*SIFRENIZ[:;] *([0-9]+)") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "HALKBANK", -1, // id, bankName, expiry
				new String[] { "*HALKBANK*", "**HALKBANK" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* SIFRESI: ([0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "HSBC", -1, // id, bankName, expiry
				new String[] { "HSBC BANK", "HSBC.BANK", "HSBC" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* SIFRENIZ[ :]*([0-9a-zA-Z]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "ING Bank", -1, // id, bankName, expiry
				new String[] { "ING BANK" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Sifreniz[ :]+([0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "Kuveyt Türk", -1, // id, bankName, expiry
				new String[] { "KUVEYT TURK" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* PAROLANIZ[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "Sekerbank", -1, // id, bankName, expiry
				new String[] { "SEKERBANK-" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*SIFRENIZ[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "T.C. Ziraat Bankası", -1, // id, bankName, expiry
				new String[] { "-ZIRAATBANK", "ZIRAATBANK-" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "[^:]*Sifreniz[: ]*([0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "TEB", 180, // id, bankName, expiry
				new String[] { "TEB", "TEB*", "MOBIL SIFRE" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Sifreniz: *([0-9]+).*"),// transactionSigning, regexp
						new Expression(false, ".* Kodunuz[^:]*: *([0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "Türkiye İş Bankası", -1, // id, bankName, expiry
				new String[] { "ISBANK", "IS BANKASI", "ISBANKASI" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "[\"]*([a-zA-Z0-9]+)[\"]* .* Kodunuz.*"),// transactionSigning, regexp
						new Expression(false, ".* sifreniz[: ]+([a-zA-Z0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "VakifBank", 180, // id, bankName, expiry
				new String[] { "VAKIFBANK.", "VAKIFBANK" }, // phoneNumbers
				new Expression[] {// expressions 
				new Expression(false, ".* sifreniz: *([0-9]+).*"),// transactionSigning, regexp
						new Expression(false, ".* Kodunuz: ([a-zA-Z0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "Vodafone", -1, // id, bankName, expiry
				new String[] { "7048" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "[^:]*SIFRENIZ[: ]*([0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

		addBank(new Bank(-1, "Yapi Kredi", -1, // id, bankName, expiry
				new String[] { "AKILLISMS", "4410" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* sifre[nsiz]+[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "TR")); // country

	}
}
