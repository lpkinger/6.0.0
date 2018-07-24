Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.ProductVendor', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','scm.purchase.ProductVendor','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload','core.button.Scan',
      			'core.button.Close','core.button.Update','core.button.DeleteDetail',
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
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProductVendor', '新增物料供应商', 'jsps/scm/purchase/productVendor.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'dbfindtrigger[name=pr_code]':{
    			afterrender:function(f){ 
    				f.setEditable(false);
    				var id = Ext.getCmp('pr_id').value;
					if (id != null & id != '') {
						this.getStore('pv_prodid=' + id);
					}
    			},
				aftertrigger : function() {
					var id = Ext.getCmp('pr_id').value;
					if (id != null & id != '') {
	    				window.location.href = basePath+'jsps/scm/purchase/productVendor.jsp?formCondition=pr_idIS' +id
	    				+ '&gridCondition=pv_prodidIS' +id;
						//this.getStore('pv_prodid=' + id);
					}
				}
    		}
    		/*'textfield[name=pr_code]':{
    			change: function(field){
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
    		}*/
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getStore : function(condition) {
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		me.BaseUtil.getActiveTab().setLoading(true);// loading...
		Ext.Ajax.request({// 拿到grid的columns
			url : basePath + "common/singleGridPanel.action",
			params : {
				caller : "Product!ProductVendor",
				condition : condition
			},
			method : 'post',
			callback : function(options, success, response) {
				me.BaseUtil.getActiveTab().setLoading(false);
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				var data = [];
				if (!res.data || res.data.length == 2) {
					me.GridUtil.add10EmptyItems(grid);
				} else {
					data = Ext.decode(res.data.replace(/,}/g, '}').replace(
							/,]/g, ']'));
					if (data.length > 0) {
						grid.store.loadData(data);
					}
				}
			}
		});
	}
});