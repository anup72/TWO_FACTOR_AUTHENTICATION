package panacea.ADV.ejb;

import java.rmi.RemoteException;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import panacea.common.DTObject;
import panacea.common.PanaceaException;
import panacea.TranControl.TransControl;
import panacea.ADV.Update.Twofaconf;
import panacea.ADV.Update.TwofaconfManager;
import panacea.ADV.Update.Twofaconfhist;
import panacea.ADV.Update.TwofaconfhistManager;
import panacea.ADV.interfaces.mtwofaconf;
import panacea.ADV.interfaces.mtwofaconfLocal;

@Stateless
@Remote({ mtwofaconf.class })
@Local({ mtwofaconfLocal.class })
@TransactionManagement(TransactionManagementType.BEAN)
public class mtwofaconfBO extends TransControl {

	private String Option;
	@Resource
	private SessionContext context;

	public mtwofaconfBO() {
		super();
	}

	private void Init_Para() throws PanaceaException {
		Tba_key_In_Main_Table = "";
		Programid = "mtwofaconf";
		TBAAUTH_MAIN_TABLE_NAME = "TWOFACONFHIST";
		// TBAAUTH_MAIN_PK Should be in Table Column Order(change below line)
		TBAAUTH_MAIN_PK = dtoobj.getValue("TWOFACONFH_USER_ID") + "|" + dtoobj.getValue("TWOFACONFH_ENTRY_DATE");
		TBAAUTH_ENTRY_DATE = null;
		TBAAUTH_DTL_SL = 0;
		TBA_DISPLAY_DTLS = "";
		Option = dtoobj.getValue("UserOption");
		TBAAUTH_OPERN_FLG = Option;
		User_Id = dtoobj.getValue("USERID");
		ReturnResult = new DTObject();
		ReturnResult.clearMap();
		tba_auth_queue_req = false;
		Table_Class_Name = "TWOFACONFHIST";
		Twofaconfhist TwofaconfhistInstance = null;
		TwofaconfhistManager TwofaconfhistManagerInstance = new TwofaconfhistManager(_COLLECTIONObj, V_LOG_REQ,
				V_ADD_LOG_REQ);
		try {
			TwofaconfhistInstance = TwofaconfhistManagerInstance.loadByKey(
					DateToYYYYMMDD(dtoobj.getValue("TWOFACONFH_ENTRY_DATE")), dtoobj.getValue("TWOFACONFH_USER_ID"));
			if (TwofaconfhistInstance != null) {
				Tba_key_In_Main_Table = TwofaconfhistInstance.getTbaMainKey();
			} else {
				Tba_key_In_Main_Table = "";
			}
		} catch (SQLException e) {
			throw new PanaceaException(e.getLocalizedMessage());
		}
	}

	public DTObject updateValues(DTObject InputmapObj) throws EJBException, RemoteException {
		setcommonvalues(InputmapObj);
		dtoobj = InputmapObj;
		try {
			Init_Para();
			BeginTransaction(context);
			check_for_tba_updation();
			if (Option.equalsIgnoreCase("A")) {
				addRecord(InputmapObj);
				addRecordhist(InputmapObj);
			} else {
				modRecord(InputmapObj);
				modRecordhist(InputmapObj);
			}
			CommitTran();
		} catch (PanaceaException Excep) {
			RollBackTran(Excep.getLocalizedMessage());
		}
		return ReturnResult;
	}

	private void Set_Entd_Dtls(Twofaconf inputObj) throws PanaceaException {
		if (Option.equalsIgnoreCase("A")) {
			inputObj.setTwofaconfEntdBy(User_Id);
			inputObj.setTwofaconfEntdOn(Get_System_Time());
			inputObj.setTwofaconfLastModBy("");
			inputObj.setTwofaconfLastModOn(null);
		} else {
			inputObj.setTwofaconfLastModBy(User_Id);
			inputObj.setTwofaconfLastModOn(Get_System_Time());
			inputObj.setTwofaconfAuthBy("");
			inputObj.setTwofaconfAuthOn(null);
		}
		if (tba_auth_queue_req == true) {
			inputObj.setTwofaconfAuthBy("");
			inputObj.setTwofaconfAuthOn(null);
		} else {
			inputObj.setTwofaconfAuthBy(User_Id);
			inputObj.setTwofaconfAuthOn(Get_System_Time());
		}
	}

	private void Set_Entd_Dtls_Hist(Twofaconfhist inputObj) throws PanaceaException {
		if (Option.equalsIgnoreCase("A")) {
			inputObj.setTwofaconfhEntdBy(User_Id);
			inputObj.setTwofaconfhEntdOn(Get_System_Time());
			inputObj.setTwofaconfhLastModBy("");
			inputObj.setTwofaconfhLastModOn(null);
		} else {
			inputObj.setTwofaconfhLastModBy(User_Id);
			inputObj.setTwofaconfhLastModOn(Get_System_Time());
			inputObj.setTwofaconfhAuthBy("");
			inputObj.setTwofaconfhAuthOn(null);
		}
		if (tba_auth_queue_req == true) {
			inputObj.setTwofaconfhAuthBy("");
			inputObj.setTwofaconfhAuthOn(null);
		} else {
			inputObj.setTwofaconfhAuthBy(User_Id);
			inputObj.setTwofaconfhAuthOn(Get_System_Time());
		}
	}

	private void addRecord(DTObject dtoobject) throws PanaceaException {
		try {
			Twofaconf TwofaconfInstance = null;
			TwofaconfManager TwofaconfManagerInstance = new TwofaconfManager(_COLLECTIONObj, V_LOG_REQ, V_ADD_LOG_REQ);
			TwofaconfInstance = TwofaconfManagerInstance.loadByKey(dtoobject.getValue("TWOFACONFH_USER_ID"));
			if (TwofaconfInstance != null) {
				deleteMtwofaconf(dtoobject);
			}
			Twofaconf TwofaconfInstance1 = new Twofaconf();
			TwofaconfManager TwofaconfManagerInstance1 = new TwofaconfManager(_COLLECTIONObj, V_LOG_REQ,
					V_ADD_LOG_REQ);
			TwofaconfInstance1.setTwofaconfUserId(dtoobject.getValue("TWOFACONFH_USER_ID"));
			TwofaconfInstance1.setTwofaconfSecKey(dtoobject.getValue("TWOFACONFH_SEC_KEY"));
			TwofaconfInstance1.setTwofaconfEntryDate(DateToYYYYMMDD(dtoobject.getValue("TWOFACONFH_ENTRY_DATE")));
			Set_Entd_Dtls(TwofaconfInstance1);
			TwofaconfInstance1.setIsNew(true);
			TwofaconfManagerInstance1.save(TwofaconfInstance1, TBAAUTH_MAIN_PK, TBAAUTH_ENTRY_DATE, TBAAUTH_DTL_SL);

		} catch (Exception e) {
			throw new PanaceaException(e.getLocalizedMessage());
		}
	}

	private void modRecord(DTObject dtoobject) throws PanaceaException {
		try {
			Twofaconf TwofaconfInstance = null;
			TwofaconfManager TwofaconfManagerInstance = new TwofaconfManager(_COLLECTIONObj, V_LOG_REQ, V_ADD_LOG_REQ);
			TwofaconfInstance = TwofaconfManagerInstance.loadByKey(dtoobject.getValue("TWOFACONFH_USER_ID"));
			if (TwofaconfInstance != null) {
				TwofaconfInstance.setTwofaconfUserId(dtoobject.getValue("TWOFACONFH_USER_ID"));
				TwofaconfInstance.setTwofaconfSecKey(dtoobject.getValue("TWOFACONFH_SEC_KEY"));
				TwofaconfInstance.setTwofaconfEntryDate(DateToYYYYMMDD(dtoobject.getValue("TWOFACONFH_ENTRY_DATE")));
				Set_Entd_Dtls(TwofaconfInstance);
				TwofaconfInstance.setIsNew(false);
				TwofaconfManagerInstance.save(TwofaconfInstance, TBAAUTH_MAIN_PK, TBAAUTH_ENTRY_DATE, TBAAUTH_DTL_SL);
			}
		} catch (Exception e) {
			throw new PanaceaException(e.getLocalizedMessage());
		}
	}

	private void addRecordhist(DTObject dtoobject) throws PanaceaException {
		try {
			Twofaconfhist TwofaconfhistInstance = new Twofaconfhist();
			TwofaconfhistManager TwofaconfhistManagerInstance = new TwofaconfhistManager(_COLLECTIONObj, V_LOG_REQ,
					V_ADD_LOG_REQ);
			TwofaconfhistInstance.setTwofaconfhUserId(dtoobject.getValue("TWOFACONFH_USER_ID"));
			TwofaconfhistInstance.setTwofaconfhSecKey(dtoobject.getValue("TWOFACONFH_SEC_KEY"));
			TwofaconfhistInstance.setTwofaconfhEntryDate(DateToYYYYMMDD(dtoobject.getValue("TWOFACONFH_ENTRY_DATE")));
			Set_Entd_Dtls_Hist(TwofaconfhistInstance);
			TwofaconfhistInstance.setIsNew(true);
			TwofaconfhistManagerInstance.save(TwofaconfhistInstance, TBAAUTH_MAIN_PK, TBAAUTH_ENTRY_DATE,
					TBAAUTH_DTL_SL);
		} catch (Exception e) {
			throw new PanaceaException(e.getLocalizedMessage());
		}
	}

	private void modRecordhist(DTObject dtoobject) throws PanaceaException {
		try {
			Twofaconfhist TwofaconfhistInstance = null;
			TwofaconfhistManager TwofaconfhistManagerInstance = new TwofaconfhistManager(_COLLECTIONObj, V_LOG_REQ,
					V_ADD_LOG_REQ);
			TwofaconfhistInstance = TwofaconfhistManagerInstance.loadByKey(
					DateToYYYYMMDD(dtoobject.getValue("TWOFACONFH_ENTRY_DATE")),
					dtoobject.getValue("TWOFACONFH_USER_ID"));
			if (TwofaconfhistInstance != null) {
				TwofaconfhistInstance.setTwofaconfhUserId(dtoobject.getValue("TWOFACONFH_USER_ID"));
				TwofaconfhistInstance.setTwofaconfhSecKey(dtoobject.getValue("TWOFACONFH_SEC_KEY"));
				TwofaconfhistInstance
						.setTwofaconfhEntryDate(DateToYYYYMMDD(dtoobject.getValue("TWOFACONFH_ENTRY_DATE")));
				Set_Entd_Dtls_Hist(TwofaconfhistInstance);
				TwofaconfhistInstance.setIsNew(false);
				TwofaconfhistManagerInstance.save(TwofaconfhistInstance, TBAAUTH_MAIN_PK, TBAAUTH_ENTRY_DATE,
						TBAAUTH_DTL_SL);
			}
		} catch (Exception e) {
			throw new PanaceaException(e.getLocalizedMessage());
		}
	}

	private void deleteMtwofaconf(DTObject dtoobject) throws PanaceaException {
		try {
			String userId = dtoobject.getValue("TWOFACONFH_USER_ID");
			TwofaconfManager TwofaconfManagerInstance = new TwofaconfManager(_COLLECTIONObj, V_LOG_REQ, V_ADD_LOG_REQ);
			TwofaconfManagerInstance.deleteByKey(userId);
		}catch (Exception e) {
			throw new PanaceaException(e.getLocalizedMessage());
		}

	}

}
