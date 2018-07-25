Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.VendorBank', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','scm.purchase.VendorBank','core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField', 
     		'core.button.Save','core.button.Upload','core.button.Close','core.button.Update',
     			'core.button.Add','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.BankNameTrigger',
     	],
   init:function(){
	   	var me = this;
	   	me.allowinsert = true;
		this.control({
		   'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addVendorBank', '新增供应商银行资料', 'jsps/scm/purchase/vendorBank.jsp');
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					var veid = Ext.getCmp('ve_id').value;
				    var i=0;
				    Ext.Array.each(items, function(item){
				    	item.set('vpd_veid', veid);
				    	item.set('vpd_vecode', Ext.getCmp('ve_code').value);
				    	if (item.data['vpd_remark'] == '是') {
							i++;
						}
					});
					if (i > 1) {
						showError('默认开户行只能选择一个,请重新选择!');
						return;
					}
				    if(i == 0){
				    	Ext.Msg.alert("提示","请选择默认开户行!");
				    	return;
				    }
					this.FormUtil.onUpdate(this);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid'),items = grid.store.data.items;
					var i = 0;
					Ext.each(items, function(item){
						if (item.data['vpd_remark'] == '是') {
							i++;
						}
					});
					if (i > 1) {
						showError('默认开户行只能选择一个,请重新选择!');
						return;
					}
					this.FormUtil.onUpdate(this);
				}
			},    		
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'dbfindtrigger[name=ve_code]':{
    			aftertrigger: function(){
    				var id = Ext.getCmp('ve_id').value;
    				if(id != null & id != ''){
    					window.location.href = basePath+'jsps/scm/purchase/vendorBank.jsp?formCondition=ve_idIS' +id
	    				+ '&gridCondition=vpd_veidIS' +id;
    				}
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
	getAddressStore: function(condition, ve_bank, ve_bankaccount, ve_bankman, ve_contact, ve_taxrate, ve_currency, ve_code){
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/singleGridPanel.action",
        	params: {
        		caller: "VendorBank",
        		condition: condition
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.BaseUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = [];
        		if(!res.data || res.data.length == 2){
        			me.GridUtil.add10EmptyItems(grid);
        		} else {
        			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
        			if(data.length > 0){
            			grid.store.loadData(data);
            		}
        		}
        		var bool = false;
    			grid.store.each(function(item){
    				if(item.get('vpd_bank') == ve_bank) {
    					bool = true;
    				}
    				if(item.get('vpd_bankaccount') == ve_bankaccount) {
    					bool = true;
    				}
    				if(item.get('vpd_bankman') == ve_bankman) {
    					bool = true;
    				}
    				if(item.get('vpd_contact') == ve_contact) {
    					bool = true;
    				}
    				if(item.get('vpd_currency') == ve_currency) {
    					bool = true;
    				}
    				if(item.get('vpd_taxrate') == ve_taxrate) {
    					bool = true;
    				}
    				if(item.get('vpd_bankaddress') == ve_bankaddress) {
    					bool = true;
    				}
    			});
    			if(!bool) {
    				var items = grid.store.data.items, item = null;
    				for(var i in items) {
    					item = items[i];
    					if(Ext.isEmpty(item.get('vpd_bank'))) {
    						item.set('vpd_bank', ve_bank);
    						item.set('vpd_bankaccount', ve_bankaccount);
    						item.set('vpd_bankman', ve_bankman);
    						item.set('vpd_contact', ve_contact);
    						item.set('vpd_currency', ve_currency);
    						item.set('vpd_taxrate', ve_taxrate);
    						item.set('vpd_vecode', ve_code);
    						item.set('vpd_bankaddress', ve_bankaddress);
    						break;
    					}
    				}
    			}
        	}
        });
	}	
});