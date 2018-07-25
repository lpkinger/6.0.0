Ext.define('erp.view.common.init.ImportScheme',{ 
	extend: 'Ext.Viewport', 
	initComponent : function(){ 
		var me = this; 
		me.FormUtil = Ext.create('erp.util.FormUtil');
		Ext.apply(me, { 
			items: [{
				xtype: 'panel',
				bodyPadding: 10,
				items: [{
    				xtype: 'form',
    				bodyStyle: 'background:#e0e0e0',
    				items: [{
	    				xtype: 'filefield',
	    				name: 'file',
	    		        buttonOnly: true,
	    		        buttonText: '点击选择.json文件'
	    			},{
	    				xtype: 'displayfield',
	    				name: 'expDate',
	    				labelAlign: 'right',
    					hidden: true,
    					fieldLabel: '导出日期'
	    			},{
	    				xtype: 'displayfield',
	    				name: 'from',
	    				labelAlign: 'right',
    					hidden: true,
	    				fieldLabel: '来源'
	    			},{
    					xtype: 'displayfield',
	    				name: 'type',
	    				labelAlign: 'right',
    					hidden: true,
	    				fieldLabel: '类型'
	    			},{
	    				xtype: 'displayfield',
	    				name: 'desc',
	    				labelAlign: 'right',
    					hidden: true,
	    				fieldLabel: '描述'
	    			}]
	    		}],
    			buttonAlign: 'center',
    			buttons: [{
	    			text: '确认导入',
	    			itemId: 'confirm',
	    			disabled: true,
	    			handler: function() {
	    				me.FormUtil.setLoading(true);
	    				Ext.Ajax.request({
	    					url: basePath + 'common/dump/imp.action',
	    					params: {
	    						jsonData: Ext.JSON.encode(me.dumpfile)
	    					},
	    					callback: function(opt, success, res) {
	    						me.FormUtil.setLoading(false);
	    						var rs = Ext.JSON.decode(res.responseText);
	    						if(rs.success) {
	    							showMessage('提示', '导入成功');
	    							/*panel.close();*/
	    						} else if(rs.exceptionInfo) {
	    							showError(rs.exceptionInfo);
	    						}
    						}
    					});
    				}
    			},{
	    			text: '取消',
	    			handler: function(){
	    				var tabpanel = parent.Ext.getCmp('content-panel');
	    				tabpanel.getActiveTab().close();
	    			}
				}]
			}]
		});
		me.callParent(arguments); 
	},
	listeners: {
		afterrender: function() {
			var me = this, panel = me.down('panel');
			var confirmBtn = panel.down('button[itemId=confirm]');
	    	// 本地读取解析文件
	    	panel.down('filefield').fileInputEl.dom.addEventListener('change', function(e){
	    		e = e || window.event;
	    	    var reader = new FileReader(), file = this.files[0];
	    	    if(!/^.+\.json$/.test(file.name)) {
	    	    	showError('文件格式错误');
	    	    	confirmBtn.setDisabled(true);
	    	    	return;
	    	    }
		        reader.onload = (function(file) {
		            return function(e) {
		            	try {
		            		var dump = Ext.JSON.decode(this.result), form = panel.down('form').getForm();
		            		form.setValues(dump);
		            		form.getFields().each(function(){
		            			this.show();
		            		});
		            		me.dumpfile = dump;
		            		confirmBtn.setDisabled(false);
		            	} catch (err) {
		            		showError('文件错误，无法解析');
		            		confirmBtn.setDisabled(true);
		            	}
		            };
		        })(file);
		        //读取文件内容
		        reader.readAsText(file);
	    	}, false);
		}
	}
});