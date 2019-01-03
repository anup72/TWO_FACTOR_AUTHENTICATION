package panaceaADVweb.ADVCtlbean;

import panacea.common.DTObject;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import panacea.common.JNDINames;
import panacea.delegate.CommonDelegate;
import panaceaweb.utility.QueryManager;
import panacea.Validator.AccessValidator;
import panacea.Validator.PasswordValidator;
import panacea.ADVaction.mtwofaconfval;

import panaceaweb.utility.Common;

public class mtwofaconfctl extends Common {
	// Commonly added-Constructor - Beg 06-Nov-2009
	public mtwofaconfctl() {
		qrymgr = new QueryManager(getsession());
		mtwofaconfinstance = new mtwofaconfval(getsession());
	}

	public mtwofaconfctl(HttpSession _session) {
		super(_session);
		qrymgr = new QueryManager(getsession());
		mtwofaconfinstance = new mtwofaconfval(getsession());
	}

	private String userId = "";
	private String userPass = "";

	private String muserOption = "";
	private String mtxnStatus = "";
	private String merrmsg = "";
	private String msecretKey = "";
	private String motpsuccessflag = "";
	
	DTObject inputDTO = new DTObject();
	DTObject outputDTO = new DTObject();
	DTObject revalDTO = new DTObject();
	private DTObject DTOResult;
	QueryManager qrymgr;

	mtwofaconfval mtwofaconfinstance;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPass() {
		return userPass;
	}

	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}

	public String getMuserOption() {
		return muserOption;
	}

	public void setMuserOption(String muserOption) {
		this.muserOption = muserOption;
	}

	public String getMtxnStatus() {
		return mtxnStatus;
	}

	public void setMtxnStatus(String mtxnStatus) {
		this.mtxnStatus = mtxnStatus;
	}

	public String getMerrmsg() {
		return merrmsg;
	}

	public void setMerrmsg(String merrmsg) {
		this.merrmsg = merrmsg;
	}

	public String getMsecretKey() {
		return msecretKey;
	}

	public void setMsecretKey(String msecretKey) {
		this.msecretKey = msecretKey;
	}

	public DTObject getDTOResult() {
		return DTOResult;
	}

	public void setDTOResult(DTObject dTOResult) {
		DTOResult = dTOResult;
	}
	
	

	public String getMotpsuccessflag() {
		return motpsuccessflag;
	}

	public void setMotpsuccessflag(String motpsuccessflag) {
		this.motpsuccessflag = motpsuccessflag;
	}

	public void setreturnvalue() {
		sethiddenidValues("useroption", getMuserOption());
		setV_view_component_Id("txnstatus#errmsg");
		setcommonvalues(getMtxnStatus(), getMerrmsg());
	}

	public String PersistData() {
		if (RevalidateMTWOFACONF() == true) {
			CommonDelegate CDelagate = null;
			DTObject inputDTO = new DTObject();
			inputDTO.clearMap();
			inputDTO.setValue("TWOFACONFH_USER_ID", userId);
			inputDTO.setValue("TWOFACONFH_SEC_KEY", msecretKey);
			inputDTO.setValue("TWOFACONFH_ENTRY_DATE", getM_CurrBusDate());
			inputDTO.setValue("UserOption", muserOption);
			inputDTO.setValue("Class", JNDINames.MTWOFACONF_EJBHOME);
			inputDTO.setValue("USERID", getM_Userid());
			inputDTO.setValue("USRBRNCODE", getM_BranchCode());
			inputDTO.setValue("IPADDRESS", get_IPaddress());
			try {
				CDelagate = new CommonDelegate();
				addcommonobject(inputDTO);
				DTOResult = CDelagate.setInfo(inputDTO);
				System.out.println(DTOResult.getValue("Result"));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				if ("SUCCESS".equals(DTOResult.getValue("Result"))) {
					mtxnStatus = "0";
				} else {
					// 1 is used to indicate failure
					mtxnStatus = "1";
					merrmsg = "";
					if (DTOResult.containsKey("Errmsg"))
						;
					{
						merrmsg = DTOResult.getValue("Errmsg");
					}
				}
			}

			{
				setreturnvalue();
				return "success";
			}
		} else {
			{
				setreturnvalue();
				return "failure";
			}
		}

	}

	private boolean RevalidateMTWOFACONF() {

		if (!revalmtwofaconfUserId()) {
			return false;
		}

		if (!revalmtwofaconfUserPassword()) {
			return false;
		}
		if(motpsuccessflag.equalsIgnoreCase("0")){
			merrmsg = "MF:txtUserId|" +"One Time Password Validation Failed";
			return false;
		}
		return true;
	}

	private boolean revalmtwofaconfUserId() {
		revalDTO.clearMap();
		revalDTO.setValue("USER_ID", userId);
		revalDTO.setValue("LOGGED_USER_ID", getM_Userid());
		revalDTO = mtwofaconfinstance.userIdkeypress(revalDTO);
		if (!revalDTO.getValue(ErrorKey).equalsIgnoreCase("")) {
			merrmsg = "MF:txtUserId|" + revalDTO.getValue(ErrorKey).toString();
			return false;
		}
		return true;

	}

	private boolean revalmtwofaconfUserPassword() {
		String loggedusrpword = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("LoggedUserPword");
		revalDTO.clearMap();
		revalDTO.setValue("USER_PASSWORD", userPass);
		revalDTO.setValue("LOGGED_USER_PASSWORD", loggedusrpword);
		revalDTO = mtwofaconfinstance.userPasswordkeypress(revalDTO);
		if (!revalDTO.getValue(ErrorKey).equalsIgnoreCase("")) {
			merrmsg = "MF:txtUserPass|" + revalDTO.getValue(ErrorKey).toString();
			return false;
		}
		setUserOption();
		return true;
	}

	private void setUserOption() {
		QueryManager qrymgr = new QueryManager(getsession());
		inputDTO.clearMap();
		inputDTO.setValue("SQLToken", "Valmtwofaconf1");
		inputDTO.setValue("Args", userId + "|" + getM_CurrBusDate());
		inputDTO.setValue("DataTypes", "S|D");
		outputDTO = qrymgr.getInfo(inputDTO);
		if (outputDTO.getValue("Result").equalsIgnoreCase("RowPresent")) {
			muserOption = "M";
		} else if (outputDTO.getValue("Result").equalsIgnoreCase("RowNotPresent")) {
			muserOption = "A";
		}
	}

}
