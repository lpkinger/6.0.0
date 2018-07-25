/**
 * 转8D报告
 */	
Ext.define('erp.view.core.button.TurnT8DReport',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnT8DReportButton',
		iconCls: 'x-button-icon-delete',
		cls: 'x-btn-gray',
    	id: 'erpTurnT8DReportButton',
    	text: $I18N.common.button.erpTurnT8DReportButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});