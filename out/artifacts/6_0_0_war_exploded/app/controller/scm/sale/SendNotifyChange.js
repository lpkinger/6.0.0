Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.SendNotifyChange', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.form.Panel','scm.sale.SendNotifyChange','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print',
      		'core.button.ResAudit','core.button.Audit','core.button.Close','core.button.Delete','core.button.Update',
      		'core.button.DeleteDetail','core.button.ResSubmit',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	this.FormUtil = Ext.create('erp.util.FormUtil');
    	this.GridUtil = Ext.create('erp.util.GridUtil');
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'erpFormPanel': {
    			afterload: function(form){
    				var main = getUrlParam('main');
					formCondition = getUrlParam('formCondition');
					if(main&&!formCondition){
						me.FormUtil.autoDbfind(caller, 'sc_sncode', main);
					}
    			}
    		},
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			reconfigure: function(grid, store, columns){
					var detail = getUrlParam('detail');
					gridCondition = getUrlParam('gridCondition');
					if(detail&&!gridCondition){
						var sncode = Ext.getCmp("sc_sncode").value;
						if(!sncode){
							showError("请先选择出货通知!");
							return;
						}
    					detail += " and sn_code = '"+sncode+"'";
						me.GridUtil.autoDbfind(grid, 'scd_snddetno', detail);
					}
				}
    		},
    		'field[name=sc_sncode]': {
				afterrender:function(f){
					f.setFieldStyle({
						'color': 'blue'
	 				});
	 				f.focusCls = 'mail-attach';
	 				var c = Ext.Function.bind(me.openInvoice, me);
	 				Ext.EventManager.on(f.inputEl, {
	 					mousedown : c,
	 					scope: f,
	 					buffer : 100
	 				});
				}
	    	},    		
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true;
    				Ext.Array.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['scd_snddetno'])){
	    		   			if (item.data['scd_qty'] > item.data['scd_oldqty']) {
			                    bool = false;
			                    showError('明细表第' + item.data['scd_detno'] + '行的新数量大于原数量');
			                    return;
			               	}
			               	if(item.data['scd_qty']<item.data['snd_yqty']){
    							showError('明细表第' + item.data['scd_detno'] + '行新数量小于已转数量');
    							bool = false;
    							return;
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
    				me.FormUtil.onDelete(Ext.getCmp('sc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true;
    				Ext.Array.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['scd_snddetno'])){
	    		   			if (item.data['scd_qty'] > item.data['scd_oldqty']) {
			                    bool = false;
			                    showError('明细表第' + item.data['scd_detno'] + '行的新数量大于原数量');
			                    return;
			               	}
			               	if(item.data['scd_qty']<item.data['snd_yqty']){
    							showError('明细表第' + item.data['scd_detno'] + '行新数量小于已转数量');
    							bool = false;
    							return;
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
    				me.FormUtil.onAdd('addSendNotifyChange', '新增出货通知变更单', 'jsps/scm/sale/sendNotifyChange.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['scd_snddetno'])){
	    		   			if (item.data['scd_qty'] > item.data['scd_oldqty']) {
			                    bool = false;
			                    showError('明细表第' + item.data['scd_detno'] + '行的新数量大于原数量');
			                    return;
			               	}
			               	if(item.data['scd_qty']<item.data['snd_yqty']){
    							showError('明细表第' + item.data['scd_detno'] + '行新数量小于已转数量');
    							bool = false;
    							return;
    						}
	    		   		}
					});
    				if(bool){
    					me.FormUtil.onSubmit(Ext.getCmp('sc_id').value);
    				}
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('sc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['scd_snddetno'])){
	    		   			if (item.data['scd_qty'] > item.data['scd_oldqty']) {
			                    bool = false;
			                    showError('明细表第' + item.data['scd_detno'] + '行的新数量大于原数量');
			                    return;
			               	}
			               	if(item.data['scd_qty']<item.data['snd_yqty']){
    							showError('明细表第' + item.data['scd_detno'] + '行新数量小于已转数量');
    							bool = false;
    							return;
    						}
	    		   		}
					});
    				if(bool)
    					me.FormUtil.onAudit(Ext.getCmp('sc_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('sc_id').value);
    			}
    		},
    		/*'erpPrintButton': {
    			click:function(btn){
					var reportName="PURCChange";
					var condition='{PurchaseChange.sc_id}='+Ext.getCmp('pc_id').value+'';
					var id=Ext.getCmp('pc_id').value;
					me.FormUtil.onwindowsPrint(id,reportName,condition);
    			}
    		},*/
    		'dbfindtrigger[name=scd_snddetno]': {
    			afterrender: function(t){
    				t.gridKey = "sc_sncode";
    				t.mappinggirdKey = "sn_code";
    				t.gridErrorMessage = "请先选择出货通知!";
    			}
    		},
    		'dbfindtrigger[name=scd_newpaymentscode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var grid = Ext.getCmp('grid');
    				var column = grid.down('gridcolumn[dataIndex=scd_newpaymentscode]');
    				if(column && column.dbfind && column.dbfind.indexOf('CustomerPayments') > -1) {
    					var record = grid.selModel.getLastSelected();
        				var code = record.data['scd_cuid'];
	    				if(code == null || code == ''){
	    					showError("请先选择订单单号!");
	    					t.setHideTrigger(true);
	    					t.setReadOnly(true);
	    				} else {
	    					t.dbBaseCondition = "cp_cuid='" + code + "'";
	    				}
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
	openInvoice: function(e, el, obj) {
		var f = obj.scope, form = f.ownerCt,
			i = form.down('#sn_id');
		if(i && i.value) {
			url = 'jsps/scm/sale/sendNotify.jsp?formCondition=sn_idIS' + i.value + '&gridCondition=snd_snidIS' + i.value;
			openUrl(url);
		}
	}
});