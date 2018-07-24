Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.Chance', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.chance.Chance','core.form.Panel','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger'
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
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ch_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('ch_statucode');
					if(statu && statu.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ch_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('ch_statucode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ch_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('ch_statucode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ch_id').value);
				}
			},'erpResAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('ch_statucode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ch_id').value);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addChance', '新增商机', 'jsps/crm/chance/chance.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				},
				afterrender:function(btn){
					  var dbfind=getUrlParam('dbfind');
						if(dbfind!=null){
							me.dbfindAndSetValue('form',caller,dbfind.split('=')[0],dbfind);
						}
					}
			}
    	});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	dbfindAndSetValue:function(which,caller,field,condition){//key:sc_chcode,condition:sc_chcode like '%1%'
		Ext.Ajax.request({
				url : basePath + 'common/autoDbfind.action',
				params: {
		   			which: which,
		   			caller: caller,
		   			field: field,
		   			condition: condition
		   		},
		   		async: false,
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var res = new Ext.decode(response.responseText);
		   			if(res.exceptionInfo){
		   				showError(res.exceptionInfo);return;
		   			}
		   			if(!res.data){
		   				return;
		   			}
		   			var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
		   			var form=Ext.getCmp('form');
		   			Ext.Array.each(res.dbfinds,function(db){
		   				if(Ext.getCmp(db.field)){
		   					Ext.getCmp(db.field).setValue(data[0][db.dbGridField]);
		   				}
		   			});
		   		}
		});
	}
});