Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.VendorArp', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.arp.VendorArp','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close',
			'core.button.Update','core.button.Delete',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
			'core.button.Scan','core.button.Banned','core.button.ResBanned'
	],
	init:function(){
		var me = this;
		this.control({ 
			'erpSaveButton': {
				click: function(btn){/*
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					this.FormUtil.beforeSave(this);
				*/}
			},
			'erpDeleteButton' : {
				click: function(btn){
//					me.FormUtil.onDelete(Ext.getCmp('cr_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){/*
					me.FormUtil.onAdd('addCurrencys', '新增币别', 'jsps/fa/ars/currencys.jsp');
				*/}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}/*,
			'erpBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp("cr_id").value;
//					console.log("crid = "+crid);

					me.FormUtil.onBanned(crid);
				}
			},'erpResBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'BANNED'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp("cr_id").value;
					me.FormUtil.onResBanned(crid);
				}
			}*//*,
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