Ext.QuickTips.init();
Ext.define('erp.controller.ma.encryptConfig', {
	extend: 'Ext.app.Controller',
	views: [
	        'ma.encryptConfig'
	],
	init:function(){
		var me = this;
		this.control({
			'encryptConfig': {
				afterrender: function(me){
					Ext.Ajax.request({
						url: basePath + 'ma/encrypt/getConfigs.action',
						callback: function(options, success, response){
							var res = Ext.decode(response.responseText);
							if(!res.success){
								Ext.Msg.alert('提示',res.message);
								Ext.getCmp('confirm').setDisabled(true);
								if(res.configs == 1)
									Ext.getCmp('encrypt').setValue(true);
								else
									Ext.getCmp('decrypt').setValue(true);
							}else{
								if(res.configs){
									if(res.configs == 1){
										Ext.getCmp('encrypt').setValue(true);
										Ext.getCmp('decrypt').setDisabled(true);
										Ext.getCmp('confirm').setDisabled(true);
									}else{
										Ext.getCmp('decrypt').setValue(true);
									}
								}else{
									Ext.getCmp('decrypt').setValue(true);
								}
							}
						}
					});
				}
			},
			'button[id=confirm]': {
				click: function(btn){
					var encrypt = Ext.getCmp('encrypt'),
						decrypt = Ext.getCmp('decrypt'),
						value;
					if(encrypt.getValue() && decrypt.getValue()){
						Ext.Msg.alert('提示','请不要同时勾选"是"和"否"!');
						return false;
					}else if(!(encrypt.getValue() || decrypt.getValue())){
						Ext.Msg.alert('提示','请勾选"是"或"否"!');
						return false;
					}else{
						if(encrypt.getValue())
							value = encrypt.inputValue;
						else
							value = decrypt.inputValue;
					}
					Ext.Ajax.timeout = 240000;
					if(value == '1')
						Ext.getCmp('enctyptConfig').getEl().mask("正在将密码加密...");
					else
						Ext.getCmp('enctyptConfig').getEl().mask();
					Ext.Ajax.request({
						url: basePath + 'ma/encrypt/updateConfigs.action',
						params: {
							value: value
						},
						callback: function(options, success, response){
							Ext.getCmp('enctyptConfig').getEl().unmask();
							var res = Ext.decode(response.responseText);
							if(res.success)
								Ext.Msg.alert('提示','保存成功!',function(){
									document.location.reload();
								});
							if(res.exceptionInfo){
								showError(res.exceptionInfo);
								return false;
							}
						}
					});
				}
			}
		});
	}
});