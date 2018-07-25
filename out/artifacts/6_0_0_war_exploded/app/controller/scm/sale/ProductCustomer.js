Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.ProductCustomer', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'scm.sale.ProductCustomer','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload','core.button.Scan',
  			'core.button.Close','core.button.Update','core.button.DeleteDetail',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField',
  			'core.trigger.AutoCodeTrigger'
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
					var grid = Ext.getCmp("grid");
					var me=this;
					var items = grid.getStore().data.items;
					me.BaseUtil.getSetting('Sale', 'allowCustprodcode', function(bool) {
    					if(!bool) { 
    						var flag=true;
    						for(var i=0;i<items.length;i++){
    							for(var j=i+1;j<items.length-1;j++){
    								if(items[i].data.pc_custcode!=""&&items[i].data.pc_custcode!=null&&items[j].data.pc_custcode!=""&&items[j].data.pc_custcode!=null&&items[i].data.pc_custcode==items[j].data.pc_custcode){
    									showError("同产品同客户出现重复信息：行号"+items[i].data.pc_detno+","+items[j].data.pc_detno);
    									flag=false
    									break;
    								}
    							}
    						}
    						if(flag){
    							me.FormUtil.onUpdate(me);	
    						}
    					}else{
    						me.FormUtil.onUpdate(me);	
    					};    					
    		        },false);		
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addProductCustomer', '新增物料客户', 'jsps/scm/sale/productCustomer.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'dbfindtrigger[name=pr_code]':{
				afterrender: function(t){
    				t.setEditable(false);
				},
				aftertrigger : function() {
					var id = Ext.getCmp('pr_id').value;
					var form=Ext.getCmp('form');
    				var grid =Ext.getCmp('grid');
					if (id != null & id != '') {
						var formCondition = form.keyField + "IS" + id ;
						var gridCondition = grid.mainField + "IS" + id;;
	    				window.location.href = basePath+'jsps/scm/sale/productCustomer.jsp'+ '?formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
						//this.getStore('pc_prodid=' + id);
					}
				}
				/*change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var id = Ext.getCmp('pr_id').value;
						me.insertnum = 0;//grid的有效数据有多少行
						me.allowinsert = true;
						Ext.each(grid.getStore().data.items, function(){
							if(this.data['pc_prodid'] != null && this.data['pc_prodid'] != '0'){
								me.insertnum++;
								if(this.data['pc_prodid'] == id){
	    							me.allowinsert = false;
	    						}
							}
						});
						if(me.insertnum == grid.getStore().data.items.length){
							me.GridUtil.add10EmptyItems(grid);
						}
						if(me.allowinsert){
							grid.getStore().data.items[me.insertnum].set('pc_prodid', id);
							//grid.getStore().data.items[me.insertnum].set('pc_custprodcode', Ext.getCmp('pr_code').value);
						}
					}
				}*/
			}
			/*'textfield[name=pr_detail]':{
				change: function(){
					if(me.allowinsert){
						var grid = Ext.getCmp('grid');
						grid.getStore().data.items[me.insertnum].set('pc_custproddetail', Ext.getCmp('pr_detail').value);
					}
				}
			},
			'textfield[name=pr_spec]':{
				change: function(){
					if(me.allowinsert){
						var grid = Ext.getCmp('grid');
						grid.getStore().data.items[me.insertnum].set('pc_custprodspec', Ext.getCmp('pr_spec').value);
					}
				}
			},
			'textfield[name=pr_unit]':{
				change: function(){
					if(me.allowinsert){
						var grid = Ext.getCmp('grid');
						grid.getStore().data.items[me.insertnum].set('pc_custprodunit', Ext.getCmp('pr_unit').value);
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
		grid.setLoading(true);// loading...
		Ext.Ajax.request({// 拿到grid的columns
			url : basePath + "common/singleGridPanel.action",
			params : {
				caller : "Product!ProductCustomer",
				condition : condition
			},
			method : 'post',
			callback : function(options, success, response) {
				grid.setLoading(false);
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