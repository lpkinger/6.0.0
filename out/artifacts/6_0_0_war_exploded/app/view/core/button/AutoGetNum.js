/**
 * 自动获取编号按钮
 */	
Ext.define('erp.view.core.button.AutoGetNum',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAutoGetNumButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpAutoGetNumButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(){
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
   				    	  html : '<iframe id="iframe" src="' + basePath +  'jsps/scm/product/autoGetNum.jsp" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
   				    }]
   				});
   				win.show();
		}
	});