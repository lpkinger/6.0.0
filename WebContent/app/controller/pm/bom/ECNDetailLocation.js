Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.ECNDetailLocation', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.bom.ECNDetailLocation','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload',
  			'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
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
					me.GridUtil.onSave(Ext.getCmp('grid'));
				}
			},
			'erpDeleteButton' : {
				afterrender: function(btn){
					if(Ext.getCmp('ed_id').value != null && Ext.getCmp('ed_id').value != ''){
						btn.show();
					} else {
						btn.hide();
					}
    			},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ed_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					if(Ext.getCmp('ed_id').value != null && Ext.getCmp('ed_id').value != ''){
						btn.show();
					} else {
						btn.hide();
					}
    			},
				click: function(btn){
					me.GridUtil.onUpdate(Ext.getCmp('grid'));
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addECNDetailLocation', '新增ECN物料位号', 'jsps/pm/bom/ECNDetailLocation.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'dbfindtrigger[name=ed_sonid]': {
    			afterrender: function(t){
    				t.dbKey = "ed_bomid";
    				t.mappingKey = "ed_ecnid";
    				t.dbMessage = "请先选择ECNID!";
    			}
    		},
			'field[name=ed_id]': {
				change: function(f){
					if(f.value != null && f.value != ''){
						me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
							caller: caller,
							condition: 'edl_bdid=' + f.value
						});
						Ext.getCmp('deletebutton').show();
						Ext.getCmp('updatebutton').show();
						//Ext.getCmp('save').hide();
					} else {
						Ext.getCmp('deletebutton').hide();
						Ext.getCmp('updatebutton').hide();
						//Ext.getCmp('save').show();
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