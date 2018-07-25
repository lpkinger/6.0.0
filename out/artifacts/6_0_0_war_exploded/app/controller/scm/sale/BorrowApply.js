Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.BorrowApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.BorrowApply','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResAudit',
  			'core.button.DeleteDetail','core.button.ResSubmit','core.button.TurnBorrow',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'field[name=ba_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=ba_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true, returndate = Ext.getCmp('ba_returndate').value;
					if(Ext.Date.format(returndate, 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')){
						showError('归还日期小于系统当前日期');
						return;
					}
					Ext.Array.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['bad_prodcode'])){
	    		   			if(Ext.isEmpty(item.data['bad_returndate'])){
	    		   				item.set('bad_returndate', returndate);
	    		   			} else {
	    		   				if (Ext.Date.format(item.data['bad_returndate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['bad_detno'] + '行的归还日期小于系统当前日期');
				                    return;
				               	}
	    		   			}
	    		   		}
					});
					if(bool){
						if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
							me.BaseUtil.getRandomNumber();//自动添加编号
						}
						this.FormUtil.beforeSave(this);
					}
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ba_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true, returndate = Ext.getCmp('ba_returndate').value;
					if(Ext.Date.format(returndate, 'Ymd') < Ext.Date.format(new Date(), 'Ymd')){
						showError('归还日期小于系统当前日期');
						return;
					}
					Ext.Array.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['bad_prodcode'])){
	    		   			if(Ext.isEmpty(item.data['bad_returndate'])){
	    		   				item.set('bad_returndate', returndate);
	    		   			} else {
	    		   				if (Ext.Date.format(item.data['bad_returndate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['bad_detno'] + '行的归还日期小于系统当前日期');
				                    return;
				               	}
	    		   			}
	    		   		}
					});
					if(bool){
						this.FormUtil.onUpdate(this);
					}
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addBorrowApply', '新增借货申请单', 'jsps/scm/sale/borrowApply.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ba_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true, returndate = Ext.getCmp('ba_returndate').value;
					if(Ext.Date.format(returndate, 'Ymd') < Ext.Date.format(new Date(), 'Ymd')){
						showError('归还日期小于系统当前日期');
						return;
					}
					Ext.Array.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['bad_prodcode'])){
	    		   			if(Ext.isEmpty(item.data['bad_returndate'])){
	    		   				item.set('bad_returndate', returndate);
	    		   			} else {
	    		   				if (Ext.Date.format(item.data['bad_returndate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['bad_detno'] + '行的归还日期小于系统当前日期');
				                    return;
				               	}
	    		   			}
	    		   		}
					});
					if(bool){
						me.FormUtil.onSubmit(Ext.getCmp('ba_id').value);
					}
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ba_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ba_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ba_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true;
					if(Ext.Date.format(Ext.getCmp('ba_returndate').value, 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')){
						showError('归还日期小于系统当前日期');
						return;
					}
					Ext.Array.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['bad_prodcode'])){
	    		   			if (Ext.Date.format(item.data['bad_returndate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
			                    bool = false;
			                    showError('明细表第' + item.data['bad_detno'] + '行的归还日期小于系统当前日期');
			                    return;
			               	}
	    		   		}
					});
					if(bool){
						me.FormUtil.onAudit(Ext.getCmp('ba_id').value);
					}
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ba_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ba_id').value);
				}
			},
			'erpPrintButton': {
				click:function(btn){
					var reportName="BorrowApply";
					var condition='{BorrowApply.ba_id}='+Ext.getCmp('ba_id').value+'';
					var id=Ext.getCmp('ba_id').value;
					me.FormUtil.onwindowsPrint(id,reportName,condition);
				}
			},
			'erpTurnBorrowButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('ba_statuscode');
    				if(status && status.value != 'AUDITED' && status.value != 'PARTOUT'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.turn('BorrowApply!ToProdBorrow!Deal', 'bad_baid=' + Ext.getCmp('ba_id').value +' and nvl(bad_yqty,0) < bad_qty', 'scm/sale/turnProdBorrow.action');
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
	turn: function(nCaller, condition, url){
    	var win = new Ext.window.Window({
	    	id : 'win',
			    height: "100%",
			    width: "80%",
			    maximizable : true,
				buttonAlign : 'center',
				layout : 'anchor',
			    items: [{
			    	  tag : 'iframe',
			    	  frame : true,
			    	  anchor : '100% 100%',
			    	  layout : 'fit',
			    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller 
			    	  	+ "&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			    }],
			    buttons : [{
			    	name: 'confirm',
			    	text : $I18N.common.button.erpConfirmButton,
			    	iconCls: 'x-button-icon-confirm',
			    	cls: 'x-btn-gray',
			    	listeners: {
				    		buffer: 500,
				    		click: function(btn) {
				    			var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
	   				    		btn.setDisabled(true);
	   				    		grid.updateAction(url);
	   				    		window.location.reload();
				    		}
				    	}
			    }, {
			    	text : $I18N.common.button.erpCloseButton,
			    	iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler : function(){
			    		Ext.getCmp('win').close();
			    	}
			    }]
			});
			win.show();
    }
});