/**
 *辅助核算窗口
 **/
Ext.define('erp.view.core.window.AssWindow',{
	id:'erpasswin_'+caller,
	extend: 'Ext.window.Window',
	alias: 'widget.erpAssWindow',
	height: screen.height*0.7*0.7,
	width: screen.width*0.7*0.6,
	condition:'',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    layout : 'anchor',
    items:[{
		autoScroll : true,
		anchor: '100% 100%',
		id:'windowgrid',
		xtype:'assgrid',
		condition:caller+'_main'
	}],
	buttonAlign : 'center',
	buttons:[{
		name: 'confirm',
		text: $I18N.common.button.erpConfirmButton,
		iconCls: 'x-button-icon-confirm',
		cls: 'x-btn-gray',
		style: {
			marginLeft: '10px'
	    },
	    handler:function(){
	    	var grid = Ext.getCmp('windowgrid');
	    	grid.beforeUpdate();
	    }
	},'-','-',{
		text: $I18N.common.button.erpDeleteButton,
		iconCls: 'x-button-icon-delete',
		cls: 'x-btn-gray',
		handler: function(btn){
			var grid = Ext.getCmp('windowgrid');
			grid.beforeDelete(btn,grid);
		}
	},'-','-',{
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
		cls: 'x-btn-gray',
		handler: function(){
			var main = Ext.getCmp('erpasswin_'+caller); 
			main.close();
		}
	}],
	initComponent : function(){ 
		this.callParent();
	}
});