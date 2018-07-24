Ext.QuickTips.init();
Ext.define('erp.controller.drp.distribution.PricePolicy', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'drp.distribution.PricePolicy','core.form.Panel',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.form.YnField','core.trigger.DbfindTrigger','core.button.Scan'
    	],
    	init:function(){
	var me = this;
	me.allowinsert = true;
	this.control({
		'erpGridPanel2': { 
			itemclick: this.onGridItemClick
		},
		'erpSaveButton': {
			click: function(btn){
				var form = me.getForm(btn);
				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
					me.BaseUtil.getRandomNumber();//自动添加编号
				}
				//保存之前的一些前台的逻辑判定
				this.beforeSave();
			}
		},
		'erpDeleteButton' : {
			click: function(btn){
				me.FormUtil.onDelete(Ext.getCmp('pp_id').value);
			}
		},
		'erpUpdateButton': {
			afterrender: function(btn){
				var status = Ext.getCmp('pp_statuscode');
				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
					btn.hide();
				}
			},
			click: function(btn){
				me.beforeUpdate();
			}
		},
		'erpAddButton': {
			click: function(){
				me.FormUtil.onAdd('addPricePolicy', '新增终端日常促销申请', 'jsps/drp/distribution/pricePolicy.jsp');
			}
		},
		'erpCloseButton': {
			click: function(btn){
				me.FormUtil.beforeClose(me);
			}
		},
        'erpSubmitButton': {
			afterrender: function(btn){
				var status = Ext.getCmp('pp_statuscode');
				if(status && status.value != 'ENTERING'){
					btn.hide();
				}
			},
			click: function(btn){
				me.FormUtil.onSubmit(Ext.getCmp('pp_id').value);
			}
		},
		'erpResSubmitButton': {
			afterrender: function(btn){
				var status = Ext.getCmp('pp_statuscode');
				if(status && status.value != 'COMMITED'){
					btn.hide();
				}
			},
			click: function(btn){
				me.FormUtil.onResSubmit(Ext.getCmp('pp_id').value);
			}
		},
		'erpAuditButton': {
			afterrender: function(btn){
				var status = Ext.getCmp('pp_statuscode');
				if(status && status.value != 'COMMITED'){
					btn.hide();
				}
			},
			click: function(btn){
				me.FormUtil.onAudit(Ext.getCmp('pp_id').value);
			}
		},
		'erpResAuditButton': {
			afterrender: function(btn){
				var status = Ext.getCmp('pp_statuscode');
				if(status && status.value != 'AUDITED'){
					btn.hide();
				}
			},
			click: function(btn){
				me.FormUtil.onResAudit(Ext.getCmp('pp_id').value);
			}
		}
	});
}, 
onGridItemClick: function(selModel, record){//grid行选择
	this.GridUtil.onGridItemClick(selModel, record);
},
getForm: function(btn){
	return btn.ownerCt.ownerCt;
},

beforeSave: function(){
	var bool = true;
	if (bool) {
	    this.FormUtil.beforeSave(this);
	}
},
beforeUpdate: function(){
	var bool = true;
	if (bool) {
		this.FormUtil.onUpdate(this);
	}
}
});