Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.SaleForecast', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','pm.mps.SaleForecast','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'
    	],
    init:function(){
    var me=this;
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
    				this.save(this);
    			}
    		},
    		'dbfindtrigger': {
    			change: function(trigger){
    				if(trigger.name == 'team_prjid'){
    					this.changeGrid(trigger);
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
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('team_id').value);
    			}
    		},
    		 'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addTeam', '创建团队', 'jsps/plm/team/team.jsp');
    			}
    		},
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		if(Ext.getCmp('team_code').value == null || Ext.getCmp('team_code').value == ''){
			me.BaseUtil.getRandomNumber();
		}
		me.FormUtil.beforeSave(me);
	},
	changeGrid: function(trigger){
		var grid = Ext.getCmp('grid');
		Ext.Array.each(grid.store.data.items, function(item){
			item.set('tm_prjid',trigger.value);
		});
	}
	
});