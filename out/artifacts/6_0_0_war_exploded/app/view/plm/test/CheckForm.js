Ext.define('erp.view.plm.test.CheckForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpCheckPanel',
	id: 'form', 
	//title: '<font color=#a1a1a1;>'+ $I18N.common.form.title +'</font><font id="errMsg" color=red style="float:right;padding-right:50px;"></font>',
    region: 'north',
    frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	saveUrl: '',
	updateUrl: '',
	deleteUrl: '',
	auditUrl: '',
	resAuditUrl: '',
	submitUrl: '',
	resSubmitUrl: '',
	bannedUrl: '',
	resBannedUrl: '',
	getIdUrl: '',
	keyField: '',
	codeField: '',
	statusField: '',
	initComponent : function(){ 
		formCondition =formCondition+getUrlParam('formCondition');
    	formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
    	var param = {caller: caller, condition: formCondition};
    	this.FormUtil.getItemsAndButtons(this, 'plm/CheckFormItemsAndData.action', param);//
		this.callParent(arguments);
	}
});