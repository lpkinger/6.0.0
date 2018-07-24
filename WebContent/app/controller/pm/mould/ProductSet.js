Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.ProductSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.mould.ProductSet','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit', 'core.button.VendReturn', 'core.button.CustReturn',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'      
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('ps_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    			},
    			itemclick: function(selModel, record){
    				if(record.data.psd_id != 0 && record.data.psd_id != null && record.data.psd_id != ''){
    					var btn = Ext.getCmp('updatereturnqty');
						btn && btn.setDisabled(false);
    				}
    				this.onGridItemClick(selModel, record);
    			}
			},
			/**
    		 * 更改返还数量
    		 */
    		'#updatereturnqty': {
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    me.UpdateReturnqty(record);
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ps_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('ps_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addProductSet', '新增模具资料', 'jsps/pm/mould/productSet.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ps_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ps_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ps_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ps_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ps_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ps_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ps_statuscode'),
    					vendstatus = Ext.getCmp('ps_vendreturnstatus'),
    					custstatus = Ext.getCmp('ps_custreturnstatus');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(vendstatus && vendstatus.value != '未返还'){
    					btn.hide();
    				}
    				if(custstatus && custstatus.value != '未返还'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ps_id').value);
    			}
    		},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('ps_id').value);
				}
			}
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
    	if(!selModel.ownerCt.readOnly){
    		this.GridUtil.onGridItemClick(selModel, record);
    	}
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	UpdateReturnqty:function(record){
		var win = this.window;
		if (!win) {
			win = this.window = this.getWindow(record);
		}
		win.show();
	},
	getWindow : function(record) {
		var me = this;
		return Ext.create('Ext.window.Window',{
			width: 330,
	       	height: 180,
	       	closeAction: 'hide',
	       	cls: 'custom-blue',
	       	title:'<h1>更改物料编号</h1>',
	       	layout: {
	       		type: 'vbox'
	       	},
	       	items:[{
				margin: '5 0 0 5',
				xtype: 'dbfindtrigger',
				fieldLabel: '物料编号',
				id:'psd_prodcode',
				name: 'psd_prodcode',
				readOnly:false,
				allowBlank: false,
				value : record.data.psd_prodcode
			},{
	        	 margin: '5 0 0 5',
	       		 xtype:'numberfield',
	       		 fieldLabel:'返还数量',
	       	     name:'returnqty',
	       	     allowBlank:true,
	       	     id:'returnqty' ,
	       	     value : record.data.psd_returnqty
	       	 }],
	       	 buttonAlign:'center',
	       	 buttons:[{
	 				xtype:'button',
	 				text:'保存',
	 				width:60,
	 				iconCls: 'x-button-icon-save',
	 				handler:function(btn){
	 					var w = btn.up('window');
	 					me.saveReturnqty(w);
	 					w.hide();
	 				}
	 			},{
	 				xtype:'button',
	 				columnWidth:0.1,
	 				text:'关闭',
	 				width:60,
	 				iconCls: 'x-button-icon-close',
	 				margin:'0 0 0 10',
	 				handler:function(btn){
	 					btn.up('window').hide();
	 				}
	 			}]
	        });
	},
	saveReturnqty: function(w) {
		var returnqty = w.down('#returnqty').getValue(),
			prodcode = w.down('#psd_prodcode').getValue(),
			grid = Ext.getCmp('grid'),
			ps_id = Ext.getCmp('ps_id').value,
			record = grid.getSelectionModel().getLastSelected(); 
		if(!returnqty) {
			showError('请先设置返还数量.') ;  
			return;
		} else {
			var dd = {
					psd_id : record.data.psd_id,
					psd_psid : record.data.psd_psid,
					returnqty : returnqty ? returnqty : 0,
					prodcode : prodcode
			};
			Ext.Ajax.request({
				url : basePath +'pm/mould/updateReturnqty.action',
				params : {
					_noc: 1,
					data: unescape(Ext.JSON.encode(dd))
				},
				method : 'post',
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.success){
	    				showMessage('提示', '更新成功!', 1000);
	    				grid.GridUtil.loadNewStore(grid, {caller: 'ProductSet', condition: 'psd_psid=' + ps_id});
		   			} else if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			} else{
		   				saveFailure();
		   			}
				}
			});
		}
	}
});