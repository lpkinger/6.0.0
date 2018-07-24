/**
 * 报表文件设置按钮
 */	
Ext.define('erp.view.core.button.ReportFiles',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpReportFilesButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	id: 'reportfiles',
    	text: $I18N.common.button.erpReportFilesButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});