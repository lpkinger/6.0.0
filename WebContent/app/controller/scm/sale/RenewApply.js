Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.RenewApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.RenewApply','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResAudit',
  			'core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'field[name=ra_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=ra_date]'),
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
						bool = true;
					Ext.Array.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['rad_prodcode'])){
	    		   			if(!Ext.isEmpty(item.data['rad_newreturndate'])){
	    		   				if (Ext.Date.format(item.data['rad_newreturndate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['rad_detno'] + '行的新归还日期小于系统当前日期');
				                    return;
				               	}
	    		   				if (!Ext.isEmpty(item.data['rad_returndate']) && Ext.Date.format(item.data['rad_newreturndate'], 'Y-m-d') < Ext.Date.format(item.data['rad_returndate'], 'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['rad_detno'] + '行的新归还日期小于原归还日期');
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
					me.FormUtil.onDelete(Ext.getCmp('ra_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true;
					Ext.Array.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['rad_prodcode'])){
	    		   			if(!Ext.isEmpty(item.data['rad_newreturndate'])){
	    		   				if (Ext.Date.format(item.data['rad_newreturndate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['rad_detno'] + '行的新归还日期小于系统当前日期');
				                    return;
				               	}
	    		   				if (!Ext.isEmpty(item.data['rad_returndate']) && Ext.Date.format(item.data['rad_newreturndate'], 'Y-m-d') < Ext.Date.format(item.data['rad_returndate'], 'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['rad_detno'] + '行的新归还日期小于原归还日期');
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
					me.FormUtil.onAdd('addRenewApply', '新增续借申请单', 'jsps/scm/sale/renewApply.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ra_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true;
					Ext.Array.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['rad_prodcode'])){
	    		   			if(!Ext.isEmpty(item.data['rad_newreturndate'])){
	    		   				if (Ext.Date.format(item.data['rad_newreturndate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['rad_detno'] + '行的新归还日期小于系统当前日期');
				                    return;
				               	}
	    		   				if (!Ext.isEmpty(item.data['rad_returndate']) && Ext.Date.format(item.data['rad_newreturndate'], 'Y-m-d') < Ext.Date.format(item.data['rad_returndate'], 'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['rad_detno'] + '行的新归还日期小于原归还日期');
				                    return;
				               	}
	    		   			}
	    		   		}
					});
					if(bool){
						me.FormUtil.onSubmit(Ext.getCmp('ra_id').value);
					}
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ra_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ra_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ra_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true;
					Ext.Array.each(items, function(item){
	    		   		if(!Ext.isEmpty(item.data['rad_prodcode'])){
	    		   			if (Ext.Date.format(item.data['rad_newreturndate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')) {
			                    bool = false;
			                    showError('明细表第' + item.data['rad_detno'] + '行的新归还日期小于系统当前日期');
			                    return;
			               	}
	    		   			if (!Ext.isEmpty(item.data['rad_returndate']) && Ext.Date.format(item.data['rad_newreturndate'], 'Y-m-d') < Ext.Date.format(item.data['rad_returndate'], 'Y-m-d')) {
			                    bool = false;
			                    showError('明细表第' + item.data['rad_detno'] + '行的新归还日期小于原归还日期');
			                    return;
			               	}
	    		   		}
					});
					if(bool){
						me.FormUtil.onAudit(Ext.getCmp('ra_id').value);
					}
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ra_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ra_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('ra_id').value);
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