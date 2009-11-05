package bankdroid.soda.test;

import junit.framework.TestCase;
import bankdroid.soda.BankManager;

public class BankTest extends TestCase
{
	/*
		@SuppressWarnings( "deprecation" )
		public void testIsBankPhoneNumber()
		{
			Bank bank = null;

			bank = BankManager.findByPhoneNumber("+3646509112");
			assertNull("Failed to realize invalid phonenumber.", bank);

			//Raiffeisen
			bank = BankManager.findByPhoneNumber("+36707060660");
			assertTrue("Failed to realize Raiffeisen phonenumber.", "Raiffeisen Bank".equals(bank.getName()));

			//ERSTE
			bank = BankManager.findByPhoneNumber("+36303444481");
			assertTrue("Failed to realize ERSTE phonenumber.", "ERSTE".equals(bank.getName()));

			//KHB
			bank = BankManager.findByPhoneNumber("+36209000703");
			assertTrue("Failed to realize KHB phonenumber.", "KHB".equals(bank.getName()));

			//OTP
			bank = BankManager.findByPhoneNumber("+36309400700");
			assertTrue("Failed to realize OTP phonenumber.", "OTP".equals(bank.getName()));
			bank = BankManager.findByPhoneNumber("+36209400700");
			assertTrue("Failed to realize OTP phonenumber.", "OTP".equals(bank.getName()));

			//Unicredit
			bank = BankManager.findByPhoneNumber("+36303444504");
			assertTrue("Failed to realize Unicredit phonenumber.", "Unicredit".equals(bank.getName()));

			//MKB
			bank = BankManager.findByPhoneNumber("+36707060652");
			assertTrue("Failed to realize MKB phonenumber.", "MKB".equals(bank.getName()));
			bank = BankManager.findByPhoneNumber("+36209000652");
			assertTrue("Failed to realize MKB phonenumber.", "MKB".equals(bank.getName()));

			//Allianz
			bank = BankManager.findByPhoneNumber("+36303444664");
			assertTrue("Failed to realize Allianz phonenumber.", "Allianz".equals(bank.getName()));

			//Citibank
			bank = BankManager.findByPhoneNumber("+36303444455");
			assertTrue("Failed to realize Citibank phonenumber.", "Citibank".equals(bank.getName()));

			//FHB
			bank = BankManager.findByPhoneNumber("+36303444043");
			assertTrue("Failed to realize FHB phonenumber.", "FHB".equals(bank.getName()));

			//BudapestBank
			bank = BankManager.findByPhoneNumber("+36309266245");
			assertTrue("Failed to realize BudapestBank phonenumber.", "Budapest Bank".equals(bank.getName()));

		}

		@SuppressWarnings( "deprecation" )
		public void testGetCode()
		{
			Bank bank = null;

			//Raiffeisen Bank
			bank = BankManager.findByPhoneNumber("+36707060660");
			assertTrue("76037367".equals(bank
					.extractCode("Az Ön Raiffeisen DirektNet egyszer használatos jelszava: 76037367 Raiffeisen Bank Zrt.")));
			assertNull(bank
					.extractCode("Erste Reggeli egyenleg: Idapont: 2009.09.07. 06.51 Szamla: 00000000-12345678 Egyenleg: 123.456 HUF"));

			//KHB
			bank = BankManager.findByPhoneNumber("+36209000703");
			assertTrue("hNmKmp"
					.equals(bank
							.extractCode("**K&H e-bank** Az Ön egy bejelentkezés időtartalmára vonatkozó sms jelszava: hNmKmp A jelszó maximum 30 percen belül érvényes.")));
			assertNull(bank
					.extractCode("Erste Reggeli egyenleg: Idapont: 2009.09.07. 06.51 Szamla: 00000000-12345678 Egyenleg: 123.456 HUF"));

			//ERSTE
			bank = BankManager.findByPhoneNumber("+36303444481");
			assertTrue("45087768".equals(bank.extractCode("Az an ERSTE NetBank belapasi kadja: 45087768")));
			assertNull(bank
					.extractCode("Erste Reggeli egyenleg: Idapont: 2009.09.07. 06.51 Szamla: 00000000-12345678 Egyenleg: 123.456 HUF"));

			//OTP
			bank = BankManager.findByPhoneNumber("+36309400700");
			assertTrue("90120437"
					.equals(bank
							.extractCode("OTPdirekt - Belfaldi forint atutalas xxx szamlan yyy HUF asszeggel zzz szamlara. Azonosata: 90120437 Javahagyas 23:55-ig.")));
			assertTrue("84165595"
					.equals(bank
							.extractCode("OTPdirekt - Lekatatt betat feltarase xxx szamlan yyy asszeggel. Azonosata: 84165595 Javahagyas 23:51-ig.")));
			assertNull(bank
					.extractCode("Erste Reggeli egyenleg: Idapont: 2009.09.07. 06.51 Szamla: 00000000-12345678 Egyenleg: 123.456 HUF"));

			//Unicredit
			bank = BankManager.findByPhoneNumber("+36303444504");
			assertTrue("2HWNVRNJ".equals(bank.extractCode("Az an kezdeti SpectraNet bejelentkezasi jelszava: 2HWNVRNJ")));
			assertTrue("000-912 089".equals(bank.extractCode("SpectraNet tranzakcias kad: 000-912 089")));
			assertNull(bank
					.extractCode("Erste Reggeli egyenleg: Idapont: 2009.09.07. 06.51 Szamla: 00000000-12345678 Egyenleg: 123.456 HUF"));

			//MKB
			bank = BankManager.findByPhoneNumber("+36209000652");
			assertTrue("g985P"
					.equals(bank
							.extractCode("MKB NetBANKar Forint atutalas ragzatase. Kedvezmanyezett 103000021231313213131, asszeg: 10000 HUF. Alaara jelsza: g985P")));
			assertNull(bank
					.extractCode("Erste Reggeli egyenleg: Idapont: 2009.09.07. 06.51 Szamla: 00000000-12345678 Egyenleg: 123.456 HUF"));

			//Allianz
			bank = BankManager.findByPhoneNumber("+36303444664");
			assertTrue("74716681"
					.equals(bank
							.extractCode("Az an egyszer hasznalatos jelszava: 74716681. Karjak, ezt a jelszat alkalmazza a tranzakciakhoz as a madsatasokhoz a Netbank hasznalata soran!")));
			assertNull(bank
					.extractCode("Erste Reggeli egyenleg: Idapont: 2009.09.07. 06.51 Szamla: 00000000-12345678 Egyenleg: 123.456 HUF"));

			//FHB
			bank = BankManager.findByPhoneNumber("+36303444043");
			assertTrue("84-591727"
					.equals(bank
							.extractCode("Tisztelt agyfelank ! Az an altal indatott tranzakciahoz tartoza egyszer hasznalhata jelszava: 84-591727. FHB Zrt.")));
			assertNull(bank
					.extractCode("Sikeres bejelentkezas - FHB NetB@nk. Felhasznalonev: Minta Janos. Idopont: 2009.08.24 14:30"));

			//Citibank
			bank = BankManager.findByPhoneNumber("+36303444455");
			assertTrue("633831"
					.equals(bank
							.extractCode("OAC - Online Aktivalasi kod: 633831. Kartyaszam: XX3013; Kedvezmenyezett: LUDNYI ZOLTN2 www.citibank.hu Tel: +3612888888")));
			assertNull(bank
					.extractCode("Sikeres bejelentkezas - FHB NetB@nk. Felhasznalonev: Minta Janos. Idopont: 2009.08.24 14:30"));

			//BudapestBank
			bank = BankManager.findByPhoneNumber("+36309266245");
			assertTrue("51930398"
					.equals(bank
							.extractCode("Az an ideiglenes kadja: 51930398 Ez a kad belapashez 13:47:58-ig hasznalhatja, de arizze meg a tranzakciahoz! Kapcsolat azonosata: 133758 Budapest Bank")));
			assertNull(bank
					.extractCode("Sikeres bejelentkezas - FHB NetB@nk. Felhasznalonev: Minta Janos. Idopont: 2009.08.24 14:30"));

		}
	*/
	public void testEscape()
	{
		final String escaped = BankManager.escapeStrings(new String[] { "\"escape\"char\"", "coma,", ",after\"",
				"normal", "", "afterEmpty", "" });

		assertEquals("\"\"\"escape\"\"char\"\"\",\"coma,\",\",after\"\"\",normal,,afterEmpty,", escaped);
	}

	public void testUnescape()
	{
		String[] unescaped = BankManager
				.unescapeStrings("\"\"\"escape\"\"char\"\"\",\"coma,\",\",after\"\"\",normal,,afterEmpty,kaka");
		//"""escape""char""","coma,",",after""",normal,,afterEmpty,,
		String[] expected = new String[] { "\"escape\"char\"", "coma,", ",after\"", "normal", "", "afterEmpty", "kaka" };

		assertEquals("With normal ending", expected.length, unescaped.length);
		for ( int i = 0; i < expected.length; i++ )
		{
			assertEquals(expected[i], unescaped[i]);
		}

		unescaped = BankManager
				.unescapeStrings("\"\"\"escape\"\"char\"\"\",\"coma,\",\",after\"\"\",normal,,afterEmpty,");
		//"""escape""char""","coma,",",after""",normal,,afterEmpty,,
		expected = new String[] { "\"escape\"char\"", "coma,", ",after\"", "normal", "", "afterEmpty", "" };

		assertEquals("With empty ending", expected.length, unescaped.length);
		for ( int i = 0; i < expected.length; i++ )
		{
			assertEquals(expected[i], unescaped[i]);
		}

	}

}
