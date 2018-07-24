
Ext.define('erp.view.fa.arp.vmCopQuery.QueryWin',{
	id:'erpcopquerywin_'+caller,
	extend: 'Ext.window.Window',
	alias: 'widget.erpVmCopQueryWindow',
	height: screen.height*0.7*0.7,
	width: screen.width*0.7*0.6,
	condition:'',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    layout : 'anchor',
    items:[{
		autoScroll : true,
		anchor: '100% 100%',
		id:'vmcopqueryform_'+caller,
		xtype:'erpVmCopQueryFormPanel',
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
	    	var form = Ext.getCmp('vmcopqueryform_'+caller);
	    	form.onQuery();
	    	var win = Ext.getCmp('erpcopquerywin_'+caller); 
	    	win.close();
	    }
	},'-','-',{
		text: $I18N.common.button.erpDeleteButton,
		iconCls: 'x-button-icon-delete',
		cls: 'x-btn-gray',
		handler: function(btn){
			var form = Ext.getCmp('vmcopqueryform_'+caller);
			form.beforeDelete(btn,grid);
		}
	},'-','-',{
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
		cls: 'x-btn-gray',
		handler: function(){
			var main = Ext.getCmp('erpcopquerywin_'+caller); 
			main.close();
		}
	}],
	initComponent : function(){ 
		this.callParent();
	}
});