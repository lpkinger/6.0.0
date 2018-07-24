/**
 * 转文件归还单按钮
 */	
Ext.define('erp.view.core.button.TurnDocReturn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnDocReturnButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: '转文件归还单',
    	id: 'turnDocReturn',
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
        listeners: {
	        click: function(m){
	        	Ext.getCmp('turnDocReturn').turn(caller, ' nvl(cd_turnstatuscode,\' \') <> \'Turned\' and CD_CTID = '+Ext.getCmp("CT_ID").value	, 'oa/custom/turnDocPage.action?id='+Ext.getCmp("CT_ID").value);
	        }
	    },
		initComponent : function(){ 
			this.callParent(arguments); 
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