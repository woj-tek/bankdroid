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

		//Raiffeisen
		bank = Bank.findByPhoneNumber("+36707060660");
		assertTrue("Failed to realize Raiffeisen phonenumber.", "Raiffeisen Bank".equals(bank.getName()));

		//ERSTE
		bank = Bank.findByPhoneNumber("+36303444481");
		assertTrue("Failed to realize ERSTE phonenumber.", "ERSTE".equals(bank.getName()));

		//KHB
		bank = Bank.findByPhoneNumber("+36209000703");
		assertTrue("Failed to realize KHB phonenumber.", "KHB".equals(bank.getName()));

		//OTP
		bank = Bank.findByPhoneNumber("+36309400700");
		assertTrue("Failed to realize OTP phonenumber.", "OTP".equals(bank.getName()));
		bank = Bank.findByPhoneNumber("+36209400700");
		assertTrue("Failed to realize OTP phonenumber.", "OTP".equals(bank.getName()));

		//Unicredit
		bank = Bank.findByPhoneNumber("+36303444504");
		assertTrue("Failed to realize Unicredit phonenumber.", "Unicredit".equals(bank.getName()));

		//MKB
		bank = Bank.findByPhoneNumber("+36707060652");
		assertTrue("Failed to realize MKB phonenumber.", "MKB".equals(bank.getName()));
		bank = Bank.findByPhoneNumber("+36209000652");
		assertTrue("Failed to realize MKB phonenumber.", "MKB".equals(bank.getName()));

		//Allianz
		bank = Bank.findByPhoneNumber("+36303444664");
		assertTrue("Failed to realize Allianz phonenumber.", "Allianz".equals(bank.getName()));

		//Citibank
		bank = Bank.findByPhoneNumber("+36303444455");
		assertTrue("Failed to realize Citibank phonenumber.", "Citibank".equals(bank.getName()));

		//FHB
		bank = Bank.findByPhoneNumber("+36303444043");
		assertTrue("Failed to realize FHB phonenumber.", "FHB".equals(bank.getName()));

		//BudapestBank
		bank = Bank.findByPhoneNumber("+36309266245");
		assertTrue("Failed to realize BudapestBank phonenumber.", "Budapest Bank".equals(bank.getName()));

	}

	public void testGetCode()
	{
		Bank bank = null;

		//Raiffeisen Bank
		bank = Bank.findByPhoneNumber("+36707060660");
		assertTrue("76037367".equals(bank
				.extractCode("Az Ön Raiffeisen DirektNet egyszer használatos jelszava: 76037367 Raiffeisen Bank Zrt.")));
		assertNull(bank
				.extractCode("Erste Reggeli egyenleg: Id�pont: 2009.09.07. 06.51 Sz�mla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//KHB
		bank = Bank.findByPhoneNumber("+36209000703");
		assertTrue("hNmKmp"
				.equals(bank
						.extractCode("**K&H e-bank** Az Ön egy bejelentkezés időtartalmára vonatkozó sms jelszava: hNmKmp A jelszó maximum 30 percen belül érvényes.")));
		assertNull(bank
				.extractCode("Erste Reggeli egyenleg: Id�pont: 2009.09.07. 06.51 Sz�mla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//ERSTE
		bank = Bank.findByPhoneNumber("+36303444481");
		assertTrue("45087768".equals(bank.extractCode("Az �n ERSTE NetBank bel�p�si k�dja: 45087768")));
		assertNull(bank
				.extractCode("Erste Reggeli egyenleg: Id�pont: 2009.09.07. 06.51 Sz�mla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//OTP
		bank = Bank.findByPhoneNumber("+36309400700");
		assertTrue("90120437"
				.equals(bank
						.extractCode("OTPdirekt - Belf�ldi forint �tutal�s xxx sz�ml�n yyy HUF �sszeggel zzz sz�ml�ra. Azonos�t�: 90120437 J�v�hagy�s 23:55-ig.")));
		assertTrue("84165595"
				.equals(bank
						.extractCode("OTPdirekt - Lek�t�tt bet�t felt�r�se xxx sz�ml�n yyy �sszeggel. Azonos�t�: 84165595 J�v�hagy�s 23:51-ig.")));
		assertNull(bank
				.extractCode("Erste Reggeli egyenleg: Id�pont: 2009.09.07. 06.51 Sz�mla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//Unicredit
		bank = Bank.findByPhoneNumber("+36303444504");
		assertTrue("2HWNVRNJ".equals(bank.extractCode("Az �n kezdeti SpectraNet bejelentkez�si jelszava: 2HWNVRNJ")));
		assertTrue("000-912 089".equals(bank.extractCode("SpectraNet tranzakci�s k�d: 000-912 089")));
		assertNull(bank
				.extractCode("Erste Reggeli egyenleg: Id�pont: 2009.09.07. 06.51 Sz�mla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//MKB
		bank = Bank.findByPhoneNumber("+36209000652");
		assertTrue("g985P"
				.equals(bank
						.extractCode("MKB NetBANK�r Forint �tutal�s r�gz�t�se. Kedvezm�nyezett 103000021231313213131, �sszeg: 10000 HUF. Al��r� jelsz�: g985P")));
		assertNull(bank
				.extractCode("Erste Reggeli egyenleg: Id�pont: 2009.09.07. 06.51 Sz�mla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//Allianz
		bank = Bank.findByPhoneNumber("+36303444664");
		assertTrue("74716681"
				.equals(bank
						.extractCode("Az �n egyszer haszn�latos jelszava: 74716681. K�rj�k, ezt a jelsz�t alkalmazza a tranzakci�khoz �s a m�ds�t�sokhoz a Netbank haszn�lata sor�n!")));
		assertNull(bank
				.extractCode("Erste Reggeli egyenleg: Id�pont: 2009.09.07. 06.51 Sz�mla: 00000000-12345678 Egyenleg: 123.456 HUF"));

		//FHB
		bank = Bank.findByPhoneNumber("+36303444043");
		assertTrue("84-591727"
				.equals(bank
						.extractCode("Tisztelt �gyfel�nk ! Az �n �ltal ind�tott tranzakci�hoz tartoz� egyszer haszn�lhat� jelszava: 84-591727. FHB Zrt.")));
		assertNull(bank
				.extractCode("Sikeres bejelentkez�s - FHB NetB@nk. Felhasznalonev: Minta Janos. Idopont: 2009.08.24 14:30"));

		//Citibank
		bank = Bank.findByPhoneNumber("+36303444455");
		assertTrue("633831"
				.equals(bank
						.extractCode("OAC - Online Aktivalasi kod: 633831. Kartyaszam: XX3013; Kedvezmenyezett: LUDNYI ZOLTN2 www.citibank.hu Tel: +3612888888")));
		assertNull(bank
				.extractCode("Sikeres bejelentkez�s - FHB NetB@nk. Felhasznalonev: Minta Janos. Idopont: 2009.08.24 14:30"));

		//BudapestBank
		bank = Bank.findByPhoneNumber("+36309266245");
		assertTrue("51930398"
				.equals(bank
						.extractCode("Az �n ideiglenes k�dja: 51930398 Ez a k�d bel�p�shez 13:47:58-ig haszn�lhatja, de �rizze meg a tranzakci�hoz! Kapcsolat azonos�t�: 133758 Budapest Bank")));
		assertNull(bank
				.extractCode("Sikeres bejelentkez�s - FHB NetB@nk. Felhasznalonev: Minta Janos. Idopont: 2009.08.24 14:30"));

	}

}
