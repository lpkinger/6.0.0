Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.Craft', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.mes.Craft','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','pm.mes.DisplayPanel',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.StepCollection',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.RefreshCrafts'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			afterrender:function(){
    				me.BaseUtil.getSetting('sys', 'pricePerTime', function(v) {
						if(v){
							pricePerTime = v;						
						}
					});
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
    				me.FormUtil.onDelete(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				btn.ownerCt.add({
    					xtype: 'erpStepCollectionButton'//增加工序采集信息维护,   					
    				});   		
    			}        		
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addCraft', '新增工艺', 'jsps/pm/mes/craft.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpStepCollectionButton':{//工序采集信息维护
    			  click:function(btn){
    			  	    var id = Ext.getCmp('grid').selModel.lastSelected.data["cd_id"];
    			  	    var code = Ext.getCmp('grid').selModel.lastSelected.data["cd_stepcode"];
	    				var formCondition="cd_id IS"+id; 
	    				var mothercode = Ext.getCmp("cr_prodcode").value;
        				var gridCondition = "sp_stepcode='"+code+"' and sp_mothercode='"+mothercode+"'";
        				var linkCaller='StepCollection';
        				var win = new Ext.window.Window(
        							{  
        								id : 'win',
        								height : '90%',
        								width : '95%',
        								maximizable : true,
        								buttonAlign : 'center',
        								layout : 'anchor',
        								items : [ {
        									tag : 'iframe',
        									frame : true,
        									anchor : '100% 100%',
        									layout : 'fit',
        									 html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/pm/mes/stepCollection.jsp?_noc=1&whoami='+linkCaller+'&gridCondition='+gridCondition+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
        								} ]

    					});
    				win.show(); 
    			  },
    			 afterrender:function(btn){
    				btn.setDisabled(true);
    			}
    		}   		
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
		var id = Ext.getCmp('grid').selModel.lastSelected.data["cd_id"];
		if(id == undefined||id == ""||id == null){    	 		
		    Ext.getCmp('StepCollection').setDisabled(true);
		}else{
			Ext.getCmp('StepCollection').setDisabled(false);
		}
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});