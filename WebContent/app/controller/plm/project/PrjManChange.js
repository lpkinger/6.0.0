Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.PrjManChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.project.PrjManChange','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.grid.YnColumn','core.button.Scan',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.form.YnField','core.button.DeleteDetail','core.button.Upload','core.form.FileField',
    			'core.trigger.MultiDbfindTrigger','core.form.MultiField','core.button.TurnReturn','core.trigger.TextAreaTrigger'
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
    		'erpAddButton': {
    			click: function(btn){
    				if(caller=='PrjManChange'){
    					me.FormUtil.onAdd('addPrjManChange', '新增推广立项人员异动', 'jsps/plm/project/prjManChange.jsp');	
    				}
    				if(caller=='PrjManChange!DY'){
    					me.FormUtil.onAdd('addPrjManChangeDY', '新增调研立项人员异动', 'jsps/plm/project/prjManChange.jsp?whoami=PrjManChange!DY');
    				}
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
    				me.FormUtil.onDelete((Ext.getCmp('mc_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('mc_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('mc_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('mc_id').value);
    			}
    		},
    		'dbfindtrigger[name=mcd_emcode]':{
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var prj_code=Ext.getCmp('mc_prjcode').value;
    				var operation = record.data['mcd_operation'];
    				if(prj_code == null || prj_code == ''){
    					showError("请先选择项目编号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else if(operation == null || operation == ''){
    					showError("请先选择要执行的操作!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					if(operation=='删除'){//删除操作选项目里的人
    						if(caller=='PrjManChange'){
    							t.dbBaseCondition="exists(select 1 from projectdet where prd_emcode=em_code and prd_prjid=(select prj_id from project where nvl(prj_class,' ')='市场推广立项' and prj_code='"+prj_code+"') )";
    						}
    						if(caller=='PrjManChange!DY'){
    							t.dbBaseCondition="exists(select 1 from projectdet where prd_emcode=em_code and prd_prjid=(select prj_id from project where nvl(prj_class,' ')='市场调研立项' and prj_code='"+prj_code+"') )";
    						}
    					}else{
    						if(caller=='PrjManChange'){
    							t.dbBaseCondition="not exists(select 1 from projectdet where prd_emcode=em_code and prd_prjid=(select prj_id from project where nvl(prj_class,' ')='市场推广立项' and prj_code='"+prj_code+"') )";
    						}
    						if(caller=='PrjManChange!DY'){
    							t.dbBaseCondition="not exists(select 1 from projectdet where prd_emcode=em_code and prd_prjid=(select prj_id from project where nvl(prj_class,' ')='市场调研立项' and prj_code='"+prj_code+"') )";
    						}
//    						t.dbBaseCondition=null;
    					}
    				}
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},     			
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('mc_id').value);
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