package bankdroid.smskey.bank;

class Banks_PL extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "Alior Bank", -1, // id, bankName, expiry
				new String[] { "Alior Bank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* Kod [^:]+[: ]+([0-9]+)") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "BGŻOptima", -1, // id, bankName, expiry
				new String[] { "2010" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* kod [^:]*weryfikacji[^:]*[: ]+([a-fA-F0-9]+).*"), // transactionSigning, regexp
						new Expression(false, ".*wpisujac kod[: ]+([a-fA-F0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "BNP Paribas", -1, // id, bankName, expiry
				new String[] { "1551" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* Kod [^:]*[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "BZ WBK", -1, // id, bankName, expiry
				new String[] { "BZWBK24" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*smsKod: ([0-9]+) .*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Bank BPH", -1, // id, bankName, expiry
				new String[] { "3366" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "Kod .*: *([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Bank BPS", -1, // id, bankName, expiry
				new String[] { "+48661000277" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, "Kod autoryzacyjny ([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Bank Pekao S.A.", -1, // id, bankName, expiry
				new String[] { "PekaoSA" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* Kod [^:]*[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Bank Pocztowy", -1, // id, bankName, expiry
				new String[] { "+48661100500" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* Kod[ :]+([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "BlueCash", -1, // id, bankName, expiry
				new String[] { "+48790569575", "+48662323323" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "BlueCash.* kod .*: ([a-zA-Z0-9]+)") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Citibank", -1, // id, bankName, expiry
				new String[] { "226922484", "Info", "+48797987950", "+44226922484" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*Kod[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Deutche Bank PBC", -1, // id, bankName, expiry
				new String[] { "DB PBC" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*Haslo: ([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Getin Online Bank", -1, // id, bankName, expiry
				new String[] { "GETINONLINE" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*: ([a-zA-Z0-9]+)") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Heyah", -1, // id, bankName, expiry
				new String[] { "Heyah" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* haslo .* to[ ]*([0-9]+).*"), // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "home.pl", -1, // id, bankName, expiry
				new String[] { "home.pl" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*Kod .* to[: ]+([0-9]+)[^\\d].*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "ING Bank", -1, // id, bankName, expiry
				new String[] { "ING" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Kod .* to[: ]+([0-9]+)[^\\d].*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "inkantor", -1, // id, bankName, expiry
				new String[] { "+48792498158" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* numer .* to[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Idea Bank", -1, // id, bankName, expiry
				new String[] { "IDEA BANK" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Haslo[^:]*:[ ]*([0-9a-zA-Z]+)") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Inteligo", -1, // id, bankName, expiry
				new String[] { "Inteligo", "inteligo" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Kod [^:]+: ([0-9]+)") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Kantor", -1, // id, bankName, expiry
				new String[] { "Kantor" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*kod[^:]+[ :]+([0-9a-zA-Z]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Łącki Bank Spółdzielczy", -1, // id, bankName, expiry
				new String[] { "+48501773757" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*haslo[ :]+([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Meritum Bank", -1, // id, bankName, expiry
				new String[] { "MeritumBank" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".*kod .*: ([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Millennium Bank", -1, // id, bankName, expiry
				new String[] { "HasloSMS" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* Haslo[^ ]*[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "MultiBank", -1, // id, bankName, expiry
				new String[] { "3003" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*haslo[: ]+([0-9]+) MultiBank.*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Open Finance", -1, // id, bankName, expiry
				new String[] { "OPEN", "Operacja" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* Haslo[^:]*: ([a-zA-Z0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Orange", -1, // id, bankName, expiry
				new String[] { "Kod Orange" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Orange[^:]+: ([a-z0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "PKO BP", -1, // id, bankName, expiry
				new String[] { "PKOBP", "PKO BP" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Kod [^:]+[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Platnosci.pl", -1, // id, bankName, expiry
				new String[] { "smsKod:" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, "^([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Play Mobile", -1, // id, bankName, expiry
				new String[] { "+6670", "PLAY", "Play.pl" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* haslo .* ([0-9]+)") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Play Music", -1, // id, bankName, expiry
				new String[] { "PLAYMUSIC" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* haslo[: ]+([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Plus Online", -1, // id, bankName, expiry
				new String[] { "Plus", "484857" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Haslo .* ([0-9]+).*"),// transactionSigning, regexp
						new Expression(false, "^([0-9]+)$") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Polbank EFG", 120, // id, bankName, expiry
				new String[] { "PolbankEFG" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "[^:]+ Polbank24: ([0-9]+). *") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Przelew24", -1, // id, bankName, expiry
				new String[] { "2424" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, "smsKod[ :]+([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Raiffeisen Bank", -1, // id, bankName, expiry
				new String[] { "Raiffeisen" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* Haslo: ([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "SkyCash", 1800, // id, bankName, expiry
				new String[] { "+48510510205" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* hasla [^:]*: ([a-zA-Z0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "T-Mobile", -1, // id, bankName, expiry
				new String[] { "+48602909", "mBOA", "iBOA", "T-Mobile", "IBOA", "602900" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* haslo .* to ([0-9]+)"),// transactionSigning, regexp
						new Expression(false, "Kod .* to[ ]*([0-9]+).*"), // transactionSigning, regexp
						new Expression(false, "Haslo [^:]+[: ]+([0-9]+)") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "TakTak", -1, // id, bankName, expiry
				new String[] { "iTakTak" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* haslo .* to ([0-9]+)") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Tax Care S.A.", -1, // id, bankName, expiry
				new String[] { "TAXCARE" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, ".* Haslo: ([0-9a-zA-Z]+)") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "Walutomat", -1, // id, bankName, expiry
				new String[] { "+48530950114", "Walutomat", "+48518328684", "+48796321536" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".* Kod [^:]*: ([0-9]+).*") // transactionSigning, regexp
				}, "PL")); // country

		addBank(new Bank(-1, "mBank", -1, // id, bankName, expiry
				new String[] { "2287", "3388", "Operacja", "mBank:" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(true, ".*haslo: ([0-9]+) mBank.*") // transactionSigning, regexp
				}, "PL")); // country

	}
}
