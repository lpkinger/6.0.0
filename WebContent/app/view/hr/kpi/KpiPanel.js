/**
 *formpanel自定义样式
 * 供kpi添加明细行单表直接使用
 */
Ext.define('erp.view.hr.kpi.KpiPanel',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpKpiPanel',
	id: 'form', 
	region: 'north',
	frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	cls: 'u-form-default',
	fieldDefaults : {
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
	caller: null,
	formCondition:null,
	Contextvalue:null,
	LastValue:null,
	enableTools: false,
	enableKeyEvents: true,
	_noc: 0,
	initComponent : function(){
		var formCondition = this.formCondition;
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		//集团版
		var master=getUrlParam('newMaster');
		var param = {caller:this.caller, condition:formCondition, _noc: (getUrlParam('_noc') || this._noc)};
		if(master){
			param.master=master;
		}
		this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action',param);//从后台拿到formpanel的items
		this.callParent(arguments);		
	},	
	getDataByField : function(field) {
		var form = this, f = form.child('#' + field);
		return f ? "'" + f.getValue() + "'" : '';
	},
	getYearmonthByField : function(field) {
		var form = this;
		var f = form.child('#' + field),
		v = f ? (Ext.isDate(f.value) ? f.value : Ext.Date.parse(f.value, 'Y-m-d')) : new Date();
		return Ext.Date.format(v, 'Ym');
	}
});