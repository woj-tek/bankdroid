package bankdroid.smskey.bank;

class Banks_VN extends AbstractBanks
{
	@Override
	public void init()
	{
		addBank(new Bank(-1, "Vietcombank", -1, // id, bankName, expiry
				new String[] { "+8170" }, // phoneNumbers
				new Expression[] {// expressions
				new Expression(false, "VCB .* khau ([0-9a-zA-Z]+).*") // transactionSigning, regexp
				}, "VN")); // country

	}
}
