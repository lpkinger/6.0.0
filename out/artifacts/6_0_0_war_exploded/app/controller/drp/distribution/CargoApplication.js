Ext.QuickTips.init();
Ext.define('erp.controller.drp.distribution.CargoApplication', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
'core.form.Panel','drp.distribution.CargoApplication','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
	'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
		'core.button.ResSubmit','core.button.TurnStorage','core.button.TurnCheck','core.button.TurnSale',
	'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger'
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
    				this.FormUtil.beforeSave(this);    				
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			 afterrender: function(btn) {
                     var status = Ext.getCmp('ca_statuscode');
                     if (status && status.value != 'ENTERING') {
                         btn.hide();
                     }
                 },
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			 afterrender: function(btn) {
                     var status = Ext.getCmp('ca_statuscode');
                     if (status && status.value != 'ENTERING') {
                         btn.hide();
                     }
                 },
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			 afterrender: function(btn) {
                     var status = Ext.getCmp('ca_statuscode');
                     if (status && status.value != 'ENTERING') {
                         btn.hide();
                     }
                 },
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			 afterrender: function(btn) {
                     var status = Ext.getCmp('ca_statuscode');
                     if (status && status.value != 'COMMITED') {
                         btn.hide();
                     }
                 },
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpAuditButton': {
    			 afterrender: function(btn) {
                     var status = Ext.getCmp('ca_statuscode');
                     if (status && status.value != 'COMMITED') {
                         btn.hide();
                     }
                 },
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			 afterrender: function(btn) {
                     var status = Ext.getCmp('ca_statuscode');
                     if (status && status.value != 'AUDITED') {
                         btn.hide();
                     }
                 },
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpTurnSaleButton': {
   			 afterrender: function(btn) {
                    var status = Ext.getCmp('ca_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
   			click: function(btn){

				warnMsg("确定要转销售单吗?", function(btn){
					if(btn == 'yes'){
						me.FormUtil.getActiveTab().setLoading(true);//loading...
	    				Ext.Ajax.request({
	    			   		url : basePath + 'drp/distribution/turnFXSale.action',
	    			   		params: {
	    			   			id: Ext.getCmp('ca_id').value
	    			   		},
	    			   		method : 'post',
	    			   		callback : function(options,success,response){
	    			   			me.FormUtil.getActiveTab().setLoading(false);
	    			   			var localJson = new Ext.decode(response.responseText);
	    			   			if(localJson.exceptionInfo){
	    			   				showError(localJson.exceptionInfo);
	    			   			}
	    		    			if(localJson.success){
	    		    				turnSuccess(function(){
	    		    					var id = localJson.id;
	    		    					var url = "jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_id=" + id + "&gridCondition=sd_said=" + id;
	    		    					me.FormUtil.onAdd('Sale' + id, '销售订单' + id, url);
	    		    				});
	    			   			}
	    			   		}
	    				});
					}
				});
			
   			}
   		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addCargoApplication', '新增配货申请单', 'jsps/drp/distribution/cargoApplication.jsp');
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