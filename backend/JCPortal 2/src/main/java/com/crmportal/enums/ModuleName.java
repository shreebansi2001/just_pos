package com.crmportal.enums;

public enum ModuleName {

	USERBASICFILE, // sale / manager / call
	USERDOCUMENT, // KYC
	USERDOWNPAYMENT, // invoice / receipt
	USERAMCFILE, // AMC docs
	REFUNDDETAILFILE, // refund docs
	TRIPEXPENSEFILE, OFFICEEXPENSEFILE,

	USERTEMPLATE, // reports, name plate
	PARTY, MENUCATEGORY, MENUITEM, USERLOGO, USEREXPENSE, UTILITY, REPORTPAGE, TICKET, MISC, RAWMATERIAL,

	SUPERADMIN_INVOICE,

	CHECKLIST,

	FONTS,

	MANAGERTASK,

	QRCODE,

	GROUDIMAGE,

	SPECIALNOTES, LABOR_HELPER,

	BANQUETHALL, DECOREMAINCATEGORY, DECOREMAINCATEGORYITEM, EVENTFUNCTIONDECOREMAINCATEGORYITEM,

	CATEGORY_BG,

	EVENT_IMG;

	public static ModuleName fromString(String s) {
		if (s == null)
			return MISC;
		try {
			return ModuleName.valueOf(s.trim().toUpperCase());
		} catch (Exception e) {
			return MISC;
		}
	}
}
