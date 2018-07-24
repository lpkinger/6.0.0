Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.Estimate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.arp.Estimate','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
      			'core.button.Post','core.button.ResPost','core.trigger.CateTreeDbfindTrigger',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.AutoInvoice',
      			'core.form.MonthDateField'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				//保存之前的一些前台的逻辑判定
    				this.beforeSaveGoodsSend();
    			}
    		},
    		'field[name=es_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=es_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('es_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				var form = Ext.getCmp('form');
    				var title = '新增';
    				if(form){
    					if(form.title){
    						title = title+form.title;
    					}
    				}
    				me.FormUtil.onAdd('addEstimate', title, 'jsps/fa/arp/estimate.jsp?whoami='+caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
    				if(auditStatus && auditStatus.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('es_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
    				if(auditStatus && auditStatus.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('es_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
    				if(auditStatus && auditStatus.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('es_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
    				if(auditStatus && auditStatus.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('es_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('es_id').value);
    			}
    		},
    		'erpPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statusCode);
    				if(status && status.value == 'UNPOST'){
    					btn.show();
    				}else{
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onPost(Ext.getCmp('es_id').value);
    			}
    		},
    		'erpResPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statusCode);
    				if(status && status.value == 'POSTED'){
    					btn.show();
    				}else{
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('es_id').value);
    			}
    		}
    	});
    }, 
    
    
    //此CALLER为  应收发票维护界面  修改单据需要把明细行中开票数据还原
//	//在此做还原操作

    onGridItemClick: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('grid');
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSaveGoodsSend: function(){
		//保存ARBill
		/*if(bool)*/
			this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
		var bool = true;
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('esd_esid',Ext.getCmp('es_id').value);
		});
        Ext.each(items, function(item) {
            if (!Ext.isEmpty(item.data['esd_picode'])) {
                if (!Ext.isEmpty(item.data['esd_piclass'])) {
                	if(item.data['esd_piclass'] == '委外验收单' || item.data['esd_piclass'] == '采购验收单'){
                		if(item.data['esd_qty'] < 0){
                			 bool = false;
                			 showError('明细表第' + item.data['esd_detno'] + '行的来源类型为' + item.data['esd_piclass'] + ',数量不能填写负数');
                             return;
                		}
                	} else if (item.data['esd_piclass'] == '委外验退单' || item.data['esd_piclass'] == '采购验退单'){
                		if(item.data['esd_qty'] > 0){
	               			 bool = false;
	               			 showError('明细表第' + item.data['esd_detno'] + '行的来源类型为' + item.data['esd_piclass'] + ',数量不能填写正数');
	                         return;
                		}
                	}
               	}
            }
        });
		//保存
		if(bool)
			this.FormUtil.onUpdate(this);
	}
});