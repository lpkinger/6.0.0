Ext.define('erp.view.scm.product.ProductApprovals.prodAppDetail',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.prodAppDetail',
	id: 'prodAppDetail', 
	columnWidth:1, 
	frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	/*tools:[{xtype:'ProdAppDetailsaveButton',id:'prodAppDetailsave'}],//
*/	fieldDefaults : {
		margin : '2 2 2 2',
		fieldStyle : "background:#FFFAFA;color:#515151;",
		focusCls: 'x-form-field-cir-focus',
		labelAlign : "right",
		msgTarget: 'side',
		blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	saveUrl: '',
	updateUrl: '',
	deleteUrl: '',
	auditUrl: '',
	resAuditUrl: '',
	submitUrl: '',
	resSubmitUrl: '',
	bannedUrl: '',
	resBannedUrl: '',
	postUrl:'',
	printUrl: '',
	getIdUrl: '',
	keyField: '',
	codeField: '',
	statusField: '',
	params: null,
	caller: 'prodAppDetail',
	formCondition:null,
	Contextvalue:null,
	LastValue:null,
	enableTools: true,
	enableKeyEvents: true,
	_noc: 0,
	initComponent : function(){ 
		formCondition = getUrlParam('formCondition');//从url解析参数
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		//集团版
		var master=getUrlParam('newMaster');
		var param = {caller: this.caller || caller, condition: this.formCondition || formCondition, _noc: (getUrlParam('_noc') || this._noc)};
		if(master){
			param.master=master;
		}
		this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', this.params || param);//从后台拿到formpanel的items
		this.callParent(arguments);
	}
});