Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.marketresearch.Team', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','crm.marketmgr.marketresearch.Team','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    var me=this;
    	this.control({ 
    	'erpGridPanel2': {
    		  itemclick: this.onGridItemClick
    		 },
    		'erpSaveButton': {
    			click: function(btn){
					var grids = Ext.ComponentQuery.query('gridpanel');
		         if(grids.length > 0){
		   					var s = grids[0].getStore().data.items;
		   					for(var i=0;i<s.length;i++){
		   					  var rowdata=s[i].data;
		   					  if(rowdata.tm_employeecode!=''){
		   						  console.log('aa');
		   					      s[i].set('tm_prjid',Ext.getCmp('team_prjid').value);
		   					      s[i].set('tm_name',Ext.getCmp('team_name').value);
		   					  }
		   					 }
		    				}		
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
    		
    		  'textfield[name=team_name]': {
    			change: function(field){
    				var grid = Ext.getCmp('grid');
    				Ext.Array.each(grid.store.data.items, function(item){
    					item.set('tm_name',field.value);
    				});
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    			var grids = Ext.ComponentQuery.query('gridpanel');
		         if(grids.length > 0){
		   					var s = grids[0].getStore().data.items;
		   					for(var i=0;i<s.length;i++){
		   					  var rowdata=s[i].data;
		   					  if(rowdata.tm_employeecode!=''){		   					      
		   					      s[i].set('tm_prjid',Ext.getCmp('team_prjid').value);
		   					      s[i].set('tm_name',Ext.getCmp('team_name').value);
		   					  }
		   					 }
		    				}		
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('team_id').value);
    			}
    		},
    		 'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addTeam', '创建团队', 'jsps/crm/marketmgr/marketresearch/team.jsp');
    			}
    		},
    	});
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