Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.Packing', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.Packing','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.button.CatchMadeIn',
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
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('pa_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addPacking', '新增包装单', 'jsps/scm/sale/packing.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('pa_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pa_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pa_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pa_id').value);
				}
			},
			'erpCatchMadeInButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var form = btn.ownerCt.ownerCt,
						ev_id = Ext.getCmp('ev_id').value,
						bo_id = Ext.getCmp('ev_bomid').value,
						pr_code = Ext.getCmp('ev_prcode').value;
				if(bo_id==""){bo_id=0;}
				form.setLoading(true);
    			Ext.Ajax.request({
    				url: basePath + 'scm/sale/bomcost.action',
    				params: {
    					ev_id: ev_id,
    					bo_id: bo_id,
    					pr_code: pr_code
    				},
    				timeout: 600000,
    				callback: function(opt, s, r) {
    					form.setLoading(false);
    					var rs = Ext.decode(r.responseText);
    					if(rs.success) {
    						alert('计算完成!');
    						me.FormUtil.loadNewStore(form, {caller: caller, condition: 'ev_id=' + ev_id});
    						me.GridUtil.loadNewStore(form.ownerCt.down('grid'), {caller: caller, condition: 'evd_evid=' + ev_id});
    					}
    				}
    			});
				}
			},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('pa_id').value);
				}
			},
			'dbfindtrigger[name=pi_custcode2]': {
    			afterrender:function(trigger){
    				trigger.dbKey='pi_custcode';
	    			trigger.mappingKey='cu_code';
	    			trigger.dbMessage='请先选客户编号！';
    			}
    		},
    		'dbfindtrigger[name=pi_invoiceremark]': {
    			afterrender:function(trigger){
    				trigger.dbKey='pi_custcode';
	    			trigger.mappingKey='cu_code';
	    			trigger.dbMessage='请先选客户编号！';
    			}
    		},
    		'dbfindtrigger[name=pi_packingremark]': {
    			afterrender:function(trigger){
    				trigger.dbKey='pi_custcode';
	    			trigger.mappingKey='cu_code';
	    			trigger.dbMessage='请先选客户编号！';
    			}
    		},
    		'dbfindtrigger[name=pi_receivecode]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='pi_custcode';
	    			trigger.mappingKey='cu_code';
	    			trigger.dbMessage='请先选客户编号！';
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