/**
 * 转发货按钮
 */	
Ext.define('erp.view.core.button.Consign',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConsignButton',
		iconCls: 'x-button-icon-Consign',
    	cls: 'x-btn-gray',
    	text: '转通知单',
    	id: 'consign',
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
        listeners: {
	        click: function(m){
	        	Ext.getCmp('consign').turn('Sale!ToAccept!Deal', 'sd_said=' + Ext.getCmp('sa_id').value + ' AND nvl(sd_yqty,0)<nvl(sd_qty,0) and sd_statuscode=\'AUDITED\'', 'scm/sale/turnNotify.action');
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