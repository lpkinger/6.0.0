Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.CustomerProduct', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.sale.CustomerProduct','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload','core.button.Scan',
  			'core.button.Close','core.button.Update','core.button.DeleteDetail','core.button.Sync',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
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
					var grid = Ext.getCmp("grid");
					var items = grid.getStore().data.items; 
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addCustomerProduct', '新增客户物料', 'jsps/scm/sale/customerProduct.jsp');
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
				},
				aftertrigger : function() {
					var id = Ext.getCmp('cu_id').value;
					var form=Ext.getCmp('form');
    				var grid =Ext.getCmp('grid');
					if (id != null & id != '') {
						var formCondition = form.keyField + "IS" + id ;
						var gridCondition = grid.mainField + "IS" + id;;
	    				window.location.href = basePath+'jsps/scm/sale/customerProduct.jsp'+ '?formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
						//this.getStore('pc_custid=' + id);
					}
				}/*,
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var id = Ext.getCmp('cu_id').value;
						var insert = true;//是否需要加入到grid
						var num = 0;//grid的有效数据有多少行
						Ext.each(grid.getStore().data.items, function(){
							if(this.data['pc_custid'] != null && this.data['pc_custid'] != '0'){
								num++;
								if(this.data['pc_custid'] == id){
	    							insert = false;
	    						}
							}
						});
						if(num == grid.getStore().data.items.length){
							me.GridUtil.add10EmptyItems(grid);
						}
						if(insert){
							grid.getStore().data.items[num].set('pc_custid', id);							
						}
					}
				}*/
			}
			
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
				caller : "Customer!ProductCustomer",
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