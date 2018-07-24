Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.Currencys', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.ars.Currencys','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close',
			'core.button.Update','core.button.Delete',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
			'core.button.Scan','core.button.Banned','core.button.ResBanned','core.button.Sync'
	],
	init:function(){
		var me = this;
		this.control({ 
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					this.FormUtil.beforeSave(this);
				}
			},
			'erpSyncButton': {
    			afterrender: function(btn){
    				btn.autoClearCache = true;
    			}
    		},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('cr_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addCurrencys', '新增币别', 'jsps/fa/ars/currencys.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					me.FormUtil.onBanned(Ext.getCmp('cr_id').value);
				}
			},
			'erpResBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'DISABLE'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					me.FormUtil.onResBanned(Ext.getCmp('cr_id').value);
				}
			}/*,
			'erpScanButton': {
    			afterrender: function(btn){
    				btn.urlcondition =  "cr_statuscode<>'BANNED' ";
    			}
    		}*/
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});