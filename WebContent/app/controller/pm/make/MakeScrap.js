Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeScrap', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.make.MakeScrap','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.ResAudit',
  			'core.button.Audit','core.button.Print','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
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
	    				var grid = Ext.getCmp('grid');
	    				var firstItem = grid.store.getAt(0);
	    		    	if(firstItem) {
	    		    		var desc = firstItem.get('md_reason'), dc = firstItem.get('md_department'),
	    		    			dn = firstItem.get('md_departmentname');
	    		    			grid.store.each(function(item){
		    		    			if(!Ext.isEmpty(item.get('md_mmcode'))){
		    		    				if(Ext.isEmpty(item.get('md_reason'))) {
			    		    				item.set('md_reason', desc);
			    		    			}
			    		    			if(Ext.isEmpty(item.get('md_department'))) {
			    		    				item.set('md_department', dc);
			    		    				item.set('md_departmentname', dn);
			    		    			}
		    		    			} 
	    		    		});
	    		    	}
	    				//保存之前的一些前台的逻辑判定
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
					var grid = Ext.getCmp('grid');
					var firstItem = grid.store.getAt(0);
					if(firstItem) {
    		    		var desc = firstItem.get('md_reason'), dc = firstItem.get('md_department'),
    		    			dn = firstItem.get('md_departmentname');
    		    			grid.store.each(function(item){
    		    				if(!Ext.isEmpty(item.get('md_mmcode'))){
    		    					if(Ext.isEmpty(item.get('md_reason'))) {
    	    		    				item.set('md_reason', desc);
    	    		    			}
    	    		    			if(Ext.isEmpty(item.get('md_department'))) {
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
					me.FormUtil.onAdd('addMakeScrap', '新增生产报废单', 'jsps/pm/make/makeScrap.jsp');
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
			 'erpPrintButton':{
    			click:function(btn){
    				var reportName="MakeScrap";
    				var condition="";
    			    condition='{MakeScrap.ms_id}='+Ext.getCmp('ms_id').value;
    				var id=Ext.getCmp('ms_id').value;
    				me.FormUtil.onwindowsPrint2(id,reportName,condition);
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