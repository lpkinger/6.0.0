Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProductDescription', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.product.ProductDescription','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Upload','core.button.Close','core.button.Update',
  				'core.button.Add','core.button.DeleteDetail',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},  
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProductDescription', '新增物料品名规格额外说明', 'jsps/scm/product/productDescription.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'textfield[name=pr_code]': {
    			change: function(){
    				var id = Ext.getCmp('pr_id').value;
    				if(id != null & id != ''){
    					this.getDescriptionStore('pd_prid=' + id);
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
	getDescriptionStore: function(condition){
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/singleGridPanel.action",
        	params: {
        		caller: "Product!Description",
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
        			grid.store.loadData(data);
        		}
        	}
        });
	}
});