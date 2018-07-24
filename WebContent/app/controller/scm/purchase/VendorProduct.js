Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.VendorProduct', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','scm.purchase.VendorProduct','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload','core.button.Scan',
      			'core.button.Close','core.button.Update','core.button.DeleteDetail',
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
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addVendorProduct', '新增供应商物料', 'jsps/scm/purchase/vendorProduct.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'dbfindtrigger[name=ve_code]':{
    			afterrender:function(f){
    				f.setEditable(false);
    				var id = Ext.getCmp('ve_id').value;
					if (id != null & id != '') {
						this.getStore('pv_vendid=' + id);
					}
    			},
    			aftertrigger : function() {
					var id = Ext.getCmp('ve_id').value;
					if (id != null & id != '') {
						//this.getStore('pv_vendid=' + id);
						if(window.location.href.indexOf('?')>0){
							var url = window.location.href;
							if(url.indexOf("formCondition")>0&&url.indexOf("gridCondition")>0){
								var formcondition = url.substring(url.indexOf("formCondition"),url.indexOf("&"));
								var newformcondition = "formCondition=ve_idIS"+id;
								var str = url.replace(formcondition,newformcondition);
								var gridcondition = url.substring(url.indexOf("gridCondition"),url.indexOf("&",url.indexOf("gridCondition")));
								var newgridcondition = "gridCondition=pv_vendidIS"+id;
								str = str.replace(gridcondition,newgridcondition);
								window.location.href = str;
							}else{
								window.location.href=window.location.href+'&formCondition=ve_idIS' +id+ '&gridCondition=pv_vendidIS' +id;
							}
						}
					}
				}
    		}
    		/*'textfield[name=ve_code]':{
    			change: function(field){
    				if(field.value != null && field.value != ''){
    					var grid = Ext.getCmp('grid');
    					var id = Ext.getCmp('ve_id').value;
    					var insert = true;//是否需要加入到grid
    					var num = 0;//grid的有效数据有多少行
    					Ext.each(grid.getStore().data.items, function(){
    						if(this.data['pv_vendid'] != null && this.data['pv_vendid'] != '0'){
    							num++;
    							if(this.data['pv_vendid'] == id){
        							insert = false;
        						}
    						}
    					});
    					if(num == grid.getStore().data.items.length){
    						me.GridUtil.add10EmptyItems(grid);
    					}
    					if(insert){
    						grid.getStore().data.items[num].set('pv_vendid', id);
    						grid.getStore().data.items[num].set('pv_vendcode', Ext.getCmp('ve_code').value);
    					}
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
				caller : "Vendor!ProductVendor",
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