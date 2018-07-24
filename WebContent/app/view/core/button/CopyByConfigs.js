/**
 * 通用复制按钮，通过复制方案
 */	
Ext.define('erp.view.core.button.CopyByConfigs',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCopyByConfigsButton',
		iconCls: 'x-button-icon-copy',
    	cls: 'x-btn-gray',
		width: 80,
    	text: $I18N.common.button.erpCopyByConfigsButton,
    	requires: ['erp.util.FormUtil'],
		initComponent : function(){ 
			var me=this;
			 me.FormUtil = Ext.create('erp.util.FormUtil');
			this.callParent(arguments); 
		},
		handler: function(btn){
			var me=this;
			var form= btn.ownerCt.ownerCt;
			var keyValue=Ext.getCmp(form.keyField).value;
			var _copyConf="%7BkeyValue:"+Ext.getCmp(form.keyField).value+"%7D";
			var url=window.location.href;
			var copyUrl=url.substring(url.indexOf('jsps/'),url.indexOf('?'))+"?whoami="+ caller+"&_copyConf="+_copyConf;
			var main = parent.Ext.getCmp("content-panel");
			me.FormUtil.onAdd('copy' + caller, '单据复制', copyUrl);
		}
	});