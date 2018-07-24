/**
 * 转不良品出库单
 */	
Ext.define('erp.view.core.button.TurnDefectOut',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnDefectOutButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'turnout',
    	text: $I18N.common.button.erpTurnDefectOutButton,
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
        		Ext.getCmp('turnout').turn('ProdIN!ToProdDefectOut!Deal', ' pd_piid=' + Ext.getCmp('pi_id').value +' and nvl(pd_yqty,0) < nvl(pd_inqty,0) + nvl(pd_outqty,0)', 'scm/reserve/turnDefectOut.action?type=ProdInOut!DefectOut');
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