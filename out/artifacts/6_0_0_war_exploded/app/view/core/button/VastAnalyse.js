/**
 * 缺料分析按钮
 */	
Ext.define('erp.view.core.button.VastAnalyse',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastAnalyseButton',
		text: $I18N.common.button.erpVastAnalyseButton,
    	iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 90,
    	id: 'erpVastAnalyseButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});