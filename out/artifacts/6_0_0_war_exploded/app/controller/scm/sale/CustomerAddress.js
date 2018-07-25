Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.CustomerAddress', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','scm.sale.CustomerAddress','core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField', 
     		'core.button.Save','core.button.Upload','core.button.Close','core.button.Update',
     			'core.button.Add','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
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
    				me.FormUtil.onAdd('addCustomerAddress', '新增客户收货地址', 'jsps/scm/sale/customerAddress.jsp');
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					var cuid = Ext.getCmp('cu_id').value;
					var i=0;
				    Ext.Array.each(items, function(item){
				    	item.set('ca_cuid', cuid);
				    	if (item.data['ca_remark'] == '是') {
							i++;
						}
					});
					if (i > 1) {
						showError('默认收货地址只能选择一个,请重新选择!');
						return;
					}
				    if(i == 0){
				    	Ext.Msg.alert("提示","请选择默认送货地址!");
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
						if (item.data['ca_remark'] == '是') {
							i++;
						}
					});
					if (i > 1) {
						showError('默认收货地址只能选择一个,请重新选择!');
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
	    				window.location.href = basePath+'jsps/scm/sale/customerAddress.jsp'+ '?formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
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
	getAddressStore: function(condition, address, contact, phone, fax, shcustname){
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/singleGridPanel.action",
        	params: {
        		caller: "CustomerAddress",
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
    				if(item.get('ca_address') == address) {
    					bool = true;
    				}
    				if(item.get('ca_person') == contact) {
    					bool = true;
    				}
    				if(item.get('ca_phone') == phone) {
    					bool = true;
    				}
    				if(item.get('ca_fax') == fax) {
    					bool = true;
    				}
    				if(item.get('ca_shcustname') == shcustname) {
    					bool = true;
    				}
    			});
    			if(!bool) {
    				var items = grid.store.data.items, item = null;
    				for(var i in items) {
    					item = items[i];
    					if(Ext.isEmpty(item.get('ca_address'))) {
    						item.set('ca_address', address);
    						item.set('ca_person', contact);
    						item.set('ca_phone', phone);
    						item.set('ca_fax', fax);
    						item.set('ca_shcustname', shcustname);
    						item.set('ca_remark', '是');
    						break;
    					}
    				}
    			}
        	}
        });
	}	
});