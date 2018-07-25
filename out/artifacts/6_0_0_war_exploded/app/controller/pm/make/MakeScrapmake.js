Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeScrapmake', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.make.MakeScrapmake','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Print','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.Flow','core.button.PrintByCondition'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
			'erpSaveButton': {
					click: function(btn){
	    				var form = me.getForm(btn);
	    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
	    					me.BaseUtil.getRandomNumber();//自动添加编号
	    				}
	    				//保存之前的一些前台的逻辑判定
	    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
						var firstItem = grid.store.getAt(0);
						if(firstItem) {
							var dc = firstItem.get('md_department'), dn = firstItem.get('md_departmentname'),
							   	md_reason = firstItem.get('md_reason');
							 Ext.Array.each(items, function(item){
								 if(!Ext.isEmpty(item.data['md_mmcode'])){
									 if(Ext.isEmpty(item.data['md_reason'])){
										   item.set('md_reason', md_reason); 
									 }
									 if(Ext.isEmpty(item.data['md_department'])){
										   item.set('md_department', dc); 
										   item.set('md_departmentname', dn);
									 }  
								 }
							 });
						}
	    				this.FormUtil.beforeSave(this);	
	    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ms_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('ms_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					var firstItem = grid.store.getAt(0);
					if(firstItem) {
						var dc = firstItem.get('md_department'), dn = firstItem.get('md_departmentname'),
						   	md_reason = firstItem.get('md_reason');
						 Ext.Array.each(items, function(item){
							 if(!Ext.isEmpty(item.data['md_mmcode'])){
								 if(Ext.isEmpty(item.data['md_reason'])){
									   item.set('md_reason', md_reason); 
								 }
								 if(Ext.isEmpty(item.data['md_department'])){
									   item.set('md_department', dc); 
									   item.set('md_departmentname', dn);
								 }  
							 }
						 });
					}
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addMakeScrapmake', '新增委外报废单', 'jsps/pm/make/makeScrapmake.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ms_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ms_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ms_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ms_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ms_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ms_id').value);
				}
			},
			 'erpPrintButton':{
    			click:function(btn){
    				var reportName="MakeScrap";
    				var condition="";
    			    condition='{MakeScrap.ms_id}='+Ext.getCmp('ms_id').value;
    				var id=Ext.getCmp('ms_id').value;
    				me.FormUtil.onwindowsPrint2(id,reportName,condition);
    			}
    		},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ms_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ms_id').value);
				}
			},
			'dbfindtrigger[name=md_mmdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['md_mmcode'];
    				if(code == null || code == ''){
    					showError("请先选择关联单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					/*var field = me.getBaseCondition();
    					if(field){*/
    						t.dbBaseCondition = "ma_code='" + code + "'";
    					//} 
    				}
    			}
    		},
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
	    this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});