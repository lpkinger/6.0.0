/**
 * 载入替代关系
 */	
Ext.define('erp.view.core.button.LoadRelation',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLoadRelationButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpLoadRelationButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners: {
			afterrender: function(btn){
				var status = Ext.getCmp("bo_statuscode");
				if(status && status.value != 'ENTERING'){
					btn.hide();
				}
			},
			click: function(m){
        		m.turn();
        	}
		},
		turn: function(){
	    	var win = new Ext.window.Window({
		    	    id : 'win',
		    	    x:'150',
		    	    y:'300',
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
				    	  html : '<iframe src="' + basePath + 'jsps/common/editorColumn.jsp?caller=LoadRelation' 
				    	  	+ "&condition=prr_usestatuscode='AUDITED' and NVL(bd_usestatus,' ')<>'DISABLE' and bd_bomid=" + Ext.getCmp("bo_id").value + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
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
   				    			grid.updateAction('pm/bom/loadRelation.action');
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