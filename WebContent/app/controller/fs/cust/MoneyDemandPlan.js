Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.MoneyDemandPlan', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fs.cust.MoneyDemandPlan','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.form.HrefField','core.button.PrintByCondition',
			'core.form.YnField','core.form.TimeMinuteField','core.trigger.DbfindTrigger','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField'
    	],
        init:function(){
        	var me = this;
        	this.control({ 
        		'erpGridPanel2': {       			
        			itemclick: function(selModel, record){								
    					this.onGridItemClick(selModel, record);   					
        			}      			
        		},
        		'erpAddButton': {
        			click: function(){
        				me.FormUtil.onAdd('MoneyDemandPlan', '资金需求计划', 'jsps/fs/cust/moneyDemandPlan.jsp');
        			}
        		},        
        		'erpSaveButton': {
        			click: function(btn){  
        				var form = Ext.getCmp('form'); 
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
        			click: function(btn){        				
        				this.FormUtil.onUpdate(this);	
        			}
        		},
        		'erpDeleteButton': {
        			click: function(btn){
        				me.FormUtil.onDelete((Ext.getCmp('mp_id').value));
        			}
        		},
          		'erpSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('mp_statuscode');
        				if(status && status.value != 'ENTERING'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onSubmit(Ext.getCmp('mp_id').value);
        			}
        		},
        		'erpResSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('mp_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResSubmit(Ext.getCmp('mp_id').value);
        			}
        		},
        		'erpAuditButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('mp_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onAudit(Ext.getCmp('mp_id').value);
        			}
        		},
        		'erpResAuditButton' : {
					afterrender : function(btn) {
						var status = Ext.getCmp('mp_statuscode');
						if (status && status.value != 'AUDITED') {
							btn.hide();
						}
					},
					click : function(btn) {
						me.FormUtil.onResAudit(Ext.getCmp('mp_id').value);
					}
        		}		
        	});
        },
        onGridItemClick: function(selModel, record){//grid行选择
        	this.GridUtil.onGridItemClick(selModel, record); 	
        }
});