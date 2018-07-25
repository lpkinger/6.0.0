Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProductUnit', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','scm.product.ProductUnit','core.grid.Panel2','core.toolbar.Toolbar',
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
    				me.FormUtil.onAdd('addProductUnit', '新增物料单位换算', 'jsps/scm/product/productUnit.jsp');
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid');
					var unit = Ext.getCmp('pr_unit').value;
				    Ext.Array.each(grid.store.data.items, function(item){
				    	var other = item.data['pu_otherunit'];
				    	if(other != null && other != ''){
				    		item.set('pu_mateunit', unit);
				    	}
					});
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
    		'textfield[name=pr_code]':{
    			change: function(){
    				var id = Ext.getCmp('pr_id').value;
    				if(id != null & id != ''){
    					this.getUnitStore('pu_prid=' + id);
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
	getUnitStore: function(condition){
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/singleGridPanel.action",
        	params: {
        		caller: "Product!Unit",
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
        			return;
        		} else {
        			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
        			if(data.length > 0){
            			var unit = Ext.getCmp('pr_unit').value;
            			Ext.each(data, function(){
            				this.pu_mateunit = unit;
            			});
            			grid.store.loadData(data);
            		}
        		}
        	}
        });
	}	
});