Ext.QuickTips.init();
Ext.define('erp.controller.plm.cost.ProjectFeePlease', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','plm.cost.ProjectFeePlease','core.grid.Panel2','core.toolbar.Toolbar','core.button.Scan',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.ResSubmit',
    			'core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.ColorField','core.form.YnField'
    	],
    init:function(){
       var me=this;
    	this.control({ 
    		'field[name=pf_statuscode]': {
    			change: function(f){
    				var grid = Ext.getCmp('grid');
    				if(grid && f.value != 'ENTERING'){
    					grid.setReadOnly(true);
    				}
    			},
    		},
    		'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
    	    'erpSaveButton': {
    	    	click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.getamount();
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    		    afterrender: function(btn){
    				var status = Ext.getCmp('pf_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.getamount();
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    		  afterrender: function(btn){
    				var status = Ext.getCmp('pf_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('pf_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProjectFeePlease', '新增费用申请 ', 'jsps/plm/cost/ProjectFeePlease.jsp');
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
    				me.FormUtil.onSubmit(Ext.getCmp('pf_id').value);
    			}
    		},
    		'erpResSubmitButton':{
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
    	   'erpResAuditButton':{
    	      afterrender: function(btn){
    				var status = Ext.getCmp('pf_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pf_id').value);
    			}
    	   }	 		    
    	});
    },
    getamount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var amount = 0;
		Ext.each(items,function(item,index){
			if(item.data['pfd_subjectcode']!=null&&item.data['pfd_subjectcode']!=""){
				amount= amount + Number(item.data['pfd_amount']);
			}
		});
		Ext.getCmp('pf_amount').setValue(amount);
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});