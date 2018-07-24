Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.customerUnit', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','scm.sale.CustomerAddress','core.grid.Panel2','core.toolbar.Toolbar',
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
    				me.FormUtil.onAdd('addcustomerUnit', '新增抬头', 'jsps/scm/sale/customerUnit.jsp');
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid');
					var cuid = Ext.getCmp('cu_id').value;
				    Ext.Array.each(grid.store.data.items, function(item){
				    	item.set('cs_cuid', cuid);
					});
				   /* Ext.Array.each(grid.store.data.items, function(item){
				    	if(item.data.ca_remark=='是'){
				    		i++;
				    	}
					});
				    if(i==0){
				    	Ext.Msg.alert("提示","请选择默认送货地址!");
				    	return;
				    }
				    if(i>1){
				    	Ext.Msg.alert("提示","默认送货地址只能选择一个,请重新选择!");
				    	return;
				    }*/
					this.FormUtil.onUpdate(this);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},    		
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'dbfindtrigger[name=cu_code]':{
    			aftertrigger: function(){
    				var id = Ext.getCmp('cu_id').value;
    				if(id != null & id != ''){
    					this.getAddressStore('cs_cuid=' + id);
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
	getAddressStore: function(condition){
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/singleGridPanel.action",
        	params: {
        		caller: "customerUnit",
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
        	}
        });
	}	
});