package bankdroid.soda.test;

import junit.framework.TestCase;
import bankdroid.soda.Bank;

public class BankTest extends TestCase
{

	public void testIsBankPhoneNumber()
	{
		Bank bank = null;

		bank = Bank.findByPhoneNumber("+3646509112");
		assertNull("Failed to realize invalid phonenumber.", bank);

		//ERSTE
		bank = Bank.findByPhoneNumber("+36303444481");
		assertTrue("Failed to realize ERSTE phonenumber.", "ERSTE".equals(bank.getId()));

		//KHB
		bank = Bank.findByPhoneNumber("+36209000703");
		assertTrue("Failed to realize KHB phonenumber.", "KHB".equals(bank.getId()));

		//OTP
		bank = Bank.findByPhoneNumber("+36309400700");
		assertTrue("Failed to realize OTP phonenumber.", "OTP".equals(bank.getId()));
		bank = Bank.findByPhoneNumber("+36209400700");
		assertTrue("Failed to realize OTP phonenumber.", "OTP".equals(bank.getId()));

		//Unicredit
		bank = Bank.findByPhoneNumber("+36303444504");
		assertTrue("Failed to realize Unicredit phonenumber.", "Unicredit".equals(bank.getId()));

		//MKB
		bank = Bank.findByPhoneNumber("+36707060652");
		assertTrue("Failed to realize MKB phonenumber.", "MKB".equals(bank.getId()));
		bank = Bank.findByPhoneNumber("+36209000652");
		assertTrue("Failed to realize MKB phonenumber.", "MKB".equals(bank.getId()));

		//Allianz
		bank = Bank.findByPhoneNumber("+36303444664");
		assertTrue("Failed to realize Allianz phonenumber.", "Allianz".equals(bank.getId()));

		//Citibank
		bank = Bank.findByPhoneNumber("+36303444455");
		assertTrue("Failed to realize Citibank phonenumber.", "Citibank".equals(bank.getId()));

		//FHB
		bank = Bank.findByPhoneNumber("+36303444043");
		assertTrue("Failed to realize FHB phonenumber.", "FHB".equals(bank.getId()));

		//BudapestBank
		bank = Bank.findByPhoneNumber("+36309266245");
		assertTrue("Failed to realize BudapestBank phonenumber.", "BudapestBank".equals(bank.getId()));

	}

	public void testGetCode()
	{
		Bank bank = null;

		//KHB
		bank = Bank.findByPhoneNumber("+36209000703");
		assertTrue("dDqgap"
				.equals(bank
						.getCode("**K&H MOBILINFO** Az ön egyszer használatos jelszava: dDqgap A jelszó egy bejelentkezés idötartalmára, de maximum 3 órán belül érvényes.")));
		assertNull(bank
				.getCode("Erste Reggeli egyenleg: Idöpont: 2009.09.07. 06.51 Számla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//ERSTE
		bank = Bank.findByPhoneNumber("+36303444481");
		assertTrue("45087768".equals(bank.getCode("Az Ön ERSTE NetBank belépési kódja: 45087768")));
		assertNull(bank
				.getCode("Erste Reggeli egyenleg: Idöpont: 2009.09.07. 06.51 Számla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//OTP
		bank = Bank.findByPhoneNumber("+36309400700");
		assertTrue("90120437"
				.equals(bank
						.getCode("OTPdirekt - Belföldi forint átutalás xxx számlán yyy HUF összeggel zzz számlára. Azonosító: 90120437 Jóváhagyás 23:55-ig.")));
		assertTrue("84165595"
				.equals(bank
						.getCode("OTPdirekt - Lekötött betét feltörése xxx számlán yyy összeggel. Azonosító: 84165595 Jóváhagyás 23:51-ig.")));
		assertNull(bank
				.getCode("Erste Reggeli egyenleg: Idöpont: 2009.09.07. 06.51 Számla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//Unicredit
		bank = Bank.findByPhoneNumber("+36303444504");
		assertTrue("2HWNVRNJ".equals(bank.getCode("Az ön kezdeti SpectraNet bejelentkezési jelszava: 2HWNVRNJ")));
		assertTrue("000-912 089".equals(bank.getCode("SpectraNet tranzakciós kód: 000-912 089")));
		assertNull(bank
				.getCode("Erste Reggeli egyenleg: Idöpont: 2009.09.07. 06.51 Számla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//MKB
		bank = Bank.findByPhoneNumber("+36209000652");
		assertTrue("g985P"
				.equals(bank
						.getCode("MKB NetBANKár Forint átutalás rögzítése. Kedvezményezett 103000021231313213131, Összeg: 10000 HUF. Aláíró jelszó: g985P")));
		assertNull(bank
				.getCode("Erste Reggeli egyenleg: Idöpont: 2009.09.07. 06.51 Számla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//Allianz
		bank = Bank.findByPhoneNumber("+36303444664");
		assertTrue("74716681"
				.equals(bank
						.getCode("Az Ön egyszer használatos jelszava: 74716681. Kérjük, ezt a jelszót alkalmazza a tranzakciókhoz és a módsításokhoz a Netbank használata során!")));
		assertNull(bank
				.getCode("Erste Reggeli egyenleg: Idöpont: 2009.09.07. 06.51 Számla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//FHB
		bank = Bank.findByPhoneNumber("+36303444043");
		assertTrue("84-591727"
				.equals(bank
						.getCode("Tisztelt Ügyfelünk ! Az Ön által indított tranzakcióhoz tartozó egyszer használható jelszava: 84-591727. FHB Zrt.")));
		assertNull(bank
				.getCode("Sikeres bejelentkezés - FHB NetB@nk. Felhasznalonev: Minta Janos. Idopont: 2009.08.24 14:30"));

		//Citibank
		bank = Bank.findByPhoneNumber("+36303444455");
		assertTrue("633831"
				.equals(bank
						.getCode("OAC - Online Aktivalasi kod: 633831. Kartyaszam: XX3013; Kedvezmenyezett: LUDNYI ZOLTN2 www.citibank.hu Tel: +3612888888")));
		assertNull(bank
				.getCode("Sikeres bejelentkezés - FHB NetB@nk. Felhasznalonev: Minta Janos. Idopont: 2009.08.24 14:30"));

		//BudapestBank
		bank = Bank.findByPhoneNumber("+36309266245");
		assertTrue("51930398"
				.equals(bank
						.getCode("Az Ön ideiglenes kódja: 51930398 Ez a kód belépéshez 13:47:58-ig használhatja, de örizze meg a tranzakcióhoz! Kapcsolat azonosító: 133758 Budapest Bank")));
		assertNull(bank
				.getCode("Sikeres bejelentkezés - FHB NetB@nk. Felhasznalonev: Minta Janos. Idopont: 2009.08.24 14:30"));

	}

}
