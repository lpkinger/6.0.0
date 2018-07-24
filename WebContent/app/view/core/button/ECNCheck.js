/**
 * 制造ECN评审
 */	
Ext.define('erp.view.core.button.ECNCheck',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpECNCheckButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'ecncheck',
    	text: $I18N.common.button.erpECNCheckButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners: {
			afterrender: function(btn){
				var status = Ext.getCmp("ecn_checkstatuscode");
				if(status && status.value != 'COMMITED'){
					btn.hide();
				}
			},
			click: function(m){
        		Ext.getCmp('ecncheck').turn('ECNCheck', ' mc_ecncode=\'' + Ext.getCmp('ecn_code').value +'\' and ma_id>0');
        	}
		},
		turn: function(nCaller, condition){
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
				    	  	+ "&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
				    }],
				    buttons : [{
				    	name: 'confirm',
				    	text : $I18N.common.button.erpConfirmButton,
				    	iconCls: 'x-button-icon-confirm',
				    	cls: 'x-btn-gray',
				    	listeners: {
   				    		buffer: 500,
   				    		click: function(btn) {
   				    			var _win = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow,
   				    				grid = _win.Ext.getCmp("editorColumnGridPanel");
   				    			btn.setDisabled(true);
   				    			grid.updateAction('pm/bom/confirmECN.action');
   				    			_win.location.reload();
   				    		}
				    	}
				    },{
				    	name: 'cancel',
				    	text : $I18N.common.button.erpCancelButton,
				    	iconCls: 'x-button-icon-close',
				    	cls: 'x-btn-gray',
				    	listeners: {
   				    		buffer: 500,
   				    		click: function(btn) {
   				    			var _win = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow,
				    				grid = _win.Ext.getCmp("editorColumnGridPanel");
   				    			btn.setDisabled(true);
   				    			grid.updateAction('pm/bom/cancelECN.action');
   				    			_win.location.reload();
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