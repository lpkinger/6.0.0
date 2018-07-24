/**
 * 转借货归还单
 */	
Ext.define('erp.view.core.button.TurnRenewApply',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnRenewApplyButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'turnRenewApply',
    	text: '转续借申请单',
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners: {
			afterrender: function(btn){
				var status = Ext.getCmp("pi_statuscode");
				if(status && status.value != 'POSTED'){
					btn.hide();
				}
			},
			click: function(m){
        		Ext.getCmp('turnRenewApply').turn('ProdInOut!JHCH!ToRenewApply', 
        				' pd_piid=' + Ext.getCmp('pi_id').value +' and nvl(pd_returnqty,0)<nvl(pd_outqty,0) order by pd_pdno',
        				'scm/reserve/turnRenewApply.action');
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
   				    			window.location.reload();
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