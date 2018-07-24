/**
 * 转借货归还单
 */	
Ext.define('erp.view.core.button.TurnCapitalization',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnCapitalizationButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'turnCapitalization',
    	text: '转资本化',
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners: {
			afterrender: function(btn){
				var status = Ext.getCmp("pfr_statuscode");
				if(status && status.value != 'AUDITED'){
					btn.hide();
				}
			},
			click: function(m){
        		Ext.getCmp('turnCapitalization').turn('ProjectFinishReport!ToCapitalization', 
        				' vds_asscode=\'' + Ext.getCmp('pfr_prjcode').value +'\' and vds_asstype=\'项目\' and VO_EXPLANATION<>\'结转损益\' and nvl(pfk_capitalize,0)<>0 order by vo_yearmonth,vo_number',
        				'plm/cost/turnCapitalization.action');
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