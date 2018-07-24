Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.BOMSetChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.BOMSetChange','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.grid.YnColumn'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    			}
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
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bc_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addBomSetChange', '新增Bom配套变更单', 'jsps/pm/bom/BOMSetChange.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('bc_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('bc_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bc_id').value);
    			}
    		},   		
    		'dbfindtrigger[name=bcd_bsdetno]': {
    			focus: function(t){
    				t.autoDbfind = false;
    				t.setHideTrigger(false);
    				t.setReadOnly(false);     				
    				var bs_code = Ext.getCmp("bc_bscode").value;
    				console.log(bs_code);
   					if (bs_code =='' || bs_code == null){
    					showError("请先选择需要变更的配套编号 !");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    					return;
   					}
   					var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var type = record.data['bcd_type'];    				
    				if (type=='' || type == null){
    					showError("请先选择操作类型 !");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    					return;
    				}else if(type=="ADD" ){//需指定BOM 序号
    					showError("增加物料不需要选择配套表序号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    					return;
    				}
    				t.dbBaseCondition = "bsd_code='" + bs_code + "'";
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