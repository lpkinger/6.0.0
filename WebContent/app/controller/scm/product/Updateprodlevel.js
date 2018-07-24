Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.Updateprodlevel', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
	'scm.product.Updateprodlevel','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
	'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
	'core.button.Update','core.button.Delete','core.form.YnField',
	'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
	'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.Updateprodlevel',
	'core.form.FileField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('cp_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addUpdateprodlevel', '新增更新物料等级', 'jsps/scm/product/Updateprodlevel.jsp');
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('cp_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('cp_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('cp_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cp_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('cp_id').value);
				}
			},
			'erpUpdateprodlevelButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('cp_statuscode');
					if(status && status.value == 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					var dbwin = new Ext.window.Window({
   			    		id : 'dbwin',
	   				    title: '查找',
	   				    height: "100%",
	   				    width: "80%",
	   				    maximizable : true,
	   					buttonAlign : 'center',
	   					layout : 'anchor',
	   				    items: [],
	   				    buttons : [{
	   				    	text : '确  认',
	   				    	iconCls: 'x-button-icon-save',
	   				    	cls: 'x-btn-gray',
	   				    	handler : function(){
	   				    		var contentwindow = Ext.getCmp('dbwin').body.dom.getElementsByTagName('iframe')[0].contentWindow;
	   				    		var grid = contentwindow.Ext.getCmp('batchDealGridPanel');
	   				    		var data = grid.getMultiSelected();
	   				    		var singledata;
	   				    		var detailgrid =Ext.getCmp('grid');
	   				    		var count = detailgrid.store.data.items.length;
	   				    		var length = count;
	   				    		var m=0;
	   				    		for(i=0;i<data.length;i++){
	   				    			dataLength = detailgrid.store.data.length;
	   				    			detailgrid.store.insert(dataLength+1,{});
	   				    			detailgrid.store.data.items[dataLength].set(detailgrid.columns[0].dataIndex,dataLength+1);//明细行自动编号
	   				    			singledata = data[i];
	   				    			singledata = singledata.data;
	   				    			if(i==0){
	   				    				for(j=0;j<dataLength+1;j++){
		   				    				if(detailgrid.store.data.items[j].data.cd_prodcode ==''||detailgrid.store.data.items[j].data.cd_prodcode ==null){
		   				    					detailgrid.store.data.items[j].set('cd_prodcode',singledata.bd_soncode);
		   		   				    			detailgrid.store.data.items[j].set('cd_prodname',singledata.bd_sonname);
		   		   				    			detailgrid.store.data.items[j].set('cd_prodpesc',singledata.bd_sonspec);
		   		   				    			detailgrid.store.data.items[j].set('cd_produnit',singledata.bd_unit);
		   		   				    			detailgrid.store.data.items[j].set('cd_orilevel',singledata.pr_level);
		   		   				    			m=1;
		   		   				    			break;
		   				    				}
		   				    			}
	   				    			}
	   				    			if(m==1){
	   				    				detailgrid.store.data.items[j].set('cd_prodcode',singledata.bd_soncode);
		   				    			detailgrid.store.data.items[j].set('cd_prodname',singledata.bd_sonname);
		   				    			detailgrid.store.data.items[j].set('cd_prodpesc',singledata.bd_sonspec);
		   				    			detailgrid.store.data.items[j].set('cd_produnit',singledata.bd_unit);
		   				    			detailgrid.store.data.items[j].set('cd_orilevel',singledata.pr_level);
		   				    			j++;
	   				    			}
	   				    			if(m==0){
	   				    				detailgrid.store.data.items[length].set('cd_prodcode',singledata.bd_soncode);
		   				    			detailgrid.store.data.items[length].set('cd_prodname',singledata.bd_sonname);
		   				    			detailgrid.store.data.items[length].set('cd_prodpesc',singledata.bd_sonspec);
		   				    			detailgrid.store.data.items[length].set('cd_produnit',singledata.bd_unit);
		   				    			detailgrid.store.data.items[length].set('cd_orilevel',singledata.pr_level);
		   				    			length++;
	   				    			}
	   				    		}
	   				    		Ext.getCmp('dbwin').close();
	   				    	}
	   				    }]
	   				});
					dbwin.add({
			    		    tag : 'iframe',
			    		    frame : true,
			    		    anchor : '100% 100%',
			    		    layout : 'fit',
			    		    html : '<iframe id="iframe_updateprodlevel" src="'+basePath+'jsps/common/batchlevel.jsp?whoami=Updateprodlevel!check" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			    		});
	   				dbwin.show();
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