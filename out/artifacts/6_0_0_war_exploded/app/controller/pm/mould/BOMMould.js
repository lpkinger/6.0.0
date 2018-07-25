Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.BOMMould', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil'),
    views:[
    		'pm.mould.BOMMould','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.button.BomCopy',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail','core.button.CallProcedureByConfig',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.grid.YnColumn','core.button.Flow','core.button.Print',
    		'core.button.SonBOM','core.button.Replace','core.button.FeatureDefinition','core.button.PrintByCondition',
    		'core.button.Banned','core.button.ResBanned','core.form.FileField','core.button.Sync','core.button.FeatureQuery',
    		'core.button.LoadRelation','core.button.Modify','core.button.ProcessingWay','core.grid.detailAttach',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField','core.button.BOMTurn','core.button.BomUpdatePast'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpFormPanel': {},
    		'erpGridPanel2': {
    			itemclick: function(selModel, record){
    				Ext.getCmp('processingway').setDisabled(false); 
    				me.onGridItemClick(selModel, record);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = btn.ownerCt.ownerCt; 
    				if(Ext.getCmp(form.codeField)){
    				   if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('BOM',1,'bo_code');//自动添加编号
    				  }
    				}  				
    				this.FormUtil.beforeSave(this);   				
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				btn.ownerCt.add({
    					xtype: 'erpProcessingWayButton',
    					disabled:true
    				});
    			}
    		},
    		'#bd_piccode':{
    			click:function(){
    			}
    		},
    		'erpProcessingWayButton': {
            	click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				var id = record.data['bd_id'];
    				var bomid=record.data['bd_bomid'];
    				var main = parent.Ext.getCmp("content-panel");
    				var panelId=main.getActiveTab().id;
    			    main.getActiveTab().currentGrid=Ext.getCmp('grid');
    				if(id != null && id != '' && id != 0 && id != '0'){
    					me.FormUtil.onAdd('BOMMould!Processing' + id, '加工方式明细维护', 'jsps/pm/mould/ModifyProcessing.jsp?formCondition=bd_id=' + id + 
        						"&gridCondition=bm_bdid=" + id + "&_noc=1&panelId="+panelId+"&bomid="+bomid);
    				}
    			}
            },
    		'erpDeleteButton' : {
    			afterrender: function(btn){
    				if(Ext.getCmp('bo_id').value == null || Ext.getCmp('bo_id').value == ''){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bo_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				if(Ext.getCmp('bo_id').value == null || Ext.getCmp('bo_id').value == ''){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				var title = btn.ownerCt.ownerCt.title || ' ';
    				var url = window.location.href;
    				url = url.replace(basePath, '');
    				url = url.substring(0, url.lastIndexOf('formCondition')-1);
    				me.FormUtil.onAdd('add' + caller, title, url);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
     				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				if(bool){
    					me.FormUtil.onSubmit(Ext.getCmp('bo_id').value,true);
    				}			
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
                    if(status && status.value != 'COMMITED'){
                        btn.hide();
                    }
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp(me.getForm(btn).keyField).value);	
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	//按照gridbutton表中的gb_conf配置的id   来获取对象，如未设置id  就取默认
    	if(Ext.getCmp('BOMpic')){
    		Ext.getCmp('BOMpic').setDisabled(false);
    	}
    	this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	resize: function(form, grid){
		if(!this.resized && form && grid && form.items.items.length > 0){
			var height = window.innerHeight, 
				fh = form.getEl().down('.x-panel-body>.x-column-inner').getHeight();
			form.setHeight(70 + fh);
			grid.setHeight(height - fh - 70);
			this.resized = true;
		}
	}
});