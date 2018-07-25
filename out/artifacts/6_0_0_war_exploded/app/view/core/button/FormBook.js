/**
 * 使用条例与规则按钮
 */	
Ext.define('erp.view.core.button.FormBook',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpFormBookButton',
		iconCls: 'x-button-icon-preview',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpFormBookButton,
    	id: 'formbook',
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
        listeners: {
        	click: function(m){
        		var form = Ext.getCmp('form'), foid = form.fo_id;
        		Ext.Ajax.request({//拿到grid的columns
					url : basePath + "common/getFieldData.action",
					params: {
						caller: 'FormBook',
						field: 'fb_content',
						condition: "fb_foid='" + foid + "'"
					},
					method : 'post',
					async: false,
					callback : function(options,success,response){
						var res = new Ext.decode(response.responseText);
						if(res.exceptionInfo){
							showError(res.exceptionInfo);return;
						}
						if(res.success){
							var win = new Ext.window.Window({
								title: '使用条例与规则',
								height: "90%",
								width: "95%",
								maximizable : true,
								buttonAlign : 'center',
								layout : 'anchor',
								items: [{
									xtype : 'container',
									anchor : '100% 100%',
									html : res.data,
									style: 'padding: 20px;background: #fff'
								}]
							});
	    					win.show();
						}
					}
        		});
        	}
	    },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});