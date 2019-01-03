<%--1.0  Form Id = mtwofaconf.jsp.jsp-Optimization Changes    08-Feb-2008  --%>
<!--a.	JSP Name	       		:	mtwofaconf.jsp
   	b.  Function            	:   User Two Factor Authentication Master 
   	c.  Form Date         		:   02/12/2018
   	d.  Author              	:   Anup Paul
   	e.  Module              	:   ADV Module			
  	g. 	Modification History 	:
   	 ____________________________________________________________________
 	|Sl.No.|	Modified By	|	 Modified On |	Given By	|	Remarks  |
 	|______|________________|________________|______________|____________|
 	|______|________________|________________|_____________ |____________|
 	|______|________________|________________|______________|____________|
 -->


<%@ include file="../Header_mb.jsp"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.security.SecureRandom"%>
<%-- <%@ page import="panaceaweb.utility.QRCodeRetrivingServlet.*"%> --%>



<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<base href="<%=basePath%>">
<SCRIPT language="JavaScript" src="scripts/common_mb.js"></SCRIPT>
<SCRIPT language="JavaScript" src="scripts/OthFuncs_mb.js"></SCRIPT>
<SCRIPT language="JavaScript" src="scripts/HTTPValidator_mb.js"></SCRIPT>
<SCRIPT language="JavaScript" src="scripts/pwdNegative.js"></SCRIPT>
<link rel="stylesheet" href="theme/Panaceastyle_mb.css" type="text/css" />


<%
	StringBuffer saltBuf = new StringBuffer();
	if (session.getAttribute("RandomNumber") == null) {
		String hexits = "0123456789ABCDEF";
		SecureRandom sr = new SecureRandom();
		byte[] keyBytes = new byte[32];
		sr.nextBytes(keyBytes);
		sr.nextBytes(keyBytes);
		for (int i = 0; i < keyBytes.length; ++i) {
			saltBuf.append(hexits.charAt((keyBytes[i] >>> 4) & 0xf));
			saltBuf.append(hexits.charAt(keyBytes[i] & 0xf));
		}
		session.setAttribute("RandomNumber", saltBuf.toString());
	} else {
		saltBuf.append(session.getAttribute("RandomNumber"));
	}
%>

<SCRIPT type="text/javascript" src="scripts/sha256.js"></SCRIPT>
<script language="JavaScript" src="scripts/aes.js"></script>
<script language="JavaScript" src="scripts/scrypt.js"></script>

<script language="Javascript">
	
	    var lastEntdFld ;
	    var fldobj ;
	    var Args ;
	    var errormsg ;
	    var flag;
	    var cbd;
	    var Uid;
	    var Upword;
	    var seckey;
	    
// ---------------------------------------   Normal Screen Validation   -----------------------------------------------    
     function InitJSvar(){
   try {com_init_values();}catch(e){}
}
	function InitPara()
	{
            InitJSvar();
			PGM_VERSION=  "1";
			seckey ="";
        	DISPLAY_STATUS("MF:txtUserId","MF:txnstatus","MF:errmsg");
        	cbd = "<%=session.getAttribute("CBD")%>";
        	Uid="<%=session.getAttribute("LoggedUser")%>";
        	Upword="<%=session.getAttribute("LoggedUserPword")%>";
        	disableQRCode();

	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
	function setFocus(fldobj) {
		switch (trim(fldobj)) {
		case "MF:txtUserId":
			gid('MF:txtUserId').focus();
			break;
		case "MF:txtUserPass":
			gid('MF:txtUserPass').focus();
			break;
		case "MF:txtSeckeyVal":
			gid('MF:txtSeckeyVal').focus();
			break;
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
	function clearFields() {

		gid('MF:txtUserId').value = "";
		gid('MF:txtUserPass').value = "";
		clearNonKeyFields();
	}

	function clearNonKeyFields() {

		gid('MF:useroption').value = "";
		gid('MF:errmsg').value = "";
		gid('MF:txnstatus').value = "";
		gid('MF:txtSeckeyVal').value = "";
		gid('MF:txtSeckeyVal').disabled = true;
		gid('MF:SeckeyButton').disabled = true;
		gid("MF:txtNewPword").value = "";
		gid("MF:txtNewPword").disabled = true;
		gid('MF:OTPValButton').disabled = true;
		gid('MF:secretkey').value = "";
		disableQRCode();
		gid('MF:otpflag').value = "0";
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------

	function cancelPage() {
		if (confirm(CANCEL_ALERT) == true) {
			clearFields();
			setMsg("");
			gid('MF:txtUserId').focus();
		} else
			lastEntdFld.focus();

	}

	function exitPage() {
		if (confirm(EXIT_ALERT) == true)
			window.close();
		else
			lastEntdFld.focus();
	}

	function GotFocusEvents(fldobj) {
		lastEntdFld = fldobj;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------------------- 
	function ChangeEvents(fldobj, ev) {
		ev = ev || window.event;
		switch (fldobj.id) {
		case "MF:txtUserId": {
			if (trim(gid('MF:txtUserId').value) == "") {
				// (gid('MF:useroption').value != "A") {
					gid('MF:txtUserPass').value = "";
					clearNonKeyFields();
				//}
			}
		}
		break;
		
		case "MF:txtUserPass": {
			if (trim(gid('MF:txtUserPass').value) == "") {
				// (gid('MF:useroption').value != "A") {
					clearNonKeyFields();
				//}
			}
		}
		break;

		}
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------- 
	function KeyDownEvents(fldobj, ev) {
		ev = ev || window.event;
		setMsg("");
		if (ev.keyCode == KEY_TAB) {
			if (ev.shiftKey == false) {
				try {
					ev.keyCode = 13;
				} catch (e) {
				}
				KeyPressEvents(fldobj, ev);
			} else {
				try {
					ev.keyCode = 0;
				} catch (e) {
				}
				dostopevent(ev);
				PERFORM_BACKTRACK(fldobj);
				return;
			}
		}
		switch (ev.keyCode) {
		case KEY_F2:
			PERFORM_BACKTRACK(fldobj);
			break;
		case KEY_F5:
			SHOW_HELP(fldobj, ev);
			break;
		}

	}
	function FormKeyDown(ev) {
		ev = ev || window.event;
		if (ev.keyCode == KEY_ESC) {
			switch (lastEntdFld.id) {
			case "MF:txtUserId":
				exitPage();
				break;
			default:
				cancelPage();
				break;
			}
		}
	}

	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	function PERFORM_BACKTRACK(fldobj) {
		switch (fldobj.id) {

		case "MF:txtUserPass":
			gid('MF:txtUserId').focus();
			break;
		case "MF:txtNewPword":
			gid('MF:txtUserPass').focus();
			break;
		case "MF:OTPValButton":
			gid('MF:txtNewPword').focus();
			break;

		}
	}
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
	function SHOW_HELP(fldobj, ev) {
		switch (fldobj.id) {

		case "MF:txtUserId": {
			helpToken = "Hlptwofaconf1";
			fldArgs = gid('MF:txtUserId').value;
			registerAddInfo("TEXT");
			showHelp(helpToken, fldobj, fldArgs);
			break;
		}
		}
	}

	function getAdddetails(addinfo) {
		if (trim(addinfo) != "") {
			strhelp = addinfo.split("|");
			switch (lastEntdFld.id) {
			case "MF:txtUserId": {
				gid('MF:txtUserId').value = strhelp[0];
				break;
			}
			}
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
	function KeyPressEvents(fldobj, ev) {
		ev = ev || window.event;
		__ch = isPressKeyCode(ev);
		if (__ch == KEY_TAB) {
			dostopevent(ev);
			if (ev.shiftKey)
				return;
		}
		if (ev.keyCode == KEY_TAB || isEnterKeyPressed(ev)) {
			switch (fldobj.id) {

			case "MF:txtUserId":
				validateUserId();
				break;
			case "MF:txtUserPass":
				validateUserPass();
				break;
			case "MF:txtNewPword":
				validateNewPword();
				break;

			}
		}

		else if ((fldobj.id == "MF:txtUserId")) {
			To_Uppercase(ev);
		}
	}

	// ---------------------------------------  End of  Normal Screen Validation   ----------------------------------------------	    

	function validateUserId() {
		if (trim(gid('MF:txtUserId').value) == "") {
			setMsg(BLANK_CHECK);
			return;
		} else {
			objXMLApplet.clearMap();
			objXMLApplet.setValue("Package", "panacea.ADVaction.mtwofaconfval");
			objXMLApplet.setValue("Method", "userIdkeypress");
			objXMLApplet.setValue("ValidateToken", "true");
			objXMLApplet.setValue("USER_ID", trim(gid('MF:txtUserId').value));
			objXMLApplet.setValue("LOGGED_USER_ID", trim(Uid));
			objXMLApplet.sendAndReceive();
			if (trim(objXMLApplet.getValue("ErrorMsg")) != "") {
				setMsg(objXMLApplet.getValue("ErrorMsg"));
				clearNonKeyFields();
				gid('MF:txtUserId').focus();
				return false;
			} else
				gid('MF:txtUserPass').focus();
		}
	}
	//---------------------------------------------------------------------------------------------------------------------      

	function validateUserPass() {
		if (trim(gid('MF:txtUserPass').value) == "") {
			setMsg(BLANK_CHECK);
			return false;
		} else {
			objXMLApplet.clearMap();
			objXMLApplet.setValue("Package", "panacea.ADVaction.mtwofaconfval");
			objXMLApplet.setValue("Method", "userPasswordkeypress");
			objXMLApplet.setValue("ValidateToken", "true");
			objXMLApplet.setValue("USER_PASSWORD",trim(gid('MF:txtUserPass').value));
			objXMLApplet.setValue("LOGGED_USER_PASSWORD", trim(Upword));
			objXMLApplet.sendAndReceive();
			if (trim(objXMLApplet.getValue("ErrorMsg")) != "") {
				setMsg(objXMLApplet.getValue("ErrorMsg"));
				clearNonKeyFields();
				gid('MF:txtUserPass').focus();
				return false;
			} else {
				objXMLApplet.clearMap();
				objXMLApplet.setValue("Package", "Panaceaweb.utility");
				objXMLApplet.setValue("SQLToken", "Valmtwofaconf1");
				objXMLApplet.setValue("MTable", "TWOFACONFHIST");
				Args = gid('MF:txtUserId').value + "|" + cbd;
				objXMLApplet.setValue("Args", Args);
				objXMLApplet.setValue("Primkey", Args);
				objXMLApplet.setValue("DataTypes", "S|D");
				objXMLApplet.sendAndReceive();
				if (objXMLApplet.getValue("ErrorMsg") != "") {
					setMsg(objXMLApplet.getValue("ErrorMsg"));
					gid("MF:txtUserPass").focus();
					return false;
				} else if (objXMLApplet.getValue("Result") == "RowNotPresent") {
					gid("MF:useroption").value = "A";
					gid('MF:SeckeyButton').disabled = false;
					gid('MF:SeckeyButton').focus();
					return true;
				} else if (objXMLApplet.getValue("Result") == "RowPresent") {
					gid("MF:useroption").value = "M";
					gid('MF:txtSeckeyVal').disabled = false;
					gid('MF:txtSeckeyVal').value = objXMLApplet.getValue("TWOFACONFH_SEC_KEY");
					seckey = objXMLApplet.getValue("TWOFACONFH_SEC_KEY");
					gid('MF:txtSeckeyVal').disabled = true;
					gid("MF:txtNewPword").disabled = false;
					gid('MF:txtNewPword').focus();

				}
				return true;
			}
		}

	}

	function validateNewPword() {

		if (trim(gid("MF:txtNewPword").value) == "") {
			setMsg("Confirm One Time Password");
			gid("MF:txtNewPword").focus();
			return false;
		} else {
			gid('MF:OTPValButton').disabled = false;
			gid('MF:OTPValButton').focus();
		}
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	function onClickSave() {
		gid('MF:secretkey').value = seckey;
		submitForm();
	}

	function onClickSeckey() {

		if (gid('MF:SeckeyButton').disabled == false) {			
				objXMLApplet.clearMap();
				objXMLApplet.setValue("Package","panacea.ADVaction.mtwofaconfval");
				objXMLApplet.setValue("Method", "generateSecretKey");
				objXMLApplet.setValue("ValidateToken", "true");
				objXMLApplet.setValue("USER_ID",trim(gid('MF:txtUserId').value));
				objXMLApplet.setValue("USER_PASSWORD",trim(gid('MF:txtUserPass').value));
				objXMLApplet.sendAndReceive();
				if (trim(objXMLApplet.getValue("ErrorMsg")) != "") {
					setMsg(objXMLApplet.getValue("ErrorMsg"));
					clearNonKeyFields();
					gid('MF:txtUserId').focus();
					return false;
				} else {
					seckey = objXMLApplet.getValue("SECRET_KEY");
					gid('MF:txtSeckeyVal').disabled = false;
					displayQRCode();
					gid('MF:txtSeckeyVal').value = seckey;
					gid('MF:txtSeckeyVal').disabled = true;
					gid("MF:txtNewPword").disabled = false;
					gid('MF:txtNewPword').focus();

				}
		}
	}

	function onClickValOTP() {
		objXMLApplet.clearMap();
		objXMLApplet.setValue("Package", "panacea.ADVaction.mtwofaconfval");
		objXMLApplet.setValue("Method", "ValidateOTP");
		objXMLApplet.setValue("ValidateToken", "true");
		objXMLApplet.setValue("USER_ID", trim(gid('MF:txtUserId').value));
		objXMLApplet.setValue("NEW_PASSWORD", trim(gid("MF:txtNewPword").value));
		objXMLApplet.setValue("CBD", cbd);
		objXMLApplet.setValue("SECRET_KEY", seckey);
		objXMLApplet.sendAndReceive();
		if (objXMLApplet.getValue("ErrorMsg") != "") {
			alert(objXMLApplet.getValue("ErrorMsg"));
			gid('MF:otpflag').value = "0";
			gid("MF:txtNewPword").value = '';
			gid("MF:txtNewPword").focus();
			return;
		} else {
			alert("OTP Successfully Matched");
			gid("MF:otpflag").value ="1";
			gid('MF:Submit').focus();
		}

	}

	function displayQRCode() {
		document.getElementById("qrlogo").style.visibility = "visible";
		document.getElementById("qrlogo").style.height = "200px";
		document.getElementById("qrlogo").style.width = "200px";
		document.getElementById("qrlogo").src = "QRCodeRetrivingServlet?userid="+ trim(gid('MF:txtUserId').value)
				+ "&userpword="+ trim(gid('MF:txtUserPass').value) + "&seckey=" + seckey;

	}
	function disableQRCode() {
		document.getElementById("qrlogo").src ="";
		document.getElementById("qrlogo").style.height = "0px";
		document.getElementById("qrlogo").style.width = "0px";
		document.getElementById("qrlogo").style.visibility = "hidden";
	}

	// -------------------------------------	End Field Validation   ----------------------------------------------------------------
</script>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<title>Two Factor Authentication Configuration Master</title>

</head>

<body scroll=no class="bdycls-master" onload="InitPara()"
	onkeydown="FormKeyDown(event)">
	<f:view>
		<f:loadBundle basename="panaceaADVweb.advMsg.LabelMsg" var="msgBundle" />
		<br>
		<h:form id="MF">

			<div align="center" class="form-align" style="top: 10%;">
				<div align="left" class="block-align use-border" style="width: 85%; height:80px;">
					<div align="left" class="block-align" style="width: 100%;">

						<h:panelGrid border='0' columns="2" frame="none" style="height:70px;" >

							<h:column>
								<h:outputText id="lblUserName" value="#{msgBundle.lblUserId}"
									styleClass="lbltxt" style="display:block;width: 280px;" />
							</h:column>
							<h:column>
								<h:inputText id="txtUserId" value="#{mtwofaconfctl.userId}"
									styleClass="inputfld" size="10" maxlength="8" required="false"
									onkeypress="KeyPressEvents(this,event)"
									onkeydown="KeyDownEvents(this,event)"
									onkeyup="ChangeEvents(this,event)"
									onfocus="GotFocusEvents(this)" />
							</h:column>

							<h:column>
								<h:outputText id="lblUserPass" value="#{msgBundle.lblUserPword}"
									styleClass="lbltxt" style="display:block;width: 280px;" />
							</h:column>
							<h:column>
								<h:inputSecret id="txtUserPass"
									value="#{mtwofaconfctl.userPass}" styleClass="inputfld"
									size="17" onkeypress="KeyPressEvents(this,event)"
									onkeydown="KeyDownEvents(this,event)" required="false"
									onfocus="GotFocusEvents(this)"
									onkeyup="ChangeEvents(this,event)">
								</h:inputSecret>
							</h:column>

						</h:panelGrid>

					</div>
				</div>
				<br>

				<div align="left" class="block-align use-border" style="WIDTH: 85%">
					<div align="left" class="block-align" style="WIDTH: 100%; top: 15%">

						<h:panelGrid border='0' columns="1" frame="none">

							<h:column>
								<h:commandButton id="SeckeyButton" type="button" action=""
									value="Generate Details" accesskey="S" style="width:150px"
									onkeydown="KeyDownEvents(this,event)" onclick="onClickSeckey()" />
							</h:column>
						</h:panelGrid>
						<table align="center">
							<tr>
								<td><img id="qrlogo" src="" /></td>
							</tr>
						</table>

						<h:panelGrid border='0' columns="2" frame="none">
							<h:column>
								<h:outputText id="lblSeckeyVal" value="Secret Key"
									styleClass="lbltxt" style="display:block;width: 280px;" />
							</h:column>

							<h:column>
								<h:inputText id="txtSeckeyVal" value="" styleClass="inputfld"
									size="25" maxlength="20" required="false"
									onkeypress="KeyPressEvents(this,event)"
									onkeydown="KeyDownEvents(this,event)"
									onfocus="GotFocusEvents(this)" style="display: hidden" />

							</h:column>

						</h:panelGrid>

						<h:panelGrid border='0' columns="3" frame="none">

							<h:column>
								<h:outputText id="lblnewpword" value="One Time Password"
									styleClass="lbltxt" style="display:block;width: 280px;" />
							</h:column>
							<h:column>
								<h:inputSecret id="txtNewPword" value="" styleClass="inputfld"
									size="17" onkeypress="KeyPressEvents(this,event)"
									onkeydown="KeyDownEvents(this,event)"
									onfocus="GotFocusEvents(this)" />
							</h:column>

							<h:column>
								<h:commandButton id="OTPValButton" type="button" action=""
									value="Validate OTP" accesskey="S" style="width:100px"
									onkeydown="KeyDownEvents(this,event)" onclick="onClickValOTP()" />
							</h:column>


						</h:panelGrid>

					</div>
				</div>

				<h:inputHidden id="secretkey" value="#{mtwofaconfctl.msecretKey}" />
				<h:inputHidden id="otpflag" value="#{mtwofaconfctl.motpsuccessflag}" />
				<h:inputHidden id="useroption" value="#{mtwofaconfctl.muserOption}" />
				<h:inputHidden id="txnstatus" value="#{mtwofaconfctl.mtxnStatus}" />
				<h:inputHidden id="errmsg" value="#{mtwofaconfctl.merrmsg}" />
			</div>

			<div class="cmdtable">
				<table border='0' cellspacing="0" cellpadding="2" width="100%">
					<tr>
						<td align="center"><h:commandButton id="Submit" type="button"
								action="#{mtwofaconfctl.PersistData}"
								value="#{msgBundle.cmdsave}" accesskey="S" style="width:80px"
								onkeydown="KeyDownEvents(this,event)" onclick="onClickSave()" />
							<h:commandButton id="Cancel" type="button" accesskey="C"
								value="#{msgBundle.cmdcancel}" style="width:80px"
								onclick="cancelPage()" onkeydown="KeyDownEvents(this,event)" />
							<h:commandButton id="Exit" type="button" accesskey="X"
								value="#{msgBundle.cmdexit}" style="width:80px"
								onclick="exitPage()" onkeydown="KeyDownEvents(this,event)" /></td>
					</tr>
				</table>
			</div>
			<%@ include file="../Footer_mb.jsp"%>
		</h:form>

	</f:view>

</body>
</html>

<!--CHANGED WITH CBC TOOL-->
