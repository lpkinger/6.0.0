Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.AmortProgram', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.gla.AmortProgram','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField',
      		'core.button.Audit','core.button.ResAudit','core.button.Close','core.button.Delete','core.button.Update',
      		'core.button.DeleteDetail','core.button.Add','core.button.Save',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
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
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var statdate = Ext.getCmp('ap_startdate').value,
						enddate = Ext.getCmp('ap_enddate').value;
    				if(!Ext.isEmpty(enddate)){
    					if(Ext.Date.format(statdate,'Y-m-d') > Ext.Date.format(enddate,'Y-m-d')){
        					showError('摊销完成时间不能小于摊销开始时间!');return;
        				}
        				if(Ext.Date.format(enddate,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
        					showError('摊销完成时间不能小于当前日期!');return;
        				}
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete({id: Number(Ext.getCmp('ap_id').value)});
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ap_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(){
    				var statdate = Ext.getCmp('ap_startdate').value,
					enddate = Ext.getCmp('ap_enddate').value;
					if(!Ext.isEmpty(enddate)){
						if(Ext.Date.format(statdate,'Y-m-d') > Ext.Date.format(enddate,'Y-m-d')){
	    					showError('摊销完成时间不能小于摊销开始时间!');return;
	    				}
	    				if(Ext.Date.format(enddate,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
	    					showError('摊销完成时间不能小于当前日期!');return;
	    				}
					}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ap_statuscode');
    				if(status && status.value == 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ap_id').value);
				}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ap_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ap_id').value);
				}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addAmortProgram', '新增摊销方案', 'jsps/fa/gla/amortProgram.jsp');
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