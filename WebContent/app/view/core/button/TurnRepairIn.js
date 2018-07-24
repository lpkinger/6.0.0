Ext.define('erp.view.core.button.TurnRepairIn',{
	extend : 'Ext.Button',
	alias : 'widget.erpTurnRepairIn',
	requires: ['erp.util.FormUtil'],
	iconCls : 'x-button-icon-check',
	text : '转归还单',
	cls: 'x-btn-gray',
	width: 110,
	id: 'erpTurnRepairIn',
	FormUtil: Ext.create('erp.util.FormUtil'),
	initComponent : function(){
		this.callParent(arguments); 
	},
    listeners: {
    	afterrender : function(m){
    		var form = m.up('form');
    		var statusField = form.statusField;
    		var status = Ext.getCmp(statusField).value;
    		if(status!='已审核'){
    			m.hidden = true;
    		}
    	},
        click: function(m){
        	var form = m.up('form');
        	var fo_keyField = form.fo_keyField;
        	var fo_detailMainKeyField = form.fo_detailMainKeyField;
        	var condition = '';
        	if(caller=='RepairOut' || caller=='RepairOut!transfer'){
        		condition = "AND nvl(rd_yqty,0)<nvl(rd_qty,0)";
        	}
        	Ext.getCmp('erpTurnRepairIn').turn(caller, fo_detailMainKeyField + '=' + Ext.getCmp(fo_keyField).value + condition, 'common/turnCommon.action');
        }
    },
	turn: function(nCaller, condition, url){
		var win = new Ext.window.Window({
	    		id : 'win',
			    height: "100%",
			    width: "80%",
			    maximizable : true,
				buttonAlign : 'center',
				layout : 'anchor',
			    items: [{
			    	  tag : 'iframe',
			    	  frame : true,
			    	  anchor : '100% 100%',
			    	  layout : 'fit',
			    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller 
			    	  	+ "!transfer&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			    }],
			    buttons : [{
			    	name: 'confirm',
			    	text : $I18N.common.button.erpConfirmButton,
			    	iconCls: 'x-button-icon-confirm',
			    	cls: 'x-btn-gray',
			    	listeners: {
			    		buffer: 500,
			    		click: function(btn) {
			    			var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
				    		btn.setDisabled(true);
				    		grid.updateAction(url);
			    		}
			    	}
			    }, {
			    	text : $I18N.common.button.erpCloseButton,
			    	iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler : function(){
			    		Ext.getCmp('win').close();
			    	}
			    }]
			});
				win.show();
	}
});