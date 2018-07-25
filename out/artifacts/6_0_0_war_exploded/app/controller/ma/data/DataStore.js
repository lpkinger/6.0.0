Ext.QuickTips.init();
Ext.define('erp.controller.ma.data.DataStore', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'ma.data.DataStore','core.form.Panel','core.grid.Panel2',
   		'core.button.Add','core.button.Save','core.button.Close',
   		'core.button.Update','core.button.Submit','core.button.Scan','core.toolbar.Toolbar',
   		'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.button.DeleteDetail'
   	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': {
      		  itemclick: this.onGridItemClick
      		 },
    		'multidbfindtrigger[name=dsd_field]':{
    			afterrender:function(trigger){
    				trigger.gridKey='ds_tablename';
        			trigger.mappinggirdKey='ddd_tablename';
        			trigger.gridErrorMessage='请先选择该DataStore的表名'; 	
    			}   			
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(me);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('DataStore', '添加数据集', 'jsps/ma/data/dataStore.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(me);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('grid');
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClick(selModel, record);
    },
});