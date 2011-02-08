package bankdroid.soda.bank.test;


public class BankTest extends BankTestBase
{
	public void testBasics() throws Exception
	{
		//country HU
		testBank("AXA Bank", "AXA BANK");
		testBank("Allianz Bank", "+36303444664");
		testBank("Budapest Bank", "+36309266245");
		testBank("Citibank", "+36303444455");
		testBank("ERSTE Bank", "+36303444481");
		testBank("ERSTE Bank", "0036303444481");
		testBank("ERSTE Bank", "+36707060177");
		testBank("FHB Bank", "+36303444043");
		testBank("K&H Bank", "+36209000703");
		testBank("K&H Bank", "06709000542");
		testBank("K&H Bank", "+36709000542");
		testBank("MKB Bank", "+36707060652");
		testBank("MKB Bank", "+36209000652");
		testBank("MKB Bank", "5833");
		testBank("OTP Bank", "+36309400700");
		testBank("OTP Bank", "+36209400700");
		testBank("OTP Bank", "06709400700");
		testBank("Raiffeisen Bank", "+36707060660");
		testBank("Raiffeisen Bank", "+36209000848");
		testBank("Raiffeisen Bank", "+36303444540");
		testBank("Takarékszövetkezet", "+36709000666");
		testBank("Unicredit Bank", "+36303444504");
		testBank("Unicredit Bank", "36303444504");
		testBank("Vodafone", "Vodafone");

		//country RU
		testBank("Alfa Bank", "Alfa-Bank");
		testBank("Platezh.RU", "Platezh.RU");
		testBank("Sberbank", "900");
		testBank("Sberbank", "+79262000900");
		testBank("Sberbank", "+79165723900");
		testBank("Svyaznoy Bank", "SvyaznoyBnk");

		//country PL
		testBank("Alior Bank", "Alior Bank");
		testBank("BlueCash", "+48790569575");
		testBank("Citibank", "226922484");
		testBank("Getin Online Bank", "GETINONLINE");
		testBank("Idea Bank", "IDEA BANK");
		testBank("MultiBank", "3003");
		testBank("Open Finance", "OPEN");
		testBank("Orange", "Kod Orange");
		testBank("Play Mobile", "+6670");
		testBank("Polbank EFG", "PolbankEFG");
		testBank("Tax Care S.A.", "TAXCARE");
		testBank("iBOA/mBOA (ERA GSM)", "+48602909");
		testBank("iBOA/mBOA (ERA GSM)", "mBOA");
		testBank("mBank", "3388");
		testBank("mBank", "Operacja");

		//country TR
		testBank("Bank Asya", ".BANKASYA.");
		testBank("Yapi Kredi", "AKILLISMS");
		testBank("Yapi Kredi", "4410");

		//country AT
		testBank("Bank Austria", "+435050526101");
		testBank("PSK Bank", "PSK");

		//country US
		testBank("Bank of America", "73981");

		//country SG
		testBank("Citibank", "Citi");

		//country CH
		testBank("Credit Suisse", "00000");

		//country CZ
		testBank("GE Money Bank", "GEMB");
		testBank("Raiffeisenbank", "999024");
		testBank("UniCredit Bank", "5200");
		testBank("mBank", "mBank");
		testBank("mBank", "+6011");
		testBank("ČSOB", "39601");
		testBank("ČSOB", "999020");
		testBank("ČSOB", "+421940661750");

		//country UA
		testBank("PrivatBank", "10060");
		testBank("PrivatBank", "privat24.ua");
		testBank("UkrSibBank", "0931777755");

		//country NO
		testBank("Skandiabanken", "+4781001001");

		//country DE
		testBank("Sparkasse Hannover", "SpkHannover");

		//country SK
		testBank("Tatra banka", "+421902022200");
		testBank("VUB banka", "323");
		testBank("mBank", "2265");
		testBank("mBank", "2287");

		//country VN
		testBank("Vietcombank", "+8170");
	}

	public void testPatterns() throws Exception
	{
		testBank("Raiffeisen Bank", "+36707060660",
				"Az Ön Raiffeisen DirektNet egyszer használatos jelszava: 76037367 Raiffeisen Bank Zrt.", "76037367");
	}
}
