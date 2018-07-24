Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Myzhuanz', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
   		'hr.emplmana.Myzhuanz','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
   		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
   		'core.button.Update','core.button.Delete','core.form.YnField',
   		'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
   	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			reconfigure:function(grid){
    				var condi="em_name like '%"+Ext.getCmp('tf_recordor').value+"%'";
    				me.getEmp(grid,condi);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				if(em_class!='试用'){
    					showError("申请条件不符合，请核对后重试！");
    					return;
    				}
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
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('tf_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addTurnfullmemb', '新增用人申请', 'jsps/hr/emplmana/employee/myzhuanz.jsp');
    			}
    		},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tf_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('tf_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tf_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('tf_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tf_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('tf_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tf_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('tf_id').value);
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
	getEmp:function(grid,cond){
		Ext.Ajax.request({
	   		url : basePath + 'common/autoDbfind.action',
	   		params: {
	   			which: 'grid',
	   			caller: 'Employeecheck',
	   			field: "td_code",
	   			condition: cond//vr_vacode like '%2013100002%'
	   		},
	   		async: false,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var res = new Ext.decode(response.responseText);
	   			if(res.exceptionInfo){
	   				showError(res.exceptionInfo);return;
	   			}
	   			if(res.data){
	   				var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
	   				var record=grid.getStore().getAt(0);
	   				Ext.Array.each(grid.dbfinds,function(ds){
	   					record.set(ds.field,data[0][ds.dbGridField]);
	   				});
	   			}else {
	   				em_class="非试用";
	   			} 
	   		}
	   });
	}
});