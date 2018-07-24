Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.PurchaseForecast', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.PurchaseForecast','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit','core.button.Flow',
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
    				var form = me.getForm(btn);
    				var bool = true;
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				Ext.each(items, function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['pfd_vendcode'] == null){
    							bool = false;
    							showError('明细表第' + item.data['pfd_detno'] + '行的供应商为空!');return;
    						}
    						if (item.data['pfd_delivery'] != null && item.data['pfd_delivery'] < new Date()) {
                    			bool = false;
                    			showError('明细表第' + item.data['pfd_detno'] + '行的交货日期小于单据当前日期');
                   	 			return;
                			}
    					}
    				});
    				if(bool){
    					this.FormUtil.beforeSave(this);
    				}
    			}
    		},
    		'field[name=pf_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pf_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pf_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var bool = true;
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				Ext.each(items, function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['pfd_vendcode'] == null){
    							bool = false;
    							showError('明细表第' + item.data['pfd_detno'] + '行的供应商为空!');return;
    						}
    						if (item.data['pfd_delivery'] != null && item.data['pfd_delivery'] < new Date()) {
                    			bool = false;
                    			showError('明细表第' + item.data['pfd_detno'] + '行的交货日期小于单据当前日期');
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
    				me.FormUtil.onAdd('addPurchaseForecast', '新增请购预测单', 'jsps/scm/purchase/purchaseForecast.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pf_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var bool = true;
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				Ext.each(items, function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['pfd_vendcode'] == null){
    							bool = false;
    							showError('明细表第' + item.data['pfd_detno'] + '行的供应商为空!');return;
    						}
    						if (item.data['pfd_delivery'] != null && item.data['pfd_delivery'] < new Date()) {
                    			bool = false;
                    			showError('明细表第' + item.data['pfd_detno'] + '行的交货日期小于单据当前日期');
                   	 			return;
                			}
    					}
    				});
    				if(bool){
    					me.FormUtil.onSubmit(Ext.getCmp('pf_id').value);
    				}
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pf_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pf_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pf_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pf_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pf_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pf_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('pf_id').value);
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