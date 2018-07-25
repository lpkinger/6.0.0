Ext.QuickTips.init();
Ext.define('erp.controller.crm.customermgr.development.Contact', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'crm.customermgr.development.Contact','core.form.Panel','core.grid.Panel2','core.form.FileField','core.form.MultiField',
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
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					var cuid = Ext.getCmp('cu_id').value;
					var i=0;
				    Ext.Array.each(items, function(item){
				    	item.set('ct_cuid', cuid);
				    	if (item.data['ct_remark'] == '是') {
							i++;
						}
					});
					if (i > 1) {
						showError('默认客户联系人只能选择一个,请重新选择!');
						return;
					}
				    if(i == 0){
				    	Ext.Msg.alert("提示","请选择默认客户联系人!");
				    	return;
				    }
					this.FormUtil.onUpdate(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ct_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid'),items = grid.store.data.items;
					var i = 0;
					Ext.each(items, function(item){
						if (item.data['ct_remark'] == '是') {
							i++;
						}
					});
					if (i > 1) {
						showError('默认客户联系人只能选择一个,请重新选择!');
						return;
					}
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addContact', '新增联系人', 'jsps/crm/customermgr/development/contact.jsp');
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
			},
			'dbfindtrigger[name=cu_code]':{
    			afterrender: function(t){
    				t.setEditable(false);
    				if (t.fieldConfig == 'PT') {
    					t.dbBaseCondition = "cd_sellercode='" + em_code + "'";
    				}
    			},
    			aftertrigger: function(btn){
    				var id = Ext.getCmp('cu_id').value;
    				var form=Ext.getCmp('form');
    				var grid =Ext.getCmp('grid');
    				if(id != null & id != ''){
    					var formCondition = form.keyField + "IS" + id ;
						var gridCondition = grid.mainField + "IS" + id;;
	    				window.location.href = basePath+'jsps/crm/customermgr/development/contact.jsp'+ '?formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
    				}
    			}
    		},
    		'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			}
    	});
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
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