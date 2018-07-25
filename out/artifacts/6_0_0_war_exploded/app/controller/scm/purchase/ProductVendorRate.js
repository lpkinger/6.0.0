Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.ProductVendorRate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','scm.purchase.ProductVendor','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload',
      			'core.button.Close','core.button.DeleteDetail','core.button.LoadVendor',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
    	me.insertnum = 0;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},   		
    	/*	'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},*/
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'textfield[name=pr_code]':{
    			change: function(field){
    				/*
    				if(field.value != null && field.value != ''){
    					var grid = Ext.getCmp('grid');
    					var id = Ext.getCmp('pr_id').value;
    					me.insertnum = 0;//grid的有效数据有多少行
    					me.allowinsert = true;
    					Ext.each(grid.getStore().data.items, function(){
    						if(this.data['pv_prodid'] != null && this.data['pv_prodid'] != '0'){
    							me.insertnum++;
    							if(this.data['pv_prodid'] == id){
        							me.allowinsert = false;
        						}
    						}
    					});
    					if(me.insertnum == grid.getStore().data.items.length){
    						me.GridUtil.add10EmptyItems(grid);
    					}
    					if(me.allowinsert){
    						grid.getStore().data.items[me.insertnum].set('pv_prodid', id);
    						grid.getStore().data.items[me.insertnum].set('pv_vendprodcode', Ext.getCmp('pr_code').value);
    					}
    				}
    				*/ 
    			}
    		},
    		'field[name=pr_id]': {
    			change: function(f){
    				if(f.value != null && f.value != ''){
    					window.location.href=basePath+'jsps/scm/purchase/productVendorRate.jsp?whoami=SetProductVendorRate&formCondition=pr_idIS'+f.value+'&gridCondition=pr_idIS'+f.value;
            		} 
    			}
    		},
    		'textfield[name=pr_detail]':{
    			change: function(){
    				if(me.allowinsert){
    					var grid = Ext.getCmp('grid');
						grid.getStore().data.items[me.insertnum].set('pv_vendproddetail', Ext.getCmp('pr_detail').value);
					}
    			}
    		},
    		'textfield[name=pr_spec]':{
    			change: function(){
    				if(me.allowinsert){
    					var grid = Ext.getCmp('grid');
						grid.getStore().data.items[me.insertnum].set('pv_vendprodspec', Ext.getCmp('pr_spec').value);
					}
    			}
    		},
    		'textfield[name=pr_unit]':{
    			change: function(){
    				if(me.allowinsert){
    					var grid = Ext.getCmp('grid');
						grid.getStore().data.items[me.insertnum].set('pv_vendprodunit', Ext.getCmp('pr_unit').value);
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
	}
});