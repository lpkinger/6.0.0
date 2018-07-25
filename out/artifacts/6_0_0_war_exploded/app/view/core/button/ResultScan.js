/**
 * 结果查看
 */
Ext.define('erp.view.core.button.ResultScan',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResultScanButton',
		param: [],
		id: 'resultscan',
		text: $I18N.common.button.erpResultScanButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	width: 80,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});