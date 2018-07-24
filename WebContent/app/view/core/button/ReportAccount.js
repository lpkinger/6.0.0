/**
 * 报表计算
 */
Ext.define('erp.view.core.button.ReportAccount',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpReportAccountButton',
		param: [],
		id: 'erpReportAccountButton',
		text: $I18N.common.button.erpReportAccountButton,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 90,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});