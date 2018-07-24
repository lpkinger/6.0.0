/**
 * 此toolbar用于单个grid的页面
 * 直接根据caller，到数据库取对应的button
 */	
Ext.define('erp.view.core.toolbar.Toolbar3',{ 
		extend: 'Ext.Toolbar', 
		alias: 'widget.erpToolbar3',
		dock: 'bottom',
		height:36,
		initComponent : function(){ 
			var me = this;
			Ext.Ajax.request({
		   		url : basePath + "common/gridButton.action",
		   		params: {
		   			caller: caller
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
	    			if(localJson.exceptionInfo){
	    				showError(localJson.exceptionInfo);
	    			}
	    			if(localJson.buttons){
	    				var buttons = Ext.decode(localJson.buttons);
	    				// put an end to the wrong link when click button
	    				Ext.each(buttons, function(b){
	    					me.add({xtype: b.xtype});
	    				});
	    				Ext.each(me.items.items, function(b){
	    					for(var i in buttons) {
	    						if(b.xtype == buttons[i].xtype) {
	    							b.url = buttons[i].url;
	    							break;
	    						}
	    					}
	    				});
	    			}
		   		}
			});

			this.callParent(arguments); 
		}
	});